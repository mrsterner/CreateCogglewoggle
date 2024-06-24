package dev.sterner.coggle.common.blockentity

import com.simibubi.create.content.kinetics.transmission.SplitShaftBlockEntity
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState

class DoubleCardanShaftBlockEntity(type: BlockEntityType<*>?, pos: BlockPos?, state: BlockState?) :
    SplitShaftBlockEntity(type, pos, state) {

    override fun getRotationSpeedModifier(face: Direction): Float {
        if (hasSource()) {
            if (face == sourceFacing) return 1f
            if (sourceFacing.axisDirection == face.axisDirection) return (-1).toFloat()
        }

        return 1f
    }

    override fun isNoisy(): Boolean {
        return false
    }
};
