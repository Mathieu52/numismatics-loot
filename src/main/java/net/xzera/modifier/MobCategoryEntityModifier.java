package net.xzera.modifier;

import com.moandjiezana.toml.Toml;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.xzera.config.Exception.DeserializationException;

import java.util.HashMap;
import java.util.Map;

public class MobCategoryEntityModifier extends Modifier {
    private static final MobCategory DEFAULT_SPAWN_GROUP = null;
	private static final String KEY = "category";

	private MobCategory mobCategory;

    public static final HashMap<ResourceLocation, EntityType<? extends Entity>> LOOT_TABLE_ID_TO_ENTITY_TYPE = new HashMap<>();

    static {
        for (EntityType<? extends Entity> entityType : BuiltInRegistries.ENTITY_TYPE) {
            LOOT_TABLE_ID_TO_ENTITY_TYPE.putIfAbsent(entityType.getDefaultLootTable(), entityType);
        }
    }

	public MobCategoryEntityModifier(MobCategory mobCategory, ModificationTarget target, ModificationType type, float value) {
        super(target, type, value);
        setMobCategory(mobCategory);
    }

    public MobCategoryEntityModifier(MobCategory mobCategory, ModificationTarget target) {
        super(target);
        setMobCategory(mobCategory);
    }

    public MobCategoryEntityModifier(MobCategory mobCategory) {
        super();
        this.mobCategory = mobCategory;
    }

    public MobCategoryEntityModifier() {
        this(DEFAULT_SPAWN_GROUP);
    }

    public MobCategory getMobCategory() {
        return mobCategory;
    }

    public void setMobCategory(MobCategory mobCategory) {
        this.mobCategory = mobCategory;
    }

    @Override
    public boolean matchesModifier(ResourceLocation lootTableIdentifier) {
        EntityType<?> type = LOOT_TABLE_ID_TO_ENTITY_TYPE.get(lootTableIdentifier);

        if (type == null)
            return false;

        return type.getCategory() == getMobCategory();
    }

    @Override
    public Map<String, Object> serialize(boolean includeParent) {
        Map<String, Object> map = new HashMap<>();
        map.put(KEY, mobCategory.getName());

        if (includeParent)
            map.putAll(ReadOnlyModifier.serialize(this));

        return map;
    }

    private static String validOptions() {
        StringBuilder options = new StringBuilder("[");

        boolean first = true;
        for (MobCategory category : MobCategory.values()) {
            if (first)
                first = false;
            else
                options.append(", ");

            options.append(category.getName());
        }

        options.append(']');

        return options.toString();
    }

    @Override
    public void deserialize(Toml toml) throws DeserializationException {
        if (!toml.contains(KEY)) {
            throw new DeserializationException("Failed to deserialize mob category modifier: " + KEY + " field is missing. Valid options are: " + validOptions());
        }

        String strValue = toml.getString(KEY);

        this.mobCategory = null;
        for (MobCategory category : MobCategory.values()) {
            if (strValue.equals(category.getName())) {
                this.mobCategory = category;
                super.deserialize(toml);
                return;
            }
        }

        throw new DeserializationException("Failed to deserialize mob category modifier: " + strValue + " isn't a valid " + KEY + " option. Valid options are: " + validOptions());
    }
}
