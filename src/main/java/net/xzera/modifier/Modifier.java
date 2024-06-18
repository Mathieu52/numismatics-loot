package net.xzera.modifier;

import com.moandjiezana.toml.Toml;
import net.xzera.config.Exception.DeserializationException;

public abstract class Modifier extends ReadOnlyModifier {
    public static ModificationTarget DEFAULT_MODIFICATION_TARGET = ModificationTarget.REWARD;
    public static ModificationType DEFAULT_MODIFICATION_TYPE = ModificationType.NONE;
    public static float DEFAULT_MODIFICATION_VALUE = 1;

    public Modifier(ModificationTarget target, ModificationType type, float value) {
        this.setTarget(target);
        this.setType(type);
        this.setValue(value);
    }

    public Modifier(ModificationTarget target) {
        this(target, DEFAULT_MODIFICATION_TYPE, DEFAULT_MODIFICATION_VALUE);
    }

    public Modifier() {
        this(DEFAULT_MODIFICATION_TARGET);
    }

    public void setTarget(ModificationTarget target) {
        this.target = target;
    }

    public void setType(ModificationType type) {
        this.type = type;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public void deserialize(Toml toml) throws DeserializationException {
        for (ModificationTarget target : ModificationTarget.values()) {
            for (ModificationType type : ModificationType.values()) {
                String key = getNamingScheme(target, type);

                if (key == null)
                    continue;

                if (toml.contains(key)) {
                    this.target = target;
                    this.type = type;
                    this.value = toml.getDouble(key).floatValue();
                    return;
                }
            }
        }

        this.target = DEFAULT_MODIFICATION_TARGET;
        this.type = DEFAULT_MODIFICATION_TYPE;
        this.value = DEFAULT_MODIFICATION_VALUE;
    }
}
