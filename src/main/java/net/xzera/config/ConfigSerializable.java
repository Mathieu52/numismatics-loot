package net.xzera.config;

import com.moandjiezana.toml.Toml;
import net.xzera.config.Exception.DeserializationException;

import java.util.Map;

public interface ConfigSerializable {
    default Map<String, Object> serialize() {
        return serialize(true);
    }
    Map<String, Object> serialize(boolean includeParent);
    void deserialize(Toml toml) throws DeserializationException;
}
