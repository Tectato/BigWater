package bigwater.mixin;

import bigwater.BigWater;
import bigwater.access.FluidRendererAccess;
import com.mojang.blaze3d.vertex.VertexConsumer;
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

import static bigwater.BigWater.getTexPos;

@Mixin(FluidRenderer.class)
abstract class FluidRendererRedirect {
	@Environment(EnvType.CLIENT)

	@Redirect(
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/renderer/block/FluidRenderer;addFace(Lcom/mojang/blaze3d/vertex/VertexConsumer;FFFFFFFFFFFFFFFFFFFFIIZ)V",
					ordinal = 0
			),
			method = "Lnet/minecraft/client/renderer/block/FluidRenderer;tesselate(Lnet/minecraft/client/renderer/block/BlockAndTintGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/client/renderer/block/FluidRenderer$Output;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/material/FluidState;)V"
	)
	private void addTopFaceRedirect(FluidRenderer instance, VertexConsumer builder, float x0, float y0, float z0, float u0, float v0, float x1, float y1, float z1, float u1, float v1, float x2, float y2, float z2, float u2, float v2, float x3, float y3, float z3, float u3, float v3, int color, int lightCoords, boolean addBackFace) {
		FluidState state = ((FluidRendererAccess)instance).getFluidState();
		String id = state.getType().builtInRegistryHolder().getRegisteredName();
		Tuple<Integer, Float> scaleData = BigWater.getTextureScale(id);
		int textureScale = scaleData.getA();
		BlockPos pos = ((FluidRendererAccess)instance).getPos();
		boolean mirrorU = false;
		boolean mirrorV = false;
		Vec3 flow = ((FluidRendererAccess)instance).getFlow();
		int uPos;
		int vPos;
		if (flow.x != 0.0d || flow.z != 0.0d) { // Flowing
			mirrorU = true;
			mirrorV = false;
		} else { // Still
			mirrorU = true;
		}
		if (Math.abs(flow.x) > 0.5d) {
			uPos = pos.getZ();
			vPos = pos.getX();
		} else if (flow.x != 0.0d && flow.z != 0.0d) {
			uPos = (int) (pos.getX() + (Math.signum(flow.z)) * pos.getZ());
			vPos = (int) (pos.getZ() + (Math.signum(flow.x)) * pos.getX());
		} else {
			uPos = pos.getX();
			vPos = pos.getZ();
		}
		addFaceMod(instance, builder, x0, y0, z0, u0, v0, x1, y1, z1, u1, v1, x2, y2, z2, u2, v2, x3, y3, z3, u3, v3, color, lightCoords, addBackFace, getTexPos(uPos, textureScale, true ^ mirrorU), getTexPos(vPos, textureScale, false ^ mirrorV), scaleData);
	}

	@Redirect(
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/renderer/block/FluidRenderer;addFace(Lcom/mojang/blaze3d/vertex/VertexConsumer;FFFFFFFFFFFFFFFFFFFFIIZ)V",
					ordinal = 1
			),
			method = "Lnet/minecraft/client/renderer/block/FluidRenderer;tesselate(Lnet/minecraft/client/renderer/block/BlockAndTintGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/client/renderer/block/FluidRenderer$Output;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/material/FluidState;)V"
	)
	private void addBottomFaceRedirect(FluidRenderer instance, VertexConsumer builder, float x0, float y0, float z0, float u0, float v0, float x1, float y1, float z1, float u1, float v1, float x2, float y2, float z2, float u2, float v2, float x3, float y3, float z3, float u3, float v3, int color, int lightCoords, boolean addBackFace) {
		FluidState state = ((FluidRendererAccess)instance).getFluidState();
		String id = state.getType().builtInRegistryHolder().getRegisteredName();
		Tuple<Integer, Float> scaleData = BigWater.getTextureScale(id);
		int textureScale = scaleData.getA();
		BlockPos pos = ((FluidRendererAccess)instance).getPos();
		addFaceMod(instance, builder, x0, y0, z0, u0, v0, x1, y1, z1, u1, v1, x2, y2, z2, u2, v2, x3, y3, z3, u3, v3, color, lightCoords, addBackFace, getTexPos(pos.getX(), textureScale, false), getTexPos(pos.getZ(), textureScale, true), scaleData);
	}


	@Redirect(
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/renderer/block/FluidRenderer;addFace(Lcom/mojang/blaze3d/vertex/VertexConsumer;FFFFFFFFFFFFFFFFFFFFIIZ)V",
					ordinal = 2
			),
			method = "Lnet/minecraft/client/renderer/block/FluidRenderer;tesselate(Lnet/minecraft/client/renderer/block/BlockAndTintGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/client/renderer/block/FluidRenderer$Output;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/material/FluidState;)V"
	)
	private void writeSideQuadRedirect(FluidRenderer instance, VertexConsumer builder, float x0, float y0, float z0, float u0, float v0, float x1, float y1, float z1, float u1, float v1, float x2, float y2, float z2, float u2, float v2, float x3, float y3, float z3, float u3, float v3, int color, int lightCoords, boolean addBackFace) {
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
				uPos = getTexPos(pos.getX(), textureScale, false);
				vPos = getTexPos(pos.getY(), textureScale, true);
				break;
			case Direction.SOUTH:
				uPos = getTexPos(pos.getX(), textureScale, true);
				vPos = getTexPos(pos.getY(), textureScale, true);
				break;
			case Direction.EAST:
				uPos = getTexPos(pos.getZ(), textureScale, false);
				vPos = getTexPos(pos.getY(), textureScale, true);
				break;
			case Direction.WEST:
				uPos = getTexPos(pos.getZ(), textureScale, true);
				vPos = getTexPos(pos.getY(), textureScale, true);
				break;
			default:
				BigWater.LOGGER.info("Invalid Direction: " + dir);
				break;
		}
		addFaceMod(instance, builder, x0, y0, z0, u0, v0, x1, y1, z1, u1, v1, x2, y2, z2, u2, v2, x3, y3, z3, u3, v3, color, lightCoords, addBackFace, uPos, vPos, scaleData);
	}





	private void addFaceMod(FluidRenderer instance, VertexConsumer builder, float x0, float y0, float z0, float u0, float v0, float x1, float y1, float z1, float u1, float v1, float x2, float y2, float z2, float u2, float v2, float x3, float y3, float z3, float u3, float v3, int color, int lightCoords, boolean addBackFace, int uPos, int vPos, Tuple<Integer, Float> scaleData) {
		float scalant = scaleData.getB();
		float uMin = u0;
		float vMin = v0;
		float uMax = u2;
		float vMax = v2;
		float width = uMax - uMin;
		float height = vMax - vMin;
		u0 = BigWater.modCoord(u0,uPos,uMin,width,scalant);
		v0 = BigWater.modCoord(v0,vPos,vMin,height,scalant);
		u1 = BigWater.modCoord(u1,uPos,uMin,width,scalant);
		v1 = BigWater.modCoord(v1,vPos,vMin,height,scalant);
		u2 = BigWater.modCoord(u2,uPos,uMin,width,scalant);
		v2 = BigWater.modCoord(v2,vPos,vMin,height,scalant);
		u3 = BigWater.modCoord(u3,uPos,uMin,width,scalant);
		v3 = BigWater.modCoord(v3,vPos,vMin,height,scalant);

		addFace(builder, x0, y0, z0, u0, v0, x1, y1, z1, u1, v1, x2, y2, z2, u2, v2, x3, y3, z3, u3, v3, color, lightCoords, addBackFace);
	}

	@Shadow
	private void addFace(final VertexConsumer builder, final float x0, final float y0, final float z0, final float u0, final float v0, final float x1, final float y1, final float z1, final float u1, final float v1, final float x2, final float y2, final float z2, final float u2, final float v2, final float x3, final float y3, final float z3, final float u3, final float v3, final int color, final int lightCoords, final boolean addBackFace){}
}