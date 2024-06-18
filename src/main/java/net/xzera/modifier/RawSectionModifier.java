package net.xzera.modifier;

import com.moandjiezana.toml.Toml;

import net.minecraft.resources.ResourceLocation;
import net.xzera.config.Exception.DeserializationException;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class RawSectionModifier extends Modifier {
    private static final ResourceLocation DEFAULT_IDENTIFIER = new ResourceLocation("minecraft", "empty");
    private ResourceLocation lootIdentifier;
    private boolean ignoreNamespace;

    public RawSectionModifier(ResourceLocation lootIdentifier, ModificationTarget target, ModificationType type, float value) {
        super(target, type, value);
        setLootIdentifier(lootIdentifier);
    }

    public RawSectionModifier(ResourceLocation lootIdentifier, ModificationTarget target) {
        super(target);
        this.lootIdentifier = lootIdentifier;
    }

    public RawSectionModifier(ResourceLocation lootIdentifier) {
        super();
        this.lootIdentifier = lootIdentifier;
    }

    public RawSectionModifier() {
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
        if (!lootIdentifier.getNamespace().equals(lootTableIdentifier.getNamespace()) && !ignoreNamespace)
            return false;

        String safelyTerminatedPath = lootIdentifier.getPath();
        if (!safelyTerminatedPath.endsWith("/"))
            safelyTerminatedPath += "/";

        return lootTableIdentifier.getPath().startsWith(safelyTerminatedPath);
    }

    @Override
    public Map<String, Object> serialize(boolean includeParent) {
        Map<String, Object> map = new HashMap<>();
        map.put("section-ID", ignoreNamespace ? lootIdentifier.getPath() : lootIdentifier.toString());

        if (includeParent)
            map.putAll(ReadOnlyModifier.serialize(this));

        return map;
    }

    @Override
    public void deserialize(Toml toml) throws DeserializationException {
        if (!toml.contains("section-ID")) {
            throw new DeserializationException("Failed to deserialize raw section modifier: section-ID field is missing");
        }

        String id = toml.getString("section-ID");

        this.ignoreNamespace = !id.contains(":");

		ResourceLocation identifier = ResourceLocation.tryParse(toml.getString("section-ID"));

        if (identifier == null) {
            throw new DeserializationException("Failed to deserialize raw section modifier: section-ID doesn't have the right format, the right format is namespace:unix-path or unix-path");
        }

        this.lootIdentifier = identifier;
        super.deserialize(toml);
    }

}
