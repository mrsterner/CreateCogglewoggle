package dev.sterner.coggle.common.block

import com.simibubi.create.content.kinetics.base.KineticBlockEntity
import com.simibubi.create.content.kinetics.simpleRelays.CogWheelBlock
import com.simibubi.create.content.kinetics.transmission.GearshiftBlock
import dev.sterner.coggle.common.blockentity.DifferentialBlockEntity
import dev.sterner.coggle.registry.CoggleBlockEntityTypes
import dev.sterner.coggle.registry.CoggleBlocks
import dev.sterner.coggle.util.KineticsHelper
import io.github.fabricators_of_create.porting_lib.block.NeighborChangeListeningBlock
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.Direction.Axis
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.RandomSource
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition.Builder
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape

class DifferentialBlock(properties: Properties) : CogWheelBlock(true, properties),
    NeighborChangeListeningBlock
{

    companion object {
        val FULL_MODEL: BooleanProperty = BooleanProperty.create("full_model")
    }

    init {
        registerDefaultState(defaultBlockState().setValue(FULL_MODEL, true))
    }

    override fun createBlockStateDefinition(builder: Builder<Block, BlockState>) {
        super.createBlockStateDefinition(builder)
        builder.add(DirectionalRotatedPillarKineticBlock.POSITIVE_AXIS_DIRECTION)
        builder.add(FULL_MODEL)
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState {
        return super.getStateForPlacement(context)!!.setValue(FULL_MODEL, false)
    }

    override fun onPlace(state: BlockState, level: Level, pos: BlockPos, oldState: BlockState, isMoving: Boolean) {
        if (state.getValue(FULL_MODEL)) level.setBlockAndUpdate(pos, state.setValue(FULL_MODEL, false))
        super.onPlace(state, level, pos, oldState, isMoving)
    }

    override fun getShape(state: BlockState, worldIn: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
        return Shapes.block()
    }

    override fun onNeighborChange(state: BlockState, level: LevelReader, pos: BlockPos, neighbor: BlockPos) {
        withBlockEntityDo(level, pos) { be ->
            val neighborBE = level.getBlockEntity(neighbor)
            val directionBetween = KineticsHelper.directionBetween(pos, neighbor)
            val differentialDirection = DirectionalRotatedPillarKineticBlock.getDirection(state)
            if (be is DifferentialBlockEntity && be.hasLevel() && directionBetween == differentialDirection.opposite) {
                var newControlSpeed = 0f
                if (neighborBE is KineticBlockEntity) {
                    newControlSpeed = be.getPropagatedSpeed(neighborBE, differentialDirection)
                }
                if (be.oldControlSpeed != newControlSpeed) {
                    be.level?.scheduleTick(pos, this, 1)
                }
            }
        }
    }

    override fun tick(state: BlockState, level: ServerLevel, pos: BlockPos, random: RandomSource) {
        level.setBlockAndUpdate(pos, CoggleBlocks.DUMMY_DIFFERENTIAL.getDefaultState()
            .setValue(AXIS, state.getValue(AXIS))
            .setValue(DirectionalRotatedPillarKineticBlock.POSITIVE_AXIS_DIRECTION, state.getValue(DirectionalRotatedPillarKineticBlock.POSITIVE_AXIS_DIRECTION))
        )
    }

    override fun hasShaftTowards(world: LevelReader, pos: BlockPos, state: BlockState, face: Direction): Boolean {
        return face == DirectionalRotatedPillarKineticBlock.getDirection(state)
    }

    override fun isLargeCog(): Boolean {
        return true
    }

    override fun isDedicatedCogWheel(): Boolean {
        return true
    }

    override fun getBlockEntityType(): BlockEntityType<out KineticBlockEntity> {
        return CoggleBlockEntityTypes.DIFFERENTIAL.get()
    }

    override fun getRotationAxis(state: BlockState): Axis {
        return state.getValue(AXIS)
    }


}
