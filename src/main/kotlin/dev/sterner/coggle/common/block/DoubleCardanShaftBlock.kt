package dev.sterner.coggle.common.block


import com.simibubi.create.AllShapes
import com.simibubi.create.content.contraptions.StructureTransform
import com.simibubi.create.content.kinetics.base.DirectionalAxisKineticBlock
import com.simibubi.create.foundation.block.IBE
import dev.sterner.coggle.common.blockentity.DoubleCardanShaftBlockEntity
import dev.sterner.coggle.registry.CoggleBlockEntityTypes
import dev.sterner.coggle.registry.CoggleBlocks
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.Mirror
import net.minecraft.world.level.block.Rotation
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape
import java.util.*
import java.util.List
import java.util.Map
import kotlin.collections.ArrayList
import kotlin.collections.MutableList
import kotlin.collections.toTypedArray

class DoubleCardanShaftBlock(properties: Properties?) : DirectionalAxisKineticBlock(properties),
    IBE<DoubleCardanShaftBlockEntity> {

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState {
        val direction1 = context.clickedFace.opposite
        val direction2 = if (direction1.axis === Direction.Axis.Y) {
            context.horizontalDirection
        } else {
            context.nearestLookingVerticalDirection
        }

        return getBlockstateConnectingDirections(direction1, direction2)
    }

    override fun hasShaftTowards(world: LevelReader, pos: BlockPos, state: BlockState, face: Direction): Boolean {
        return Arrays.asList(*getDirectionsConnectedByState(state)).contains(face)
    }

    override fun getRotatedBlockState(originalState: BlockState, targetedFace: Direction): BlockState {
        return transform(
            originalState,
            StructureTransform(BlockPos(0, 0, 0), targetedFace.axis, Rotation.CLOCKWISE_90, Mirror.NONE)
        )
    }

    override fun getShape(
        state: BlockState,
        worldIn: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape {
        var xDirection: Direction? = null
        var yDirection: Direction? = null
        var zDirection: Direction? = null
        for (direction in getDirectionsConnectedByState(state)) {
            when (direction.axis) {
                Direction.Axis.X -> xDirection = direction
                Direction.Axis.Y -> yDirection = direction
                Direction.Axis.Z -> zDirection = direction
            }
        }

        return AllShapes.Builder(
            box(
                if (xDirection == Direction.WEST) 0.0 else 5.0,
                if (yDirection == Direction.DOWN) 0.0 else 5.0,
                if (zDirection == Direction.NORTH) 0.0 else 5.0,
                if (xDirection == Direction.EAST) 16.0 else 11.0,
                if (yDirection == Direction.UP) 16.0 else 11.0,
                if (zDirection == Direction.SOUTH) 16.0 else 11.0
            )
        ).build()
    }

    override fun rotate(state: BlockState, rot: Rotation): BlockState {
        val directions = getDirectionsConnectedByState(state)
        return getBlockstateConnectingDirections(
            rot.rotate(directions[0]), rot.rotate(
                directions[1]
            )
        )
    }

    override fun mirror(state: BlockState, mirror: Mirror): BlockState {
        val directions = getDirectionsConnectedByState(state)
        return getBlockstateConnectingDirections(
            mirror.getRotation(directions[0]).rotate(
                directions[0]
            ), mirror.getRotation(directions[1]).rotate(directions[1])
        )
    }

    override fun transform(state: BlockState, transform: StructureTransform): BlockState {
        val directions = getDirectionsConnectedByState(state)
        return getBlockstateConnectingDirections(
            transform.mirrorFacing(
                transform.rotateFacing(
                    directions[0]
                )
            ), transform.mirrorFacing(transform.rotateFacing(directions[1]))
        )
    }

    override fun getBlockEntityClass(): Class<DoubleCardanShaftBlockEntity> {
        return DoubleCardanShaftBlockEntity::class.java
    }

    override fun getBlockEntityType(): BlockEntityType<out DoubleCardanShaftBlockEntity?> {
        return CoggleBlockEntityTypes.DOUBLE_CARDAN_SHAFT.get()
    }

    companion object {
        /**
         * XYZXYZ
         *
         *
         * Considering the above list, with the Axis of `facing` excluded:
         *  * If `axisAlongFirst` is true, the shaft connects `facing` and the Axis to the right in the same direction.
         *  * If `axisAlongFirst` is false, the shaft connects `facing` and the Axis to the left in the opposite direction.
         *
         */
        /*
     * Direction axisAlongFirst Other Direction
     * -Z NORTH  true           -X WEST
     * -Z NORTH  false          +Y UP
     * +Z SOUTH  true           +X EAST
     * +Z SOUTH  false          -Y DOWN
     * +X EAST   true           +Y UP
     * +X EAST   false          -Z NORTH
     * -X WEST   true           -Y DOWN
     * -X WEST   false          +Z SOUTH
     * +Y UP     true           +Z SOUTH
     * +Y UP     false          -X WEST
     * -Y DOWN   true           -Z SOUTH
     * -Y DOWN   false          +Z NORTH
     */
        fun getDirectionsConnectedByState(state: BlockState): Array<Direction> {
            val facing = state.getValue(FACING)
            val axisAlongFirst = state.getValue(AXIS_ALONG_FIRST_COORDINATE)
            val secondDirectionAxis = when (facing.axis) {
                Direction.Axis.X -> if (axisAlongFirst) Direction.Axis.Y else Direction.Axis.Z
                Direction.Axis.Y -> if (axisAlongFirst) Direction.Axis.Z else Direction.Axis.X
                Direction.Axis.Z -> if (axisAlongFirst) Direction.Axis.X else Direction.Axis.Y
                else -> throw IllegalStateException("Unknown axis")
            }

            return arrayOf(
                facing,
                Direction.fromAxisAndDirection(
                    secondDirectionAxis,
                    if ((facing.axisDirection == Direction.AxisDirection.POSITIVE) xor axisAlongFirst) Direction.AxisDirection.NEGATIVE else Direction.AxisDirection.POSITIVE
                )
            )
        }

        fun getBlockstateConnectingDirections(direction1: Direction, direction2: Direction): BlockState {
            val axisAlongFirst = (direction1.axisDirection == direction2.axisDirection)
            val directionsForEachAxis = Map.of(direction1.axis, direction1, direction2.axis, direction2)
            val axes: MutableList<Direction.Axis> = ArrayList()
            axes.addAll(List.of(*Direction.Axis.entries.toTypedArray()))
            axes.remove(direction1.axis)
            axes.remove(direction2.axis)
            val primaryAxis = when (axes[0]) {
                Direction.Axis.X -> if (axisAlongFirst) Direction.Axis.Y else Direction.Axis.Z
                Direction.Axis.Y -> if (axisAlongFirst) Direction.Axis.Z else Direction.Axis.X
                Direction.Axis.Z -> if (axisAlongFirst) Direction.Axis.X else Direction.Axis.Y
                else -> throw IllegalStateException("Unknown axis")
            }

            return CoggleBlocks.DOUBLE_CARDAN_SHAFT.getDefaultState().setValue(
                FACING,
                directionsForEachAxis[primaryAxis]
            ).setValue(AXIS_ALONG_FIRST_COORDINATE, axisAlongFirst)
        }

        fun isPositiveDirection(direction: Direction): Boolean {
            return Direction.get(Direction.AxisDirection.POSITIVE, direction.axis) == direction
        }
    }
};
