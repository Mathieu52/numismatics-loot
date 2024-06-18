package net.xzera;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.advancements.critereon.DamageSourcePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.PlayerPredicate;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.LootingEnchantFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.DamageSourceCondition;
import net.minecraft.world.level.storage.loot.providers.number.BinomialDistributionGenerator;
import net.xzera.config.NumismaticsLootConfig;
import net.xzera.currency.Currency;
import net.xzera.modifier.Modifier;
import net.xzera.modifier.MobCategoryEntityModifier;
import net.xzera.registry.DropInfo;
import net.xzera.registry.RewardRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NumismaticsLoot implements ModInitializer {
    public static String MOD_ID = "numismatics-loot";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("numismatics-loot-server.toml");
    public static final NumismaticsLootConfig CONFIG = NumismaticsLootConfig.load(LOGGER, CONFIG_PATH);

    public static final ResourceLocation CLEAR_DROP_INFO_ID = new ResourceLocation(MOD_ID, "clear_drop_info");
    public static final ResourceLocation ENTITY_DROP_INFO_ID = new ResourceLocation(MOD_ID, "entity_drop_info");
    public static final ResourceLocation CHEST_DROP_INFO_ID = new ResourceLocation(MOD_ID, "chest_drop_info");

    @Override
    public void onInitialize() {
		LOGGER.info("Initializing...");

        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
			LOGGER.info("Registering: " + id.toString());
            updateLootTables(id, tableBuilder);
        });

        ServerPlayConnectionEvents.JOIN.register((networkHandler, packetSender, server) -> {
			LOGGER.info("Sending info to client");
            packetSender.sendPacket(CLEAR_DROP_INFO_ID, PacketByteBufs.create());

            for (Map.Entry<ResourceLocation, DropInfo> entry : RewardRegistry.ENTITY.entrySet()) {
				FriendlyByteBuf buf = PacketByteBufs.create();
                buf.writeResourceLocation(entry.getKey());
                buf.writeInt(entry.getValue().getReward());
                buf.writeFloat(entry.getValue().getDropRate());

                packetSender.sendPacket(ENTITY_DROP_INFO_ID, buf);
            }

            for (Map.Entry<ResourceLocation, DropInfo> entry : RewardRegistry.CHEST.entrySet()) {
                FriendlyByteBuf buf = PacketByteBufs.create();
                buf.writeResourceLocation(entry.getKey());
                buf.writeInt(entry.getValue().getReward());
                buf.writeFloat(entry.getValue().getDropRate());

                packetSender.sendPacket(CHEST_DROP_INFO_ID, buf);
            }
        });
    }
    public static EntityType<?> getEntity(ResourceLocation resourceLocation) {
        return MobCategoryEntityModifier.LOOT_TABLE_ID_TO_ENTITY_TYPE.get(resourceLocation);
    }

    public static boolean isEntity(ResourceLocation resourceLocation) {
        return MobCategoryEntityModifier.LOOT_TABLE_ID_TO_ENTITY_TYPE.containsKey(resourceLocation);
    }

    public static boolean isChest(ResourceLocation resourceLocation) {
        return !isEntity(resourceLocation);
    }

    private void updateLootTables(ResourceLocation id, LootTable.Builder tableBuilder) {
        EntityType<?> entityType = MobCategoryEntityModifier.LOOT_TABLE_ID_TO_ENTITY_TYPE.get(id);

        float reward = switch (CONFIG.getRewardConfig().getMode()) {
            case HEALTH_REWARD_RATIO -> getEntityValue(entityType) * CONFIG.getRewardConfig().getValue();
            case DEFAULT_REWARD -> CONFIG.getRewardConfig().getValue();
        };
        float dropRate = CONFIG.getDropRateConfig().getBaseDropRate();

        for (ModifierConfig modifierConfig : CONFIG.getModifiers()) {
            if (modifierConfig.isEnabled() && modifierConfig.getModifier().matchesModifier(id)) {
                Modifier modifier = modifierConfig.getModifier();

                switch (modifier.getTarget()) {
                    case REWARD -> reward = modifier.applyEffect(reward);
                    case DROP_RATE -> dropRate = modifier.applyEffect(dropRate);
                }
            }
        }

        if (dropRate != 0 && reward != 0) {
            RewardRegistry.register(id, new DropInfo((int) reward, dropRate));
            addLoot(tableBuilder, (int) reward, dropRate);
        }
    }

    public static void addLoot(LootTable.Builder tableBuilder, int reward, float dropRate) {
        ArrayList<LootPool> entries = new ArrayList<>();

        HashMap<Currency, Integer> result = Currency.fromValue(reward);

        for (Currency coin : result.keySet()) {
            LootPoolEntryContainer.Builder<?> entry = generateEntry(coin.asItem(), result.get(coin), dropRate, CONFIG.getLootingConfig().isAllowed());

            LootPool.Builder builder = LootPool.lootPool().with(entry.build());

            if (CONFIG.getPlayerKillConfig().isAllowOnlyPlayerKillDrop()) {
				PlayerPredicate player;
				if (CONFIG.getPlayerKillConfig().isAllowDeployerKillDrop()) {
					player = PlayerPredicate.Builder.player().build();
				} else {
					player = PlayerPredicate.Builder.player().checkAdvancementDone(new ResourceLocation(MOD_ID, "root"), true).build();
				}
				EntityPredicate entityPredicate = EntityPredicate.Builder.entity().subPredicate(player).build();
				builder.conditionally(DamageSourceCondition.hasDamageSource(DamageSourcePredicate.Builder.damageType().source(entityPredicate)).build());
            }

            entries.add(builder.build());
        }

        tableBuilder.pools(entries);
    }

    public static int getEntityValue(EntityType<?> type) {
        if (type != null) {
			AttributeSupplier attributes = DefaultAttributes.getSupplier((EntityType<? extends LivingEntity>) type);

            if (attributes != null) {
                return (int) attributes.getValue(Attributes.MAX_HEALTH);
            }
        }

        return 0;
    }

    public static LootPoolEntryContainer.Builder<?> generateEntry(Item item, int quantity, float dropRate, boolean looting) {

        BinomialDistributionGenerator quantityProvider = BinomialDistributionGenerator.binomial(quantity, dropRate);

		LootPoolSingletonContainer.Builder<?> entryBuilder = LootItem.lootTableItem(item);
        entryBuilder.apply(SetItemCountFunction.setCount(quantityProvider));

        if (looting) {
            entryBuilder.apply(LootingEnchantFunction.lootingMultiplier(quantityProvider));
        }

        return entryBuilder;
    }
}
