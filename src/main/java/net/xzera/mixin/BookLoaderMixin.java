package net.xzera.mixin;

import io.wispforest.lavender.book.BookContentLoader;
import net.minecraft.server.packs.resources.ResourceManager;
import net.xzera.book.BookContentRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BookContentLoader.class)
public class BookLoaderMixin {
    @Inject(method = "reloadContents", at = @At("RETURN"))
    private static void reload(ResourceManager manager, CallbackInfo ci) {
        BookContentRegistry.reloadContent();
    }
}
