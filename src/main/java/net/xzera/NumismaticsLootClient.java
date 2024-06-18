package net.xzera;

import com.google.common.collect.ImmutableSet;
//import io.wispforest.lavender.book.*;
//import io.wispforest.owo.ui.component.Components;
import io.wispforest.lavender.book.Entry;
import io.wispforest.owo.ui.component.Components;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Blocks;
import net.xzera.book.BookContentRegistry;
import net.xzera.currency.Currency;
import net.xzera.registry.DropInfo;

import java.nio.file.Path;
import java.util.HashMap;

import static net.xzera.NumismaticsLoot.MOD_ID;
import static net.xzera.book.Formatting.HORIZONTAL_RULES;
import static net.xzera.book.Formatting.PAGE_BREAK;

public class NumismaticsLootClient implements ClientModInitializer {

	private static final ResourceLocation BOOK_ID = new ResourceLocation(MOD_ID, "reward_book");
	private static final ResourceLocation HUNTING_CATEGORY_ID = new ResourceLocation(MOD_ID, "hunting_category");
	private static final ResourceLocation TREASURE_CATEGORY_ID = new ResourceLocation(MOD_ID, "treasure_category");

	@Override
	public void onInitializeClient() {
		ClientPlayNetworking.registerGlobalReceiver(NumismaticsLoot.ENTITY_DROP_INFO_ID, (client, handler, buf, responseSender) -> {
			ResourceLocation resourceLocation = buf.readResourceLocation();
			DropInfo dropInfo = new DropInfo(buf.readInt(), buf.readFloat());

			EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.get(resourceLocation);

			String content = getEntityContent(resourceLocation, dropInfo);

			Entry bookEntry = new Entry(resourceLocation,
					HUNTING_CATEGORY_ID,
					entityType.getDescription().getString(),
					sizing -> {
						assert client.level != null;
						return Components.entity(sizing, entityType.create(client.level));
					},
					false, dropInfo.getReward(), ImmutableSet.of(), ImmutableSet.of(), content);

			BookContentRegistry.addEntry(BOOK_ID, bookEntry);
		});

		ClientPlayNetworking.registerGlobalReceiver(NumismaticsLoot.CHEST_DROP_INFO_ID, (client, handler, buf, responseSender) -> {
			ResourceLocation resourceLocation = buf.readResourceLocation();
			DropInfo dropInfo = new DropInfo(buf.readInt(), buf.readFloat());

			String content = getChestContent(dropInfo);

			String title = Path.of(resourceLocation.getPath()).getFileName().toString().strip().replace("_", " ");
			if (title.length() > 0) {
				title = String.valueOf(title.charAt(0)).toUpperCase() + title.substring(1);
			}

			Entry bookEntry = new Entry(resourceLocation,
					TREASURE_CATEGORY_ID,
					title,
					sizing -> Components.block(Blocks.CHEST.defaultBlockState()).sizing(sizing),
					false, dropInfo.getReward(), ImmutableSet.of(), ImmutableSet.of(), content);

			BookContentRegistry.addEntry(BOOK_ID, bookEntry);
		});

		ClientPlayNetworking.registerGlobalReceiver(NumismaticsLoot.CLEAR_DROP_INFO_ID, (client, handler, buf, responseSender) -> BookContentRegistry.clear());
	}

	String getEntityContent(ResourceLocation resourceLocation, DropInfo dropInfo) {
		String content = String.format("<entity;%s>", resourceLocation.toString());
		content += HORIZONTAL_RULES;

		content += getDropInfoContent(dropInfo);

		return content;
	}

	String getChestContent(DropInfo dropInfo) {
		String content = "<block;minecraft:chest>";
		content += HORIZONTAL_RULES;

		content += getDropInfoContent(dropInfo);

		return content;
	}

	String getDropInfoContent(DropInfo dropInfo) {
		StringBuilder content = new StringBuilder(String.format("- Reward: %d\n", dropInfo.getReward()));
		if (dropInfo.getDropRate() != 1.0) {
			content.append(String.format(dropInfo.getDropRate() < 0.01 ? "- Drop rate: %.2f%%\n" : "- Drop rate: %.0f%%\n", dropInfo.getDropRate() * 100.0));
		}

		content.append(PAGE_BREAK);

		HashMap<Currency, Integer> coins = Currency.fromValue(dropInfo.getReward());

		for (Currency coin : Currency.values()) {
			int value = coins.get(coin);

			if (value == 0)
				continue;

			content.append(String.format("<item;%s>", coin.getIdentifier()));
			content.append(String.format("%s", value));
		}

		return content.toString();
	}
}
