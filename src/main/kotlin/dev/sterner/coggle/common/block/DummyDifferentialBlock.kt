package dev.sterner.coggle.common.block

import dev.sterner.coggle.registry.CoggleBlocks
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.RandomSource
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.state.BlockState


class DummyDifferentialBlock(properties: Properties?) : DirectionalRotatedPillarKineticBlock(properties) {
    @Deprecated("")
    override fun onPlace(state: BlockState, level: Level, pos: BlockPos, oldState: BlockState, isMoving: Boolean) {
        level.scheduleTick(pos, CoggleBlocks.DUMMY_DIFFERENTIAL.get(), 2)
    }

    override fun tick(state: BlockState, level: ServerLevel, pos: BlockPos, random: RandomSource) {
        level.setBlockAndUpdate(
            pos, CoggleBlocks.DIFFERENTIAL.getDefaultState().setValue(AXIS, state.getValue(AXIS)).setValue(
                POSITIVE_AXIS_DIRECTION, state.getValue(POSITIVE_AXIS_DIRECTION)
            )
        )
    }

    override fun hasShaftTowards(world: LevelReader, pos: BlockPos, state: BlockState, face: Direction): Boolean {
        return false
    }

    override fun getRotationAxis(state: BlockState): Direction.Axis {
        return state.getValue(AXIS)
    }
};
