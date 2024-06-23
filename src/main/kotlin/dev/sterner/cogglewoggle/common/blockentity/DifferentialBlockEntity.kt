package dev.sterner.cogglewoggle.common.blockentity


import com.simibubi.create.content.kinetics.base.IRotate
import com.simibubi.create.content.kinetics.base.KineticBlock
import com.simibubi.create.content.kinetics.base.KineticBlockEntity
import com.simibubi.create.content.kinetics.transmission.SplitShaftBlockEntity
import dev.sterner.cogglewoggle.common.block.DifferentialBlock
import dev.sterner.cogglewoggle.common.block.DirectionalRotatedPillarKineticBlock
import dev.sterner.cogglewoggle.mixin.RotationPropagatorAccessor
import dev.sterner.cogglewoggle.registry.CoggleBlocks
import dev.sterner.cogglewoggle.util.KineticsHelper.addLargeCogwheelPropagationLocations
import dev.sterner.cogglewoggle.util.KineticsHelper.directionBetween
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import kotlin.math.sign

class DifferentialBlockEntity(type: BlockEntityType<*>?, pos: BlockPos?, state: BlockState?) :
    SplitShaftBlockEntity(type, pos, state) {
    var oldControlSpeed: Float = 0f

    override fun propagateRotationTo(
        target: KineticBlockEntity,
        stateFrom: BlockState,
        stateTo: BlockState,
        diff: BlockPos,
        connectedViaAxes: Boolean,
        connectedViaCogs: Boolean
    ): Float {
        if (connectedViaAxes || LongShaftBlockEntity.connectedToLongShaft(this, target, diff)) {
            if (target is DifferentialBlockEntity) return 0f
            return (ratio(stateFrom) * sign(
                RotationPropagatorAccessor.invokeGetAxisModifier(
                    target, directionBetween(
                        target.blockPos,
                        blockPos
                    )
                ).toDouble()
            )).toFloat()
        }

        return super.propagateRotationTo(target, stateFrom, stateTo, diff, connectedViaAxes, connectedViaCogs)
    }

    // It thinks getLevel() might be null
    override fun setSource(source: BlockPos) {
        super.setSource(source)
        val directionBetween = directionBetween(blockPos, source)
        if (hasLevel() && (directionBetween == null || directionBetween.axis !== blockState.getValue(DifferentialBlock.AXIS))) getLevel()!!.destroyBlock(
            blockPos, true
        )
    }

    // It thinks getLevel() might be null
    override fun tick() {
        super.tick()
        if (!hasLevel()) return

        val direction = DirectionalRotatedPillarKineticBlock.getDirection(blockState)
        val otherAdjacentPos = blockPos.relative(direction.opposite)

        if (getSpeed() == 0f) { // Try switching the direction if we're not powered by the existing side
            val adjacentPos = blockPos.relative(direction)
            if (!propagatesToMe(adjacentPos, direction.opposite) && propagatesToMe(
                    otherAdjacentPos,
                    direction
                )
            ) getLevel()!!.setBlockAndUpdate(
                blockPos,
                CoggleBlocks.DUMMY_DIFFERENTIAL.getDefaultState().setValue(DifferentialBlock.AXIS, direction.axis)
                    .setValue(
                        DirectionalRotatedPillarKineticBlock.POSITIVE_AXIS_DIRECTION,
                        direction.axisDirection == Direction.AxisDirection.NEGATIVE
                    )
            )
        }



        if (getLevel()!!.getBlockEntity(otherAdjacentPos) is KineticBlockEntity) {
            oldControlSpeed = getPropagatedSpeed((getLevel()!!.getBlockEntity(otherAdjacentPos) as KineticBlockEntity), direction)
        }
    }

    override fun removeSource() {
        super.removeSource()
        if (hasLevel()) getLevel()!!.setBlockAndUpdate(
            blockPos,
            blockState.cycle(DirectionalRotatedPillarKineticBlock.POSITIVE_AXIS_DIRECTION)
        )
    }

    fun ratio(stateFrom: BlockState?): Float {
        val towardsInput = DirectionalRotatedPillarKineticBlock.getDirection(stateFrom!!)
        val towardsControl = towardsInput.opposite
        val inputPos = blockPos.relative(towardsInput)
        val controlPos = blockPos.relative(towardsControl)

        val inputBE = getLevel()!!.getBlockEntity(inputPos)
        var inputSpeed = 0f
        if (propagatesToMe(inputPos, towardsControl) && inputBE is KineticBlockEntity) inputSpeed =
            getPropagatedSpeed(inputBE, towardsControl)

        val controlBE = getLevel()!!.getBlockEntity(controlPos)
        var controlSpeed = 0f
        if (propagatesToMe(controlPos, towardsInput) && controlBE is KineticBlockEntity) controlSpeed =
            getPropagatedSpeed(controlBE, towardsInput)

        if (inputSpeed + controlSpeed == 0f) return 0f
        return 2f * inputSpeed / (inputSpeed + controlSpeed)
    }

    fun propagatesToMe(pos: BlockPos?, directionToMe: Direction?): Boolean {
        if (!hasLevel()) return false
        val state = getLevel()!!.getBlockState(pos)
        return state.block is KineticBlock && (state.block as KineticBlock).hasShaftTowards(getLevel(), pos, state, directionToMe)
    }

    fun getPropagatedSpeed(from: KineticBlockEntity, directionToMe: Direction?): Float {
        if (from is DifferentialBlockEntity) return 0f
        return from.speed * RotationPropagatorAccessor.invokeGetAxisModifier(from, directionToMe)
    }

    override fun addPropagationLocations(
        block: IRotate,
        state: BlockState,
        neighbours: MutableList<BlockPos?>
    ): MutableList<BlockPos?> {
        super.addPropagationLocations(block, state, neighbours)
        addLargeCogwheelPropagationLocations(worldPosition, neighbours)
        return neighbours
    }

    override fun canPropagateDiagonally(block: IRotate, state: BlockState): Boolean {
        return true
    }

    override fun getRotationSpeedModifier(face: Direction): Float {
        return ratio(blockState)
    }
};
