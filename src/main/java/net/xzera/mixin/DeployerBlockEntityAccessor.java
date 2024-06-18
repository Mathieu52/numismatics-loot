package net.xzera.mixin;

import com.simibubi.create.content.kinetics.deployer.DeployerBlockEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.UUID;

@Mixin(DeployerBlockEntity.class)
public interface DeployerBlockEntityAccessor {
    @Accessor("owner")
    void setOwner(UUID owner);
}
