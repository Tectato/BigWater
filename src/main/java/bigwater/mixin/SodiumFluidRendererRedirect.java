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
import net.minecraft.util.Tuple;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

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
    private void writeQuadRedirect(DefaultFluidRenderer instance, ChunkModelBuilder builder, TranslucentGeometryCollector collector, Material material, BlockPos offset, ModelQuadView quad, ModelQuadFacing facing, boolean flip) {
        Vec3 flow = ((FluidRendererAccess)instance).getFlow();
        if(flow.x != 0.0d || flow.z != 0.0d){
            writeQuad(builder, collector, material, offset, quad, facing, flip);
            return;
            /* TODO: handle flowing textures properly
             * - side faces need to change mapping to be across X/Y or Z/Y
             * - top faces need to rotate mapping depending on flow direction
             */
        }
        FluidState state = ((FluidRendererAccess)instance).getFluidState();
        String id = state.getType().builtInRegistryHolder().getRegisteredName();
        Tuple<Integer, Float> scaleData = BigWater.getTextureScale(id);
        int textureScale = scaleData.getA();
        float scalant = scaleData.getB();

        BlockPos pos = ((FluidRendererAccess)instance).getPos();
        int xPos = pos.getX() % textureScale;
        if (xPos < 0) xPos = textureScale + xPos;
        xPos = (textureScale - xPos) - 1; // idk why but this is reversed in sodium for some reason
        int zPos = pos.getZ() % textureScale;
        if (zPos < 0) zPos = textureScale + zPos;
        float uMin = quad.getTexU(0);
        float vMin = quad.getTexV(0);
        float uMax = quad.getTexU(2);
        float vMax = quad.getTexV(2);
        float width = uMax - uMin;
        float height = vMax - vMin;
        ModelQuadViewMutable quadMutable = (ModelQuadViewMutable)quad;
        quadMutable.setTexU(0, modCoord(quad.getTexU(0),xPos,uMin,width,scalant));
        quadMutable.setTexV(0, modCoord(quad.getTexV(0),zPos,vMin,height,scalant));
        quadMutable.setTexU(1, modCoord(quad.getTexU(1),xPos,uMin,width,scalant));
        quadMutable.setTexV(1, modCoord(quad.getTexV(1),zPos,vMin,height,scalant));
        quadMutable.setTexU(2, modCoord(quad.getTexU(2),xPos,uMin,width,scalant));
        quadMutable.setTexV(2, modCoord(quad.getTexV(2),zPos,vMin,height,scalant));
        quadMutable.setTexU(3, modCoord(quad.getTexU(3),xPos,uMin,width,scalant));
        quadMutable.setTexV(3, modCoord(quad.getTexV(3),zPos,vMin,height,scalant));
        writeQuad(builder, collector, material, offset, quad, facing, flip);
        //addFace(builder, x0, y0, z0, modCoord(u0,xPos,uMin,width,scalant), modCoord(v0,zPos,vMin,height,scalant), x1, y1, z1, modCoord(u1,xPos,uMin,width,scalant), modCoord(v1,zPos,vMin,height,scalant), x2, y2, z2, modCoord(u2,xPos,uMin,width,scalant), modCoord(v2,zPos,vMin,height,scalant), x3, y3, z3, modCoord(u3,xPos,uMin,width,scalant), modCoord(v3,zPos,vMin,height,scalant), color, lightCoords, addBackFace);
    }

    private float modCoord(float src, int relativePos, float origin, float sideLength, float scalant){
        return (((src + (sideLength * relativePos) - origin) * scalant)) + origin;
    }

    @Shadow
    private void writeQuad(ChunkModelBuilder builder, TranslucentGeometryCollector collector, Material material, BlockPos offset, ModelQuadView quad, ModelQuadFacing facing, boolean flip){}
}
