package cjminecraft.doubleslabs.fabric.mixin.client;

import cjminecraft.doubleslabs.fabric.api.client.IDynamicSlabRenderContext;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(RenderContext.class)
public interface RenderContextMixin extends IDynamicSlabRenderContext {

}
