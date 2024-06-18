package net.xzera.modifier;

import net.minecraft.resources.ResourceLocation;
import net.xzera.config.ConfigSerializable;

import java.util.HashMap;
import java.util.Map;

public abstract class ReadOnlyModifier implements ConfigSerializable {

    protected ModificationTarget target;
    protected ModificationType type;

    protected float value;

    public ModificationTarget getTarget() {
        return target;
    }

    public ModificationType getType() {
        return type;
    }

    public float getValue() {
        return value;
    }

    public float applyEffect(float value) {
        if (type == ModificationType.REPLACE) {
            return getValue();
        }

        return switch (target) {
            case REWARD -> value * getValue();
            case DROP_RATE -> multiplyProbability(value, getValue());
        };
    }

    private static float multiplyProbability(float percentage, float factor) {
        return 1f - (float) Math.pow(1f - percentage, factor);
    }

    public abstract boolean matchesModifier(ResourceLocation lootTableIdentifier);

    protected static String getNamingScheme(ModificationTarget target, ModificationType type) {
        if (type == ModificationType.NONE)
            return null;

        String targetStr = switch (target) {
            case REWARD -> "reward";
            case DROP_RATE -> "drop-rate";
        };

        String action = switch (type) {
            case REPLACE -> "set";
            case MODIFY -> "multiply";
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };

        return action + "-" + targetStr;
    }

    public static Map<String, Object> serialize(ReadOnlyModifier modifier) {
        Map<String, Object> map = new HashMap<>();

        String key = getNamingScheme(modifier.target, modifier.type);

        if (key != null) {
            map.put(getNamingScheme(modifier.target, modifier.type), modifier.value);
        }

        return map;
    }

    @Override
    public Map<String, Object> serialize(boolean includeParent) {
        Map<String, Object> map = new HashMap<>();

        String key = getNamingScheme(this.target, this.type);

        if (key != null) {
            map.put(getNamingScheme(this.target, this.type), this.value);
        }

        return map;
    }
}
