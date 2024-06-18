package net.xzera.config;

import com.moandjiezana.toml.Toml;
import net.xzera.config.Exception.DeserializationException;

import java.util.HashMap;
import java.util.Map;

public class LootingConfig implements ConfigSerializable {
    private static final String KEY = "allow-looting";
    private static final boolean DEFAULT_ALLOWED_VALUE = false;
    private boolean allowed;

    public LootingConfig(boolean allowed) {
        this.setAllowed(allowed);
    }

    public LootingConfig() {
        this(DEFAULT_ALLOWED_VALUE);
    }

    public boolean isAllowed() {
        return allowed;
    }

    public void setAllowed(boolean allowed) {
        this.allowed = allowed;
    }

    //default-drop-rate
    @Override
    public Map<String, Object> serialize(boolean includeParent) {
        Map<String, Object> data = new HashMap<>();
        data.put(KEY, this.isAllowed());

        return data;
    }

    @Override
    public void deserialize(Toml toml) throws DeserializationException {
        this.setAllowed(toml.getBoolean(KEY, DEFAULT_ALLOWED_VALUE));
    }
}
