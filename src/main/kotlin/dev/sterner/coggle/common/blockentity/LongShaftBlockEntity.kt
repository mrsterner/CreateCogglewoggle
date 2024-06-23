package dev.sterner.coggle.common.blockentity


import com.simibubi.create.AllBlocks
import com.simibubi.create.content.kinetics.base.IRotate
import com.simibubi.create.content.kinetics.base.KineticBlockEntity
import com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock
import com.simibubi.create.content.kinetics.simpleRelays.BracketedKineticBlockEntity
import dev.sterner.coggle.common.block.CoaxialGearBlock
import dev.sterner.coggle.common.block.DirectionalRotatedPillarKineticBlock
import dev.sterner.coggle.mixin.RotationPropagatorAccessor
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState

class LongShaftBlockEntity(type: BlockEntityType<*>?, pos: BlockPos?, state: BlockState?) :
    BracketedKineticBlockEntity(type, pos, state) {
    // It thinks getLevel() might be null (it's not)
    override fun tick() {
        super.tick()
        if (isVirtual || !hasLevel()) return
        val coaxialGearState = getLevel()!!.getBlockState(
            blockPos.relative(
                DirectionalRotatedPillarKineticBlock.getDirection(
                    blockState
                )
            )
        )
        if (!CoaxialGearBlock.isCoaxialGear(coaxialGearState) || coaxialGearState.getValue(
                RotatedPillarKineticBlock.AXIS
            ) !== blockState.getValue(RotatedPillarKineticBlock.AXIS) || !coaxialGearState.getValue(CoaxialGearBlock.HAS_SHAFT)
        ) {
            getLevel()!!.setBlockAndUpdate(
                blockPos,
                AllBlocks.SHAFT.defaultState.setValue(
                    RotatedPillarKineticBlock.AXIS,
                    blockState.getValue(RotatedPillarKineticBlock.AXIS)
                )
            )
        }
    }

    override fun propagateRotationTo(
        target: KineticBlockEntity,
        stateFrom: BlockState,
        stateTo: BlockState,
        diff: BlockPos,
        connectedViaAxes: Boolean,
        connectedViaCogs: Boolean
    ): Float {
        val direction = DirectionalRotatedPillarKineticBlock.getDirection(stateFrom)
        if (connectedToLongShaft(
                target,
                this,
                BlockPos.ZERO.subtract(diff)
            )
        ) return 1 / RotationPropagatorAccessor.invokeGetAxisModifier(target, direction.opposite)
        return 0f
    }

    override fun addPropagationLocations(
        block: IRotate,
        state: BlockState,
        neighbours: MutableList<BlockPos>
    ): List<BlockPos> {
        super.addPropagationLocations(block, state, neighbours)
        neighbours.add(blockPos.relative(DirectionalRotatedPillarKineticBlock.getDirection(state), 2))
        return neighbours
    }

    override fun isCustomConnection(other: KineticBlockEntity, state: BlockState, otherState: BlockState): Boolean {
        if (otherState.block is IRotate) {
            val direction = DirectionalRotatedPillarKineticBlock.getDirection(state)
            return (BlockPos.ZERO.relative(direction, 2) == other.blockPos.subtract(blockPos) && (otherState.block as IRotate).hasShaftTowards(
                getLevel(),
                other.blockPos,
                otherState,
                direction.opposite
            ))
        }

        return false
    }

    companion object {
        /**
         * Returns `true` if the `KineticBlockEntity` at the relative location is a `LongShaftBlockEntity` that is pointing in the right direction.
         * @param be
         * @param potentialLongShaft
         * @param diff `potentialLongShaft`'s position - `be`'s position
         */
        fun connectedToLongShaft(
            be: KineticBlockEntity,
            potentialLongShaft: KineticBlockEntity,
            diff: BlockPos
        ): Boolean {
            if (potentialLongShaft is LongShaftBlockEntity && be.blockState.block is IRotate) {
                val direction = DirectionalRotatedPillarKineticBlock.getDirection(potentialLongShaft.getBlockState())
                return diff == BlockPos.ZERO.relative(direction.opposite, 2) && (be.blockState.block as IRotate).hasShaftTowards(
                    be.level,
                    be.blockPos,
                    be.blockState,
                    direction.opposite
                ) // If this has a Shaft in the right direction
            }

            return false
        }
    }
};
