package dev.sterner.coggle.client.renderer


import com.jozufozu.flywheel.backend.Backend
import com.mojang.blaze3d.vertex.PoseStack
import com.simibubi.create.content.kinetics.base.DirectionalAxisKineticBlock
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer
import com.simibubi.create.foundation.render.CachedBufferer
import com.simibubi.create.foundation.utility.AnimationTickHolder
import dev.sterner.coggle.common.block.DoubleCardanShaftBlock
import dev.sterner.coggle.common.blockentity.DoubleCardanShaftBlockEntity
import dev.sterner.coggle.util.CogglePartials
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.util.Mth
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.Vec3
import java.util.List
import kotlin.math.atan2

class DoubleCardanShaftRenderer(context: BlockEntityRendererProvider.Context?) :
    KineticBlockEntityRenderer<DoubleCardanShaftBlockEntity?>(context) {

    override fun renderSafe(
        doubleCardanShaftBlockEntity: DoubleCardanShaftBlockEntity?,
        partialTicks: Float,
        ms: PoseStack,
        buffer: MultiBufferSource,
        light: Int,
        overlay: Int
    ) {
        //if (Backend.canUseInstancing(doubleCardanShaftBlockEntity!!.getLevel())) return

        val time = AnimationTickHolder.getRenderTime(doubleCardanShaftBlockEntity!!.getLevel())
        val state: BlockState = doubleCardanShaftBlockEntity.getBlockState()
        val vbSolid = buffer.getBuffer(RenderType.solid())

        val directions: Array<Direction> = DoubleCardanShaftBlock.getDirectionsConnectedByState(state)
        val shaft1Direction = directions[0]
        val shaft2Direction = directions[1]
        val facesHaveSameSign = shaft1Direction.axisDirection == shaft2Direction.axisDirection
        val secondaryPositive = state.getValue<Boolean>(DirectionalAxisKineticBlock.AXIS_ALONG_FIRST_COORDINATE)

        val axis = getAxis(shaft1Direction, shaft2Direction)

        val gimbal1Angle =
            Mth.PI * ((time * getSpeed(doubleCardanShaftBlockEntity, shaft1Direction) * 3f / 10) % 360) / 180f
        val gimbal2Angle =
            Mth.PI * ((time * getSpeed(doubleCardanShaftBlockEntity, shaft2Direction) * 3f / 10) % 360) / 180f
        val fluctuatingAngle1 =
            atan2(Mth.sin(gimbal1Angle).toDouble(), (Mth.cos(gimbal1Angle) * Mth.sqrt(2f) / 2).toDouble())
                .toFloat() + (if (axis === Direction.Axis.Z) Mth.PI / 4 * (if (facesHaveSameSign) 1f else -1f) else 0f)
        val fluctuatingAngle2 =
            atan2(Mth.sin(gimbal2Angle).toDouble(), (Mth.cos(gimbal2Angle) * Mth.sqrt(2f) / 2).toDouble()).toFloat()
        val fluctuatingAngle3 = atan2(
            Mth.sin(gimbal1Angle + Mth.PI / 2).toDouble(),
            (Mth.cos(gimbal1Angle + Mth.PI / 2) * Mth.sqrt(2f) / 2).toDouble()
        )
            .toFloat() + Mth.PI / 2
        val gimbal1FluctuatingAngle =
            Mth.sin(fluctuatingAngle1 + (if (axis === Direction.Axis.Z) -Mth.PI / 4 else 0f) + (if (facesHaveSameSign xor (axis === Direction.Axis.X && shaft1Direction.axis === Direction.Axis.Z)) Mth.PI else 0f) + (if (axis === Direction.Axis.X) Mth.PI / 2 else 0f)) * Mth.PI / 4
        val gimbal2FluctuatingAngle =
            Mth.sin(fluctuatingAngle2 + (if (facesHaveSameSign xor (axis === Direction.Axis.X && shaft2Direction.axis === Direction.Axis.Z)) Mth.PI else 0f) + (if (axis === Direction.Axis.Z && !facesHaveSameSign) Mth.PI / 2 else 0f) + (if (axis === Direction.Axis.X) Mth.PI / 2 else 0f)) * Mth.PI / 4
        val offset1 = Mth.PI * getRotationOffsetForPosition(
            doubleCardanShaftBlockEntity,
            doubleCardanShaftBlockEntity.getBlockPos(),
            shaft1Direction.axis
        ) / 180f
        val offset2 = Mth.PI * getRotationOffsetForPosition(
            doubleCardanShaftBlockEntity,
            doubleCardanShaftBlockEntity.getBlockPos(),
            shaft2Direction.axis
        ) / 180f

        val shaft1 = CachedBufferer.partialFacing(CogglePartials.DCS_SIDE_SHAFT, state, shaft1Direction)
        kineticRotationTransform(
            shaft1,
            doubleCardanShaftBlockEntity,
            shaft1Direction.axis,
            gimbal1Angle + offset1,
            light
        )
        shaft1.renderInto(ms, vbSolid)

        val shaft2 = CachedBufferer.partialFacing(CogglePartials.DCS_SIDE_SHAFT, state, shaft2Direction)
        kineticRotationTransform(
            shaft2,
            doubleCardanShaftBlockEntity,
            shaft2Direction.axis,
            gimbal2Angle + offset2,
            light
        )
        shaft2.renderInto(ms, vbSolid)

        val grip1 = CachedBufferer.partialFacing(CogglePartials.DCS_SIDE_GRIP, state, shaft1Direction)
        kineticRotationTransform(
            grip1,
            doubleCardanShaftBlockEntity,
            shaft1Direction.axis,
            gimbal1Angle + (if (axis === Direction.Axis.Z) Mth.PI / 2 else 0f),
            light
        )
        grip1.renderInto(ms, vbSolid)

        val grip2 = CachedBufferer.partialFacing(CogglePartials.DCS_SIDE_GRIP, state, shaft2Direction)
        kineticRotationTransform(grip2, doubleCardanShaftBlockEntity, shaft2Direction.axis, gimbal2Angle, light)
        grip2.renderInto(ms, vbSolid)

        CachedBufferer.partial(CogglePartials.DCS_CENTER_SHAFT, state)
            .translate(shaft1Direction.step().mul(2.5f / 16f))
            .translate(shaft2Direction.step().mul(2.5f / 16f))
            .centre()
            .rotateY((if (axis === Direction.Axis.Z) 90f else 0f).toDouble())
            .rotateX((if (axis === Direction.Axis.Z) (if (facesHaveSameSign) 45f else 135f) else 0f).toDouble())
            .rotate((if (facesHaveSameSign xor (axis !== Direction.Axis.Y)) 135f else 45f).toDouble(), axis)
            .unCentre()
            .centre()
            .rotateZRadians(((if (axis === Direction.Axis.X) fluctuatingAngle3 else fluctuatingAngle1) * (if (axis === Direction.Axis.X || ((axis === Direction.Axis.Y) xor facesHaveSameSign)) 1f else -1f) * (if (axis === Direction.Axis.X) -1f else 1f)).toDouble())
            .unCentre()
            .renderInto(ms, vbSolid)

        CachedBufferer.partialFacing(CogglePartials.DCS_GIMBAL, state, shaft1Direction)

            .centre()
            .rotate(Direction.get(Direction.AxisDirection.POSITIVE, shaft1Direction.axis), gimbal1Angle)
            .centre()

            .translateBack(gimbalTranslation(shaft1Direction))
            .rotate(
                gimbalRotation(shaft1Direction, axis === Direction.Axis.Z),
                gimbal1FluctuatingAngle
            )
            .rotateY((if (axis === Direction.Axis.Z && !secondaryPositive) 90 else 0).toDouble())
            .rotateX((if (axis === Direction.Axis.Z) 90 else 0).toDouble())
            .translate(gimbalTranslation(shaft1Direction))

            .unCentre()
            .unCentre()
            .renderInto(ms, vbSolid)

        CachedBufferer.partialFacing(CogglePartials.DCS_GIMBAL, state, shaft2Direction)

            .centre()
            .rotate(Direction.get(Direction.AxisDirection.POSITIVE, shaft2Direction.axis), gimbal2Angle)
            .centre()

            .translateBack(gimbalTranslation(shaft2Direction))
            .rotate(gimbalRotation(shaft2Direction, false), gimbal2FluctuatingAngle)
            .translate(gimbalTranslation(shaft2Direction))

            .unCentre()
            .unCentre()
            .renderInto(ms, vbSolid)
    }

    private fun getSpeed(blockEntity: DoubleCardanShaftBlockEntity, face: Direction): Float {
        var sourceFacing: Direction? = null
        if (blockEntity.hasSource()) {
            val source: BlockPos =
                blockEntity.source!!.subtract(blockEntity.getBlockPos()) // It thinks source can be null (it can't)
            sourceFacing = Direction.getNearest(source.x.toFloat(), source.y.toFloat(), source.z.toFloat())
        }

        var speed: Float = blockEntity.getSpeed()
        if (speed != 0f && sourceFacing != null) {
            if (sourceFacing.axisDirection == face.axisDirection && face != sourceFacing) speed *= -1f
        }

        return speed
    }

    protected fun getSourceFacing(blockEntity: DoubleCardanShaftBlockEntity): Direction? {
        if (blockEntity.hasSource()) {
            val source: BlockPos =
                blockEntity.source!!.subtract(blockEntity.getBlockPos()) // It thinks source can be null (it can't)
            return Direction.getNearest(source.x.toFloat(), source.y.toFloat(), source.z.toFloat())
        } else {
            return null
        }
    }

    private fun getAxis(shaft1Direction: Direction, shaft2Direction: Direction): Direction.Axis {
        val axes: MutableList<Direction.Axis> = ArrayList()
        axes.addAll(List.of(*Direction.Axis.entries.toTypedArray()))
        axes.remove(shaft1Direction.axis)
        axes.remove(shaft2Direction.axis)
        return axes[0]
    }

    fun gimbalTranslation(face: Direction?): Vec3 {
        return when (face) {
            Direction.NORTH -> Vec3(8 / 16.0, 8 / 16.0, 13 / 16.0)
            Direction.EAST -> Vec3(3 / 16.0, 8 / 16.0, 8 / 16.0)
            Direction.SOUTH -> Vec3(8 / 16.0, 8 / 16.0, 3 / 16.0)
            Direction.WEST -> Vec3(13 / 16.0, 8 / 16.0, 8 / 16.0)
            Direction.UP -> Vec3(8 / 16.0, 3 / 16.0, 8 / 16.0)
            Direction.DOWN -> Vec3(8 / 16.0, 13 / 16.0, 8 / 16.0)
            else -> Vec3(8 / 16.0, 8 / 16.0, 8 / 16.0) // Shouldn't be called
        }
    };

    fun gimbalRotation(direction: Direction?, isZaxis: Boolean): Direction {
        return when (direction) {
            Direction.NORTH -> Direction.EAST
            Direction.EAST -> if (isZaxis) Direction.UP else Direction.SOUTH
            Direction.SOUTH -> Direction.EAST
            Direction.WEST -> if (isZaxis) Direction.UP else Direction.SOUTH
            Direction.UP -> if (isZaxis) Direction.SOUTH else Direction.EAST
            Direction.DOWN -> if (isZaxis) Direction.SOUTH else Direction.EAST
            else -> throw IllegalStateException("Unknown direction")
        }
    };
};
