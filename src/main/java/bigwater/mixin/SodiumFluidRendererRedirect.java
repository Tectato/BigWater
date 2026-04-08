package bigwater.mixin;

import bigwater.BigWater;
import bigwater.access.FluidRendererAccess;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.caffeinemc.mods.sodium.client.model.quad.ModelQuadView;
import net.caffeinemc.mods.sodium.client.model.quad.ModelQuadViewMutable;
import net.caffeinemc.mods.sodium.client.model.quad.properties.ModelQuadFacing;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.buffers.ChunkModelBuilder;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.DefaultFluidRenderer;
import net.caffeinemc.mods.sodium.client.render.chunk.terrain.material.Material;
import net.caffeinemc.mods.sodium.client.render.chunk.translucent_sorting.TranslucentGeometryCollector;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.block.FluidRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(DefaultFluidRenderer.class)
abstract class SodiumFluidRendererRedirect {
    @Environment(EnvType.CLIENT)

    @Redirect(
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/pipeline/DefaultFluidRenderer;writeQuad(Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/buffers/ChunkModelBuilder;Lnet/caffeinemc/mods/sodium/client/render/chunk/translucent_sorting/TranslucentGeometryCollector;Lnet/caffeinemc/mods/sodium/client/render/chunk/terrain/material/Material;Lnet/minecraft/core/BlockPos;Lnet/caffeinemc/mods/sodium/client/model/quad/ModelQuadView;Lnet/caffeinemc/mods/sodium/client/model/quad/properties/ModelQuadFacing;Z)V",
                    ordinal = 0
            ),
            method = "Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/pipeline/DefaultFluidRenderer;render(Lnet/caffeinemc/mods/sodium/client/world/LevelSlice;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/material/FluidState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;Lnet/caffeinemc/mods/sodium/client/render/chunk/translucent_sorting/TranslucentGeometryCollector;Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/buffers/ChunkModelBuilder;Lnet/caffeinemc/mods/sodium/client/render/chunk/terrain/material/Material;Lnet/caffeinemc/mods/sodium/client/model/color/ColorProvider;Lnet/minecraft/client/renderer/block/FluidModel;)V"
    )
    private void writeTopQuadRedirect(DefaultFluidRenderer instance, ChunkModelBuilder builder, TranslucentGeometryCollector collector, Material material, BlockPos offset, ModelQuadView quad, ModelQuadFacing facing, boolean flip) {
        FluidState state = ((FluidRendererAccess)instance).getFluidState();
        String id = state.getType().builtInRegistryHolder().getRegisteredName();
        Tuple<Integer, Float> scaleData = BigWater.getTextureScale(id);
        int textureScale = scaleData.getA();
        BlockPos pos = ((FluidRendererAccess)instance).getPos();
        boolean mirrorU = false;
        boolean mirrorV = false;
        Vec3 flow = ((FluidRendererAccess)instance).getFlow();
        int posU;
        int posV;
        if (flow.x != 0.0d || flow.z != 0.0d) {
            mirrorU = true;
            //mirrorV = false;
        }
        if (Math.abs(flow.x) > 0.5d) {
            posU = pos.getZ();
            posV = pos.getX();
        } else if (flow.x != 0.0d && flow.z != 0.0d) {
            posU = (int) (pos.getX() + (Math.signum(flow.z)) * pos.getZ());
            posV = (int) (pos.getZ() + (Math.signum(flow.x)) * pos.getX());
        } else {
            posU = pos.getX();
            posV = pos.getZ();
        }
        writeFlatQuad(instance, builder, collector, material, offset, quad, facing, flip, getTexPos(posU, textureScale, true ^ mirrorU), getTexPos(posV, textureScale, false ^ mirrorV), scaleData);
    }

    @Redirect(
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/pipeline/DefaultFluidRenderer;writeQuad(Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/buffers/ChunkModelBuilder;Lnet/caffeinemc/mods/sodium/client/render/chunk/translucent_sorting/TranslucentGeometryCollector;Lnet/caffeinemc/mods/sodium/client/render/chunk/terrain/material/Material;Lnet/minecraft/core/BlockPos;Lnet/caffeinemc/mods/sodium/client/model/quad/ModelQuadView;Lnet/caffeinemc/mods/sodium/client/model/quad/properties/ModelQuadFacing;Z)V",
                    ordinal = 2
            ),
            method = "Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/pipeline/DefaultFluidRenderer;render(Lnet/caffeinemc/mods/sodium/client/world/LevelSlice;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/material/FluidState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;Lnet/caffeinemc/mods/sodium/client/render/chunk/translucent_sorting/TranslucentGeometryCollector;Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/buffers/ChunkModelBuilder;Lnet/caffeinemc/mods/sodium/client/render/chunk/terrain/material/Material;Lnet/caffeinemc/mods/sodium/client/model/color/ColorProvider;Lnet/minecraft/client/renderer/block/FluidModel;)V"
    )
    private void writeBottomQuadRedirect(DefaultFluidRenderer instance, ChunkModelBuilder builder, TranslucentGeometryCollector collector, Material material, BlockPos offset, ModelQuadView quad, ModelQuadFacing facing, boolean flip) {
        FluidState state = ((FluidRendererAccess)instance).getFluidState();
        String id = state.getType().builtInRegistryHolder().getRegisteredName();
        Tuple<Integer, Float> scaleData = BigWater.getTextureScale(id);
        int textureScale = scaleData.getA();
        BlockPos pos = ((FluidRendererAccess)instance).getPos();
        writeFlatQuad(instance, builder, collector, material, offset, quad, facing, flip, getTexPos(pos.getX(), textureScale, false), getTexPos(pos.getZ(), textureScale, true), scaleData);
    }

    @Redirect(
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/pipeline/DefaultFluidRenderer;writeQuad(Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/buffers/ChunkModelBuilder;Lnet/caffeinemc/mods/sodium/client/render/chunk/translucent_sorting/TranslucentGeometryCollector;Lnet/caffeinemc/mods/sodium/client/render/chunk/terrain/material/Material;Lnet/minecraft/core/BlockPos;Lnet/caffeinemc/mods/sodium/client/model/quad/ModelQuadView;Lnet/caffeinemc/mods/sodium/client/model/quad/properties/ModelQuadFacing;Z)V",
                    ordinal = 4
            ),
            method = "Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/pipeline/DefaultFluidRenderer;render(Lnet/caffeinemc/mods/sodium/client/world/LevelSlice;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/material/FluidState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/BlockPos;Lnet/caffeinemc/mods/sodium/client/render/chunk/translucent_sorting/TranslucentGeometryCollector;Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/buffers/ChunkModelBuilder;Lnet/caffeinemc/mods/sodium/client/render/chunk/terrain/material/Material;Lnet/caffeinemc/mods/sodium/client/model/color/ColorProvider;Lnet/minecraft/client/renderer/block/FluidModel;)V"
    )
    private void writeSideQuadRedirect(DefaultFluidRenderer instance, ChunkModelBuilder builder, TranslucentGeometryCollector collector, Material material, BlockPos offset, ModelQuadView quad, ModelQuadFacing facing, boolean flip) {
        FluidRendererAccess accessor = ((FluidRendererAccess)instance);
        Direction dir = accessor.getDirection();
        FluidState state = accessor.getFluidState();
        String id = state.getType().builtInRegistryHolder().getRegisteredName();
        Tuple<Integer, Float> scaleData = BigWater.getTextureScale(id);
        int textureScale = scaleData.getA();
        BlockPos pos = accessor.getPos();
        int uPos = 0;
        int vPos = 0;
        switch (dir){
            case Direction.NORTH:
                uPos = getTexPos(pos.getX(), textureScale, true);
                vPos = getTexPos(pos.getY(), textureScale, true);
                break;
            case Direction.SOUTH:
                uPos = getTexPos(pos.getX(), textureScale, false);
                vPos = getTexPos(pos.getY(), textureScale, true);
                break;
            case Direction.EAST:
                uPos = getTexPos(pos.getZ(), textureScale, true);
                vPos = getTexPos(pos.getY(), textureScale, true);
                break;
            case Direction.WEST:
                uPos = getTexPos(pos.getZ(), textureScale, false);
                vPos = getTexPos(pos.getY(), textureScale, true);
                break;
            default:
                BigWater.LOGGER.info("Invalid Direction: " + dir);
                break;
        }
        writeFlatQuad(instance, builder, collector, material, offset, quad, facing, flip, uPos, vPos, scaleData);
    }




    private int getTexPos(int worldPos, int textureScale, boolean reverseCoords){
        int texPos = worldPos % textureScale;
        if (texPos < 0) texPos = textureScale + texPos;
        if (reverseCoords) texPos = BigWater.reverseCoord(texPos, textureScale);
        return texPos;
    }


    private void writeFlatQuad(DefaultFluidRenderer instance, ChunkModelBuilder builder, TranslucentGeometryCollector collector, Material material, BlockPos offset, ModelQuadView quad, ModelQuadFacing facing, boolean flip, int uPos, int vPos, Tuple<Integer, Float> scaleData) {
        /* TODO: handle flowing textures properly
         * - side faces need to change mapping to be across X/Y or Z/Y
         * - top faces need to rotate mapping depending on flow direction
         */
        /*if(flow.x != 0.0d || flow.z != 0.0d){
            writeQuad(builder, collector, material, offset, quad, facing, flip);
            return;
        }*/

        float scalant = scaleData.getB();
        float uMin = quad.getTexU(0);
        float vMin = quad.getTexV(0);
        float uMax = quad.getTexU(2);
        float vMax = quad.getTexV(2);
        float width = uMax - uMin;
        float height = vMax - vMin;
        ModelQuadViewMutable quadMutable = (ModelQuadViewMutable)quad;
        quadMutable.setTexU(0, BigWater.modCoord(quad.getTexU(0),uPos,uMin,width,scalant));
        quadMutable.setTexV(0, BigWater.modCoord(quad.getTexV(0),vPos,vMin,height,scalant));
        quadMutable.setTexU(1, BigWater.modCoord(quad.getTexU(1),uPos,uMin,width,scalant));
        quadMutable.setTexV(1, BigWater.modCoord(quad.getTexV(1),vPos,vMin,height,scalant));
        quadMutable.setTexU(2, BigWater.modCoord(quad.getTexU(2),uPos,uMin,width,scalant));
        quadMutable.setTexV(2, BigWater.modCoord(quad.getTexV(2),vPos,vMin,height,scalant));
        quadMutable.setTexU(3, BigWater.modCoord(quad.getTexU(3),uPos,uMin,width,scalant));
        quadMutable.setTexV(3, BigWater.modCoord(quad.getTexV(3),vPos,vMin,height,scalant));
        writeQuad(builder, collector, material, offset, quad, facing, flip);
    }

    @Shadow
    private void writeQuad(ChunkModelBuilder builder, TranslucentGeometryCollector collector, Material material, BlockPos offset, ModelQuadView quad, ModelQuadFacing facing, boolean flip){}
}
