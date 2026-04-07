package bigwater.mixin;

import bigwater.access.FluidRendererAccess;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.FluidRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FluidRenderer.class)
public class FluidRendererMixin implements FluidRendererAccess {
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
            method = "Lnet/minecraft/client/renderer/block/FluidRenderer;tesselate(Lnet/minecraft/client/renderer/block/BlockAndTintGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/client/renderer/block/FluidRenderer$Output;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/material/FluidState;)V"
    )
    public void tesselateInject(BlockAndTintGetter level, BlockPos pos, FluidRenderer.Output output, BlockState blockState, FluidState fluidState, CallbackInfo ci){
        setPos(pos);
        setFluidState(fluidState);
        setFlow(fluidState.getFlow(level, pos));
    }
}
