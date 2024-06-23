package dev.sterner.coggle.util

import com.jozufozu.flywheel.core.PartialModel
import dev.sterner.coggle.Cogglewoggle
import net.minecraft.resources.ResourceLocation

object CogglePartials {
    //val AIR: PartialModel = PartialModel(ResourceLocation("minecraft", "block/air"))

    // Planetary Gearset
    val PG_SUN_GEAR = block("planetary_gearset/sun_gear")
    val PG_PLANET_GEAR = block("planetary_gearset/planet_gear")
    val PG_RING_GEAR = block("planetary_gearset/ring_gear")

    // Differential
    val DIFFERENTIAL_RING_GEAR: PartialModel = block("differential/ring_gear")
    val DIFFERENTIAL_INPUT_GEAR: PartialModel = block("differential/input_gear")
    val DIFFERENTIAL_CONTROL_GEAR: PartialModel = block("differential/control_gear")
    val DIFFERENTIAL_EAST_GEAR: PartialModel = block("differential/east_gear")
    val DIFFERENTIAL_WEST_GEAR: PartialModel = block("differential/west_gear")
    val DIFFERENTIAL_INPUT_SHAFT: PartialModel = block("differential/input_shaft")
    val DIFFERENTIAL_CONTROL_SHAFT: PartialModel = block("differential/control_shaft")


    private fun block(path: String): PartialModel { //copied from Create source code
        return PartialModel(Cogglewoggle.asResource("block/$path"))
    }

    fun init() {}
}
