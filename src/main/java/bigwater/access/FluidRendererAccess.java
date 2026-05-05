package bigwater.access;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;

public interface FluidRendererAccess {
    void bigwater$setPos(BlockPos pos);
    BlockPos bigwater$getPos();

    void bigwater$setFluidState(FluidState state);
    FluidState bigwater$getFluidState();

    void bigwater$setFlow(Vec3 flow);
    Vec3 bigwater$getFlow();

    void bigwater$setDirection(Direction dir);
    Direction bigwater$getDirection();
}
