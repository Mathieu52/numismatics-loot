package net.xzera.config;

import com.moandjiezana.toml.Toml;
import net.xzera.config.Exception.DeserializationException;

import java.util.HashMap;
import java.util.Map;

public class RewardConfig implements ConfigSerializable {
    private static final RewardMode DEFAULT_REWARD_MODE = RewardMode.HEALTH_REWARD_RATIO;

    private RewardMode mode;
    private float value;

    public RewardConfig(RewardMode mode, float value) {
        this.mode = mode;
        this.value = value;
    }

    public RewardConfig(RewardMode mode) {
        this(mode, mode.getDefault());
    }

    public RewardConfig() {
        this(DEFAULT_REWARD_MODE);
    }

    public RewardMode getMode() {
        return mode;
    }

    public void setMode(RewardMode mode) {
        this.mode = mode;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    //health-reward-ratio
    //default-reward
    protected static String getNamingScheme(RewardMode mode) {
        return switch (mode) {
            case HEALTH_REWARD_RATIO -> "health-reward-ratio";
            case DEFAULT_REWARD -> "default-reward";
        };
    }

    @Override
    public Map<String, Object> serialize(boolean includeParent) {
        Map<String, Object> data = new HashMap<>();
        data.put(getNamingScheme(this.getMode()), this.getValue());

        return data;
    }

    @Override
    public void deserialize(Toml toml) throws DeserializationException {
        boolean found = false;
        for (RewardMode mode : RewardMode.values()) {
            String key = getNamingScheme(mode);
            if (toml.contains(key)) {
                if (found)
                    throw new DeserializationException("Failed to deserialize default reward config: you cannot specify more than one option, they are mutually exclusive");

                this.mode = mode;
                this.value = toml.getDouble(key).floatValue();
                found = true;
            }
        }

        if (!found) {
            this.mode = DEFAULT_REWARD_MODE;
            this.value = DEFAULT_REWARD_MODE.getDefault();
        }
    }
}
