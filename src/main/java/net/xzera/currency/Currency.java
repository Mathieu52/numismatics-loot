package net.xzera.currency;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.*;

public enum Currency implements Comparable<Currency> {
    SPUR(new ResourceLocation("numismatics", "spur"), 1),
    BEVEL(new ResourceLocation("numismatics", "bevel"), 8),
    SPROCKET(new ResourceLocation("numismatics", "sprocket"), 16),
    COG(new ResourceLocation("numismatics", "cog"), 64),
    CROWN(new ResourceLocation("numismatics", "crown"), 512),
    SUN(new ResourceLocation("numismatics", "sun"), 4096);

    private static final Currency[] orderedCurrency = Arrays.copyOf(Currency.values(), Currency.values().length);

    static {
        Arrays.sort(orderedCurrency, (x, y) -> Float.compare(x.getValue(), y.getValue()));
    }

    private final ResourceLocation identifier;
    private final int value;

    Currency(ResourceLocation identifier, int value) {
        this.identifier = identifier;
        this.value = value;
    }

    public ResourceLocation getIdentifier() {
        return identifier;
    }

    public Item asItem() {
        return BuiltInRegistries.ITEM.get(this.identifier);
    }

    public int getValue() {
        return this.value;
    }

    public static HashMap<Currency, Integer> fromValue(int value) {
        HashMap<Currency, Integer> result = new HashMap<>();

        for (int i = orderedCurrency.length - 1; i >= 0; i--) {
            Currency coin = orderedCurrency[i];
            int coveredValue = Math.floorDiv(value, coin.getValue());

            result.put(coin, coveredValue);

            value -= coveredValue * coin.getValue();
        }

        return result;
    }

    public static int getValue(Map<Currency, Integer> coins) {
        int value = 0;

        for (Currency coin : coins.keySet()) {
            value += coin.getValue() * coins.get(coin);
        }

        return value;
    }
}
