package net.xzera.modifier;

import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.xzera.config.ConfigSerializable;
import net.xzera.config.Exception.DeserializationException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class EntityModifier extends Modifier {
    private static final EntityType<?> DEFAULT_ENTITY_TYPE = EntityType.ZOMBIE;
    private EntityType<?> entityType;

    public EntityModifier(EntityType<?> entityType, ModificationTarget target, ModificationType type, float value) {
        super(target, type, value);
        setEntityType(entityType);
    }

    public EntityModifier(EntityType<?> entityType, ModificationTarget target) {
        super(target);
        this.entityType = entityType;
    }

    public EntityModifier(EntityType<?> entityType) {
        super();
        this.entityType = entityType;
    }

    public EntityModifier() {
        this(DEFAULT_ENTITY_TYPE);
    }

    public EntityType<?> getEntityType() {
        return entityType;
    }

    public void setEntityType(EntityType<?> entityType) {
        this.entityType = entityType;
    }
    @Override
    public boolean matchesModifier(ResourceLocation lootTableIdentifier) {
        if (entityType == null)
            return false;

        return entityType.getDefaultLootTable().equals(lootTableIdentifier);
    }

    @Override
    public Map<String, Object> serialize(boolean includeParent) {
        Map<String, Object> map = new HashMap<>();
        map.put("entity", EntityType.getKey(entityType).toString());

        if (includeParent)
            map.putAll(ReadOnlyModifier.serialize(this));

        return map;
    }

    @Override
    public void deserialize(Toml toml) throws DeserializationException {
        if (!toml.contains("entity")) {
            throw new DeserializationException("Failed to deserialize entity modifier: entity field is missing.");
        }

        Optional<EntityType<?>> entityType = EntityType.byString(toml.getString("entity"));

        if (entityType.isEmpty())
            throw new DeserializationException("Failed to deserialize entity modifier: entity ID doesn't correspond to any entity.");

        this.entityType = entityType.get();
        super.deserialize(toml);
    }
}
