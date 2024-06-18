package net.xzera.modifier;

import com.moandjiezana.toml.Toml;
import net.minecraft.resources.ResourceLocation;
import net.xzera.config.Exception.DeserializationException;

import java.util.HashMap;
import java.util.Map;

public class RawModifier extends Modifier {
    private static final ResourceLocation DEFAULT_IDENTIFIER = new ResourceLocation("minecraft", "empty");
    private ResourceLocation lootIdentifier;

    public RawModifier(ResourceLocation lootIdentifier, ModificationTarget target, ModificationType type, float value) {
        super(target, type, value);
        setLootIdentifier(lootIdentifier);
    }

    public RawModifier(ResourceLocation lootIdentifier, ModificationTarget target) {
        super(target);
        this.lootIdentifier = lootIdentifier;
    }

    public RawModifier(ResourceLocation lootIdentifier) {
        super();
        this.lootIdentifier = lootIdentifier;
    }

    public RawModifier() {
        this(DEFAULT_IDENTIFIER);
    }

    public ResourceLocation getLootIdentifier() {
        return lootIdentifier;
    }

    public void setLootIdentifier(ResourceLocation lootIdentifier) {
        this.lootIdentifier = lootIdentifier;
    }

    @Override
    public boolean matchesModifier(ResourceLocation lootTableIdentifier) {
        return lootIdentifier.equals(lootTableIdentifier);
    }

    @Override
    public Map<String, Object> serialize(boolean includeParent) {
        Map<String, Object> map = new HashMap<>();
        map.put("loot-ID", lootIdentifier.toString());

        if (includeParent)
            map.putAll(ReadOnlyModifier.serialize(this));

        return map;
    }

    @Override
    public void deserialize(Toml toml) throws DeserializationException {
        if (!toml.contains("loot-ID")) {
            throw new DeserializationException("Failed to deserialize raw modifier: loot-ID field is missing");
        }

		ResourceLocation identifier = ResourceLocation.tryParse(toml.getString("loot-ID"));

        if (identifier == null) {
            throw new DeserializationException("Failed to deserialize raw modifier: loot-ID doesn't have the right format, the right format is namespace:unix-path");
        }

        this.lootIdentifier = identifier;
        super.deserialize(toml);
    }
}
