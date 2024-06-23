package dev.sterner.cogglewoggle.common.block

import com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock
import net.minecraft.core.Direction
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BooleanProperty


abstract class DirectionalRotatedPillarKineticBlock(properties: Properties?) : RotatedPillarKineticBlock(properties) {

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block?, BlockState?>) {
        super.createBlockStateDefinition(builder)
        builder.add(POSITIVE_AXIS_DIRECTION)
    }

    companion object {
        var POSITIVE_AXIS_DIRECTION: BooleanProperty = BooleanProperty.create("positive_axis_direction")

        fun getDirection(state: BlockState): Direction {
            return Direction.get(
                if (state.getValue(POSITIVE_AXIS_DIRECTION)) Direction.AxisDirection.POSITIVE else Direction.AxisDirection.NEGATIVE,
                state.getValue(
                    AXIS
                )
            )
        }
    }
};
