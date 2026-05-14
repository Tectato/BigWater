package bigwater.mixin;

import bigwater.BigWater;
import bigwater.access.FluidRendererAccess;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.caffeinemc.mods.sodium.client.model.color.ColorProvider;
import net.caffeinemc.mods.sodium.client.model.light.LightMode;
import net.caffeinemc.mods.sodium.client.model.light.LightPipeline;
import net.caffeinemc.mods.sodium.client.model.quad.ModelQuadViewMutable;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.buffers.ChunkModelBuilder;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.DefaultFluidRenderer;
import net.caffeinemc.mods.sodium.client.render.chunk.terrain.material.Material;
import net.caffeinemc.mods.sodium.client.render.chunk.translucent_sorting.TranslucentGeometryCollector;
import net.caffeinemc.mods.sodium.client.world.LevelSlice;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(DefaultFluidRenderer.class)
public class SodiumFluidRendererMixin implements FluidRendererAccess {
    @Unique
    BlockPos bigwater$pos;

    @Unique
    FluidState bigwater$state;

    @Unique
    Vec3 bigwater$flow;

    @Unique
    Direction bigwater$direction;

    @Override
    public void bigwater$setPos(BlockPos pos){this.bigwater$pos = pos;}

    @Override
    public BlockPos bigwater$getPos() {return bigwater$pos;}

    @Override
    public void bigwater$setFluidState(FluidState state){this.bigwater$state = state;}

    @Override
    public FluidState bigwater$getFluidState(){return bigwater$state;}

    @Override
    public void bigwater$setFlow(Vec3 flow){this.bigwater$flow = flow;}

    @Override
    public Vec3 bigwater$getFlow(){return bigwater$flow;}

    @Override
    public void bigwater$setDirection(Direction dir){
        bigwater$direction = dir;}

    @Override
    public Direction bigwater$getDirection(){return bigwater$direction;}


    @Inject(
            at = @At(
                    value = "HEAD"
            ),
            method = "render(Lnet/caffeinemc/mods/sodium/client/world/LevelSlice;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/material/FluidState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;Lnet/caffeinemc/mods/sodium/client/render/chunk/translucent_sorting/TranslucentGeometryCollector;Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/buffers/ChunkModelBuilder;Lnet/caffeinemc/mods/sodium/client/render/chunk/terrain/material/Material;Lnet/caffeinemc/mods/sodium/client/model/color/ColorProvider;Lnet/minecraft/client/renderer/block/FluidModel;)V"
    )
    public void renderHeadInject(LevelSlice level, BlockState blockState, FluidState fluidState, BlockPos blockPos, BlockPos offset, TranslucentGeometryCollector collector, ChunkModelBuilder meshBuilder, Material material, ColorProvider<FluidState> colorProvider, FluidModel sprites, CallbackInfo ci){
        bigwater$setPos(blockPos);
        bigwater$setFluidState(fluidState);
        bigwater$setFlow(fluidState.getFlow(level, blockPos));
    }

    @Inject(
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/core/BlockPos$MutableBlockPos;setWithOffset(Lnet/minecraft/core/Vec3i;Lnet/minecraft/core/Direction;)Lnet/minecraft/core/BlockPos$MutableBlockPos;",
                    ordinal = 5
            ),
            method = "render(Lnet/caffeinemc/mods/sodium/client/world/LevelSlice;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/material/FluidState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;Lnet/caffeinemc/mods/sodium/client/render/chunk/translucent_sorting/TranslucentGeometryCollector;Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/buffers/ChunkModelBuilder;Lnet/caffeinemc/mods/sodium/client/render/chunk/terrain/material/Material;Lnet/caffeinemc/mods/sodium/client/model/color/ColorProvider;Lnet/minecraft/client/renderer/block/FluidModel;)V",
            locals = LocalCapture.CAPTURE_FAILSOFT
    )
    public void renderDirInject(LevelSlice level, BlockState blockState, FluidState fluidState, BlockPos blockPos, BlockPos offset, TranslucentGeometryCollector collector, ChunkModelBuilder meshBuilder, Material material, ColorProvider<FluidState> colorProvider, FluidModel sprites, CallbackInfo ci, @Local Direction dir){
            bigwater$setDirection(dir);
    }

    @ModifyExpressionValue(
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/resources/model/sprite/Material$Baked;sprite()Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;"
            ),
            method = "render(Lnet/caffeinemc/mods/sodium/client/world/LevelSlice;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/material/FluidState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;Lnet/caffeinemc/mods/sodium/client/render/chunk/translucent_sorting/TranslucentGeometryCollector;Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/buffers/ChunkModelBuilder;Lnet/caffeinemc/mods/sodium/client/render/chunk/terrain/material/Material;Lnet/caffeinemc/mods/sodium/client/model/color/ColorProvider;Lnet/minecraft/client/renderer/block/FluidModel;)V"
    )
    public TextureAtlasSprite spriteReturnInject(TextureAtlasSprite original){
        TextureAtlasSprite sprite = BigWater.getTexture(original.contents().name().toString());
        if(sprite == null) return original;
        return sprite;
    }
}
