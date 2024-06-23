package dev.sterner.coggle.util

import com.jozufozu.flywheel.util.transform.TransformStack
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction


object KineticsHelper {
    fun addLargeCogwheelPropagationLocations(pos: BlockPos, neighbours: MutableList<BlockPos?>) {
        BlockPos.betweenClosedStream(BlockPos(-1, -1, -1), BlockPos(1, 1, 1))
            .forEach { offset: BlockPos ->
                if (offset.distSqr(BlockPos.ZERO) == 2.0) neighbours.add(pos.offset(offset))
            }
    }

    fun directionBetween(posFrom: BlockPos, posTo: BlockPos): Direction? {
        for (direction in Direction.values()) {
            if (posFrom.relative(direction) == posTo) return direction
        }

        return null
    }

    fun rotateToFace(facing: Direction?): PoseStack {
        val poseStack = PoseStack()
        TransformStack.cast(poseStack)
            .centre()
            .rotateToFace(facing)
            .multiply(Axis.XN.rotationDegrees(-90f))
            .unCentre()
        return poseStack
    }

    fun rotateToAxis(axis: Direction.Axis): PoseStack {
        val facing: Direction = Direction.fromAxisAndDirection(axis, Direction.AxisDirection.POSITIVE)
        val poseStack = PoseStack()
        TransformStack.cast(poseStack)
            .centre()
            .rotateToFace(facing)
            .multiply(Axis.XN.rotationDegrees(-90f))
            .unCentre()
        return poseStack
    }
};
