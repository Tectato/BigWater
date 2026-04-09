package bigwater.mixin;

import bigwater.access.FluidRendererAccess;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.block.FluidRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.CardinalLighting;
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

import java.util.Iterator;

@Mixin(FluidRenderer.class)
public class FluidRendererMixin implements FluidRendererAccess {
    @Unique
    BlockPos pos;

    @Unique
    FluidState state;

    @Unique
    Vec3 flow;

    @Unique
    Direction direction;

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

    @Override
    public void setDirection(Direction dir){direction = dir;}

    @Override
    public Direction getDirection(){return direction;}


    @Inject(
            at = @At(
                    value = "HEAD"
            ),
            method = "Lnet/minecraft/client/renderer/block/FluidRenderer;tesselate(Lnet/minecraft/client/renderer/block/BlockAndTintGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/client/renderer/block/FluidRenderer$Output;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/material/FluidState;)V"
    )
    public void tesselateHeadInject(BlockAndTintGetter level, BlockPos pos, FluidRenderer.Output output, BlockState blockState, FluidState fluidState, CallbackInfo ci){
        setPos(pos);
        setFluidState(fluidState);
        setFlow(fluidState.getFlow(level, pos));
    }

    @Inject(
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/block/FluidRenderer;isFaceOccludedByNeighbor(Lnet/minecraft/core/Direction;FLnet/minecraft/world/level/block/state/BlockState;)Z",
                    ordinal = 2
            ),
            method = "Lnet/minecraft/client/renderer/block/FluidRenderer;tesselate(Lnet/minecraft/client/renderer/block/BlockAndTintGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/client/renderer/block/FluidRenderer$Output;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/material/FluidState;)V",
            locals = LocalCapture.CAPTURE_FAILSOFT
    )
    public void isFaceOccludedInject(BlockAndTintGetter level, BlockPos pos, FluidRenderer.Output output, BlockState blockState, FluidState fluidState, CallbackInfo ci,
                                        BlockState blockStateDown,
                                        FluidState fluidStateDown,
                                        BlockState blockStateUp,
                                        FluidState fluidStateUp,
                                        BlockState blockStateNorth,
                                        FluidState fluidStateNorth,
                                        BlockState blockStateSouth,
                                        FluidState fluidStateSouth,
                                        BlockState blockStateWest,
                                        FluidState fluidStateWest,
                                        BlockState blockStateEast,
                                        FluidState fluidStateEast,
                                        boolean renderUp,
                                        boolean renderDown,
                                        boolean renderNorth,
                                        boolean renderSouth,
                                        boolean renderWest,
                                        boolean renderEast,
                                        FluidModel model,
                                        VertexConsumer builder,
                                        int tintColor,
                                        CardinalLighting cardinalLighting,
                                        Fluid type,
                                        float heightNorthEast,
                                        float heightNorthWest,
                                        float heightSouthEast,
                                        float heightSouthWest,
                                        float heightSelf,
                                        float x,
                                        float y,
                                        float z,
                                        float offs,
                                        float bottomOffs,
                                        int sideLightCoords,
                                        Iterator var40,
                                        Direction faceDir
                                     ){
        setDirection(faceDir);
    }
}
