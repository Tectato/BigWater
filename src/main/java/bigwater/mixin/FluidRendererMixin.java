package bigwater.mixin;

import bigwater.BigWater;
import bigwater.access.FluidRendererAccess;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.block.FluidRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
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
            method = "tesselate(Lnet/minecraft/client/renderer/block/BlockAndTintGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/client/renderer/block/FluidRenderer$Output;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/material/FluidState;)V"
    )
    public void tesselateHeadInject(BlockAndTintGetter level, BlockPos pos, FluidRenderer.Output output, BlockState blockState, FluidState fluidState, CallbackInfo ci){
        bigwater$setPos(pos);
        bigwater$setFluidState(fluidState);
        bigwater$setFlow(fluidState.getFlow(level, pos));
    }

    @Inject(
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/block/FluidRenderer;isFaceOccludedByNeighbor(Lnet/minecraft/core/Direction;FLnet/minecraft/world/level/block/state/BlockState;)Z",
                    ordinal = 2
            ),
            method = "tesselate(Lnet/minecraft/client/renderer/block/BlockAndTintGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/client/renderer/block/FluidRenderer$Output;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/material/FluidState;)V",
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
                                        Iterator<?> var40,
                                        Direction faceDir
                                     ){
        bigwater$setDirection(faceDir);
    }

    @ModifyExpressionValue(
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/resources/model/sprite/Material$Baked;sprite()Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;"
            ),
            method = "tesselate(Lnet/minecraft/client/renderer/block/BlockAndTintGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/client/renderer/block/FluidRenderer$Output;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/material/FluidState;)V"
    )
    public TextureAtlasSprite spriteReturnInject(TextureAtlasSprite original){
        TextureAtlasSprite sprite = BigWater.getTexture(original.contents().name().toString());
        if(sprite == null) return original;
        return sprite;
    }
}
