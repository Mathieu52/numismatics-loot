package net.xzera.config;

import com.moandjiezana.toml.Toml;
import net.xzera.config.Exception.DeserializationException;

import java.util.HashMap;
import java.util.Map;

public class PlayerKillConfig implements ConfigSerializable {
    private static final String KEY = "only-drop-when-killed-by-player";
    private static final boolean DEFAULT_VALUE = false;
    private boolean onlyDropWhenKilledByPlayer;

    public PlayerKillConfig(boolean onlyDropWhenKilledByPlayer) {
        this.setOnlyDropWhenKilledByPlayer(onlyDropWhenKilledByPlayer);
    }

    public PlayerKillConfig() {
        this(DEFAULT_VALUE);
    }

    public boolean isOnlyDropWhenKilledByPlayer() {
        return onlyDropWhenKilledByPlayer;
    }

    public void setOnlyDropWhenKilledByPlayer(boolean onlyDropWhenKilledByPlayer) {
        this.onlyDropWhenKilledByPlayer = onlyDropWhenKilledByPlayer;
    }

    //default-drop-rate
    @Override
    public Map<String, Object> serialize(boolean includeParent) {
        Map<String, Object> data = new HashMap<>();
        data.put(KEY, this.isOnlyDropWhenKilledByPlayer());

        return data;
    }

    @Override
    public void deserialize(Toml toml) throws DeserializationException {
        this.setOnlyDropWhenKilledByPlayer(toml.getBoolean(KEY, DEFAULT_VALUE));
    }
}
