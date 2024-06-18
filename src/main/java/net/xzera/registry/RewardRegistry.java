package net.xzera.registry;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.xzera.NumismaticsLoot;

import java.util.HashMap;
import java.util.Map;

public class RewardRegistry {
    public static final Map<ResourceLocation, DropInfo> ENTITY = new HashMap<>();
    public static final Map<ResourceLocation, DropInfo> CHEST = new HashMap<>();

    public static boolean register(ResourceLocation resourceLocation, DropInfo dropInfo) {
        if (NumismaticsLoot.isEntity(resourceLocation)) {
            RewardRegistry.register(NumismaticsLoot.getEntity(resourceLocation), dropInfo);
            return true;
        }

        if (NumismaticsLoot.isChest(resourceLocation)) {
            CHEST.put(resourceLocation, dropInfo);
            return true;
        }

        return false;
    }

    public static void register(EntityType<?> entity, DropInfo dropInfo) {
        ENTITY.put(BuiltInRegistries.ENTITY_TYPE.getKey(entity), dropInfo);
    }
}
