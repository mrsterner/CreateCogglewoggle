package dev.sterner.coggle.common.block

import com.simibubi.create.content.kinetics.base.KineticBlockEntity
import com.simibubi.create.content.kinetics.simpleRelays.CogWheelBlock
import dev.sterner.coggle.registry.CoggleBlockEntityTypes
import dev.sterner.coggle.registry.CoggleBlocks
import dev.sterner.coggle.util.CoggleShapes
import net.minecraft.core.BlockPos
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape

class PlanetaryGearsetBlock(properties: Properties?) : CogWheelBlock(true, properties) {
    init {
        registerDefaultState(defaultBlockState().setValue(SUPPORT_ONLY, false))
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(SUPPORT_ONLY)
        super.createBlockStateDefinition(builder)
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState? {
        return super.getStateForPlacement(context)!!
            .setValue(SUPPORT_ONLY, true)
    }

    override fun onPlace(state: BlockState, level: Level, pos: BlockPos, oldState: BlockState, isMoving: Boolean) {
        if (!state.getValue(SUPPORT_ONLY)) level.setBlockAndUpdate(pos, state.setValue(SUPPORT_ONLY, true))
        super.onPlace(state, level, pos, oldState, isMoving)
    }

    override fun getShape(
        state: BlockState,
        worldIn: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape {
        return CoggleShapes.PLANETARY_GEARSET.get(state.getValue(AXIS))
    }

    override fun isLargeCog(): Boolean {
        return true
    }

    override fun getBlockEntityType(): BlockEntityType<out KineticBlockEntity?> {
        return CoggleBlockEntityTypes.PLANETARY_GEARSET.get()
    }

    companion object {
        val SUPPORT_ONLY: BooleanProperty = BooleanProperty.create("support_only")
    }
};
