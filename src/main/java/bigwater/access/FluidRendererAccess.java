package bigwater.access;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;

public interface FluidRendererAccess {
    void setPos(BlockPos pos);
    BlockPos getPos();

    void setFluidState(FluidState state);
    FluidState getFluidState();

    void setFlow(Vec3 flow);
    Vec3 getFlow();

    void setDirection(Direction dir);
    Direction getDirection();
}
