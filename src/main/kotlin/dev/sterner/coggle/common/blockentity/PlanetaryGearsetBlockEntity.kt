package dev.sterner.coggle.common.blockentity

import com.simibubi.create.content.kinetics.base.IRotate
import com.simibubi.create.content.kinetics.base.KineticBlockEntity
import com.simibubi.create.content.kinetics.transmission.SplitShaftBlockEntity
import com.simibubi.create.foundation.block.IBE
import dev.sterner.coggle.mixin.RotationPropagatorAccessor
import dev.sterner.coggle.registry.CoggleBlockEntityTypes
import dev.sterner.coggle.registry.CoggleBlocks
import dev.sterner.coggle.util.KineticsHelper.addLargeCogwheelPropagationLocations
import dev.sterner.coggle.util.KineticsHelper.directionBetween
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import kotlin.math.sign

class PlanetaryGearsetBlockEntity(typeIn: BlockEntityType<PlanetaryGearsetBlockEntity>, pos: BlockPos?, state: BlockState?) :
    SplitShaftBlockEntity(typeIn, pos, state) {

    override fun propagateRotationTo(
        target: KineticBlockEntity,
        stateFrom: BlockState,
        stateTo: BlockState,
        diff: BlockPos,
        connectedViaAxes: Boolean,
        connectedViaCogs: Boolean
    ): Float {
        if (connectedViaAxes || LongShaftBlockEntity.connectedToLongShaft(this, target, diff)) {
            if (CoggleBlocks.PLANETARY_GEARSET.has(stateTo)) return 0.0f
            return (sign(
                RotationPropagatorAccessor.invokeGetAxisModifier(
                    target, directionBetween(
                        target.blockPos,
                        blockPos
                    )
                ).toDouble()
            ) * -2).toFloat()
        }

        return super.propagateRotationTo(target, stateFrom, stateTo, diff, connectedViaAxes, connectedViaCogs)
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

    override fun getRotationSpeedModifier(face: Direction): Float {
        return (-2).toFloat()
    }

    override fun canPropagateDiagonally(block: IRotate, state: BlockState): Boolean {
        return true
    }
}
