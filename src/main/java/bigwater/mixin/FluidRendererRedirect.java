package bigwater.mixin;

import bigwater.BigWater;
import bigwater.access.FluidRendererAccess;
import com.mojang.blaze3d.vertex.VertexConsumer;
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
		addTopFace(instance, builder, x0, y0, z0, u0, v0, x1, y1, z1, u1, v1, x2, y2, z2, u2, v2, x3, y3, z3, u3, v3, color, lightCoords, addBackFace, false);
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
		addTopFace(instance, builder, x0, y0, z0, u0, v0, x1, y1, z1, u1, v1, x2, y2, z2, u2, v2, x3, y3, z3, u3, v3, color, lightCoords, addBackFace, true);
	}





	private void addTopFace(FluidRenderer instance, VertexConsumer builder, float x0, float y0, float z0, float u0, float v0, float x1, float y1, float z1, float u1, float v1, float x2, float y2, float z2, float u2, float v2, float x3, float y3, float z3, float u3, float v3, int color, int lightCoords, boolean addBackFace, boolean reverseCoords) {
		Vec3 flow = ((FluidRendererAccess)instance).getFlow();
		if(flow.x != 0.0d || flow.z != 0.0d){
			addFace(builder, x0, y0, z0, u0, v0, x1, y1, z1, u1, v1, x2, y2, z2, u2, v2, x3, y3, z3, u3, v3, color, lightCoords, addBackFace);
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
		if (reverseCoords) xPos = BigWater.reverseCoord(xPos, textureScale);
		int zPos = pos.getZ() % textureScale;
		if (zPos < 0) zPos = textureScale + zPos;
		if (reverseCoords) zPos = BigWater.reverseCoord(zPos, textureScale);
		float uMin = u0;
		float vMin = v0;
		float uMax = u2;
		float vMax = v2;
		float width = uMax - uMin;
		float height = vMax - vMin;
		addFace(builder, x0, y0, z0, BigWater.modCoord(u0,xPos,uMin,width,scalant), BigWater.modCoord(v0,zPos,vMin,height,scalant), x1, y1, z1, BigWater.modCoord(u1,xPos,uMin,width,scalant), BigWater.modCoord(v1,zPos,vMin,height,scalant), x2, y2, z2, BigWater.modCoord(u2,xPos,uMin,width,scalant), BigWater.modCoord(v2,zPos,vMin,height,scalant), x3, y3, z3, BigWater.modCoord(u3,xPos,uMin,width,scalant), BigWater.modCoord(v3,zPos,vMin,height,scalant), color, lightCoords, addBackFace);
	}

	@Shadow
	private void addFace(final VertexConsumer builder, final float x0, final float y0, final float z0, final float u0, final float v0, final float x1, final float y1, final float z1, final float u1, final float v1, final float x2, final float y2, final float z2, final float u2, final float v2, final float x3, final float y3, final float z3, final float u3, final float v3, final int color, final int lightCoords, final boolean addBackFace){}
}