package net.xzera.config;

import com.moandjiezana.toml.Toml;
import net.xzera.config.Exception.DeserializationException;

import java.util.HashMap;
import java.util.Map;

public class DropRateConfig implements ConfigSerializable {
    private static final String KEY = "default-drop-rate";
    private static final float DEFAULT_DROP_RATE = 0.0f;
    private float baseDropRate;

    public DropRateConfig(float baseDropRate) {
        this.setBaseDropRate(baseDropRate);
    }

    public DropRateConfig() {
        this(DEFAULT_DROP_RATE);
    }

    public float getBaseDropRate() {
        return baseDropRate;
    }

    public boolean isValidDropRate(float value) {
        return value >= 0 && value <= 1;
    }

    public void setBaseDropRate(float baseDropRate) throws IllegalArgumentException {
        if (!isValidDropRate(baseDropRate))
            throw new IllegalArgumentException("Invalid drop rate: " + baseDropRate + ", must be between 0 and 1 (0% and 100%)");

        this.baseDropRate = baseDropRate;
    }

    @Override
    public Map<String, Object> serialize(boolean includeParent) {
        Map<String, Object> data = new HashMap<>();
        data.put(KEY, this.getBaseDropRate());

        return data;
    }

    @Override
    public void deserialize(Toml toml) throws DeserializationException {
        try {
            this.setBaseDropRate(toml.getDouble(KEY, (double) DEFAULT_DROP_RATE).floatValue());
        } catch (IllegalArgumentException e) {
            throw new DeserializationException(e.getMessage());
        }
    }
}
