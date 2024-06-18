package net.xzera.modifier;

import com.moandjiezana.toml.Toml;
import net.minecraft.resources.ResourceLocation;
import net.xzera.config.Exception.DeserializationException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class AllModifier extends Modifier {

    public AllModifier(ModificationTarget target, ModificationType type, float value) {
        super(target, type, value);
    }

    public AllModifier(ModificationTarget target) {
        super(target);
    }

    public AllModifier() {
    }

    @Override
    public boolean matchesModifier(ResourceLocation lootTableIdentifier) {
        return true;
    }

    @Override
    public Map<String, Object> serialize(boolean includeParent) {
        return ReadOnlyModifier.serialize(this);
    }

    @Override
    public void deserialize(Toml toml) throws DeserializationException {
        super.deserialize(toml);
    }
}
