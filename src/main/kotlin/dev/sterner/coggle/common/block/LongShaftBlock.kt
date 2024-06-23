package dev.sterner.coggle.common.block

import com.simibubi.create.AllBlocks
import com.simibubi.create.content.contraptions.ITransformableBlock
import com.simibubi.create.content.contraptions.StructureTransform
import com.simibubi.create.content.kinetics.base.KineticBlockEntity
import com.simibubi.create.content.kinetics.simpleRelays.ShaftBlock
import dev.sterner.coggle.registry.CoggleBlockEntityTypes
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Mirror
import net.minecraft.world.level.block.Rotation
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition


class LongShaftBlock(properties: Properties?) : ShaftBlock(properties), ITransformableBlock {
    init {
        registerDefaultState(
            defaultBlockState().setValue<Boolean, Boolean>(
                DirectionalRotatedPillarKineticBlock.POSITIVE_AXIS_DIRECTION,
                true
            )
        )
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block?, BlockState?>) {
        builder.add(DirectionalRotatedPillarKineticBlock.POSITIVE_AXIS_DIRECTION)
        super.createBlockStateDefinition(builder)
    }

    override fun hasShaftTowards(world: LevelReader?, pos: BlockPos?, state: BlockState, face: Direction): Boolean {
        if (!state.hasProperty(DirectionalRotatedPillarKineticBlock.POSITIVE_AXIS_DIRECTION)) return false
        return face.getAxis() === state.getValue<Direction.Axis>(AXIS) && (face.getAxisDirection() === Direction.AxisDirection.POSITIVE) != state.getValue(
            DirectionalRotatedPillarKineticBlock.POSITIVE_AXIS_DIRECTION
        )
    }

    override fun getCloneItemStack(level: BlockGetter, pos: BlockPos, state: BlockState): ItemStack {
        return AllBlocks.SHAFT.asStack()
    }

    override fun onWrenched(state: BlockState, context: UseOnContext): InteractionResult {
        if (tryRemoveBracket(context)) return InteractionResult.SUCCESS
        return super.onSneakWrenched(state, context)
    }

    override fun onRemove(state: BlockState?, world: Level?, pos: BlockPos?, newState: BlockState?, isMoving: Boolean) {
        super.onRemove(state, world, pos, newState, isMoving)
    }

    override fun rotate(state: BlockState, rot: Rotation): BlockState {
        return getStateForDirection(rot.rotate(DirectionalRotatedPillarKineticBlock.getDirection(state)))
    }

    override fun mirror(state: BlockState, mirror: Mirror): BlockState {
        return getStateForDirection(mirror.mirror(DirectionalRotatedPillarKineticBlock.getDirection(state)))
    }

    override fun transform(state: BlockState, transform: StructureTransform): BlockState {
        return getStateForDirection(
            transform.mirrorFacing(
                transform.rotateFacing(
                    DirectionalRotatedPillarKineticBlock.getDirection(
                        state
                    )
                )
            )
        )
    }

    fun getStateForDirection(direction: Direction): BlockState {
        return defaultBlockState().setValue(AXIS, direction.getAxis()).setValue<Boolean, Boolean>(
            DirectionalRotatedPillarKineticBlock.POSITIVE_AXIS_DIRECTION,
            direction.getAxisDirection() === Direction.AxisDirection.POSITIVE
        )
    }

    override fun getBlockEntityType(): BlockEntityType<out KineticBlockEntity?> {
        return CoggleBlockEntityTypes.LONG_SHAFT.get()
    }
};
