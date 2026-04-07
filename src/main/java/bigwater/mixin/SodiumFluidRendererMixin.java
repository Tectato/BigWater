package bigwater.mixin;

import bigwater.access.FluidRendererAccess;
import net.caffeinemc.mods.sodium.client.model.color.ColorProvider;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.buffers.ChunkModelBuilder;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.DefaultFluidRenderer;
import net.caffeinemc.mods.sodium.client.render.chunk.terrain.material.Material;
import net.caffeinemc.mods.sodium.client.render.chunk.translucent_sorting.TranslucentGeometryCollector;
import net.caffeinemc.mods.sodium.client.world.LevelSlice;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.block.FluidRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DefaultFluidRenderer.class)
public class SodiumFluidRendererMixin implements FluidRendererAccess {
    @Unique
    BlockPos pos;

    @Unique
    FluidState state;

    @Unique
    Vec3 flow;

    @Override
    public void setPos(BlockPos pos){this.pos = pos;}

    @Override
    public BlockPos getPos() {return pos;}

    @Override
    public void setFluidState(FluidState state){this.state = state;}

    @Override
    public FluidState getFluidState(){return state;}

    @Override
    public void setFlow(Vec3 flow){this.flow = flow;}

    @Override
    public Vec3 getFlow(){return flow;}


    @Inject(
            at = @At(
                    value = "HEAD"
            ),
            method = "Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/pipeline/DefaultFluidRenderer;render(Lnet/caffeinemc/mods/sodium/client/world/LevelSlice;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/material/FluidState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;Lnet/caffeinemc/mods/sodium/client/render/chunk/translucent_sorting/TranslucentGeometryCollector;Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/buffers/ChunkModelBuilder;Lnet/caffeinemc/mods/sodium/client/render/chunk/terrain/material/Material;Lnet/caffeinemc/mods/sodium/client/model/color/ColorProvider;Lnet/minecraft/client/renderer/block/FluidModel;)V"
    )
    public void renderInject(LevelSlice level, BlockState blockState, FluidState fluidState, BlockPos blockPos, BlockPos offset, TranslucentGeometryCollector collector, ChunkModelBuilder meshBuilder, Material material, ColorProvider<FluidState> colorProvider, FluidModel sprites, CallbackInfo ci){
        setPos(blockPos);
        setFluidState(fluidState);
        setFlow(fluidState.getFlow(level, blockPos));
    }
}
