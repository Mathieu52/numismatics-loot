package net.xzera.mixin;


import com.simibubi.create.content.kinetics.deployer.DeployerBlockEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DeployerBlockEntity.class)
public class DeployerBlockEntityMixin {
	@Inject(method = "initHandler", at = @At("HEAD"), remap = false)
	private void initHandler(CallbackInfo info) {
		DeployerBlockEntity instance = (DeployerBlockEntity) (Object) this;
		((DeployerBlockEntityAccessor) instance).setOwner(null);
	}
}
