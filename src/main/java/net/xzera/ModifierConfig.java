package net.xzera;

import com.moandjiezana.toml.Toml;
import net.xzera.config.formatting.ConfigFormatting;
import net.xzera.config.ConfigSerializable;
import net.xzera.config.Exception.DeserializationException;
import net.xzera.modifier.Modifier;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class ModifierConfig implements ConfigSerializable {

    private static final boolean DEFAULT_ENABLED_MODIFIER = true;

    private Modifier modifier;
    private boolean enabled;

    public ModifierConfig(Modifier modifier, boolean enabled) {
        this.modifier = modifier;
        this.enabled = enabled;
    }

    public ModifierConfig(Modifier modifier) {
        this(modifier, DEFAULT_ENABLED_MODIFIER);
    }

    public Modifier getModifier() {
        return modifier;
    }

    public void setModifier(Modifier modifier) {
        this.modifier = modifier;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public Map<String, Object> serialize(boolean includeParent) {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("enabled", enabled);

        data.put(ConfigFormatting.MODIFIER_TO_HEADER.get(modifier.getClass()), modifier.serialize(includeParent));
        return data;
    }

    private static String validOptions() {
        StringBuilder options = new StringBuilder("[");

        boolean first = true;
        for (String header : ConfigFormatting.MODIFIER_TO_HEADER.values()) {
            if (first)
                first = false;
            else
                options.append(", ");

            options.append(header);
        }

        options.append(']');

        return options.toString();
    }


    @Override
    public void deserialize(Toml toml) throws DeserializationException {
        Class<? extends Modifier> instanceClass = null;
        Toml modifierToml = null;
        for (Class<? extends Modifier> modifierClass : ConfigFormatting.MODIFIER_TO_HEADER.keySet()) {
            String key = ConfigFormatting.MODIFIER_TO_HEADER.get(modifierClass);
            if (toml.contains(key)) {
                instanceClass = modifierClass;
                modifierToml = toml.getTable(key);
                break;
            }
        }

        if (instanceClass == null) {
            throw new DeserializationException("Failed to deserialize modifier config: modifier field is missing. Valid options are: " + validOptions());
        }

        try {
            modifier = instanceClass.getDeclaredConstructor().newInstance();
            modifier.deserialize(modifierToml);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException e) {
            throw new DeserializationException("Failed to deserialize modifier config: an unexpected issue happened during construction");
        }
    }
}
