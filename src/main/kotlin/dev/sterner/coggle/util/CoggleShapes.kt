package dev.sterner.coggle.util

import com.simibubi.create.AllShapes
import com.simibubi.create.foundation.utility.VoxelShaper
import net.minecraft.world.level.block.Block


object CoggleShapes {

    val COAXIAL_GEAR: VoxelShaper = shape(2.0, 6.0, 2.0, 14.0, 10.0, 14.0)
        .forAxis()


    val PLANETARY_GEARSET: VoxelShaper = shape(0.0, 4.5, 0.0, 16.0, 11.5, 16.0)
        .add(5.0, 0.0, 5.0, 11.0, 16.0, 11.0)
        .forAxis()

    private fun shape(x1: Double, y1: Double, z1: Double, x2: Double, y2: Double, z2: Double): AllShapes.Builder {
        return AllShapes.Builder(Block.box(x1, y1, z1, x2, y2, z2))
    }

    fun init() {

    }
}
