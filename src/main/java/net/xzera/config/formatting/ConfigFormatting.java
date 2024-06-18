package net.xzera.config.formatting;

import net.xzera.modifier.*;

import java.util.HashMap;

public class ConfigFormatting {
    public static final HashMap<Class<? extends Modifier>, String> MODIFIER_TO_HEADER = new HashMap<>();
    public static final HashMap<String, Class<? extends Modifier>> HEADER_TO_MODIFIER = new HashMap<>();

    static {
        MODIFIER_TO_HEADER.put(EntityModifier.class, "entity");
        MODIFIER_TO_HEADER.put(MobCategoryEntityModifier.class, "mob-category");
        MODIFIER_TO_HEADER.put(RawModifier.class, "table");
        MODIFIER_TO_HEADER.put(RawSectionModifier.class, "section");

        for (Class<? extends Modifier> modifier : MODIFIER_TO_HEADER.keySet()) {
            HEADER_TO_MODIFIER.put(MODIFIER_TO_HEADER.get(modifier), modifier);
        }
    }
}
