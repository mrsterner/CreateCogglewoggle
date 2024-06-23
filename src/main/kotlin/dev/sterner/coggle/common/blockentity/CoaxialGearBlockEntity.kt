package dev.sterner.coggle.common.blockentity

import com.simibubi.create.AllBlocks
import com.simibubi.create.content.kinetics.base.IRotate
import com.simibubi.create.content.kinetics.base.KineticBlockEntity
import com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock
import com.simibubi.create.content.kinetics.simpleRelays.BracketedKineticBlockEntity
import dev.sterner.coggle.common.block.CoaxialGearBlock
import dev.sterner.coggle.common.block.DirectionalRotatedPillarKineticBlock
import dev.sterner.coggle.registry.CoggleBlocks
import dev.sterner.coggle.util.KineticsHelper
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState


class CoaxialGearBlockEntity(type: BlockEntityType<*>?, pos: BlockPos?, state: BlockState?) :
    BracketedKineticBlockEntity(type, pos, state) {
    override fun propagateRotationTo(
        target: KineticBlockEntity,
        stateFrom: BlockState,
        stateTo: BlockState,
        diff: BlockPos,
        connectedViaAxes: Boolean,
        connectedViaCogs: Boolean
    ): Float {
        val direction: Direction? = KineticsHelper.directionBetween(target.blockPos, blockPos)
        if (direction != null) {
            if (stateTo.block is IRotate) {
                val rot = stateTo.block as IRotate
                if (rot.hasShaftTowards(
                        getLevel(),
                        target.blockPos,
                        stateTo,
                        direction
                    )) {
                    CoaxialGearBlock.updatePropagationOfLongShaft(stateFrom, level!!, blockPos)
                }
            }
        }

        return super.propagateRotationTo(target, stateFrom, stateTo, diff, connectedViaAxes, connectedViaCogs)
    }

    override fun tick() {
        super.tick()
        if (isVirtual || !hasLevel()) return
        if (blockState.getValue<Boolean>(CoaxialGearBlock.HAS_SHAFT) && !getLevel()!!.isClientSide()) { // It thinks getLevel() might be null (it's not)
            val axis: Direction.Axis = blockState.getValue(RotatedPillarKineticBlock.AXIS)
            var longShaftExists = false
            for (axisDirection in Direction.AxisDirection.values()) {
                val longShaftState = getLevel()!!.getBlockState(
                    blockPos.relative(
                        Direction.get(
                            axisDirection,
                            axis
                        )
                    )
                ) // It thinks getLevel() might be null (it's not)
                if ((CoggleBlocks.LONG_SHAFT.has(longShaftState) && longShaftState.getValue<Direction.Axis>(
                        RotatedPillarKineticBlock.AXIS
                    ) === axis) && longShaftState.getValue<Boolean>(DirectionalRotatedPillarKineticBlock.POSITIVE_AXIS_DIRECTION) == (axisDirection !== Direction.AxisDirection.POSITIVE)
                ) {
                    longShaftExists = true
                    break
                }
            }

            if (!longShaftExists) {
                getLevel()!!.setBlockAndUpdate(
                    blockPos,
                    AllBlocks.SHAFT.defaultState.setValue(RotatedPillarKineticBlock.AXIS, axis)
                )
                Block.popResource(getLevel(), blockPos, CoggleBlocks.COAXIAL_GEAR.asStack())
            }
        }
    }
};
