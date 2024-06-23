package dev.sterner.coggle.client.ponder

import com.simibubi.create.Create
import com.simibubi.create.foundation.ponder.PonderRegistrationHelper
import com.simibubi.create.foundation.ponder.PonderStoryBoardEntry.PonderStoryBoard
import com.simibubi.create.foundation.ponder.SceneBuilder
import com.simibubi.create.foundation.ponder.SceneBuildingUtil
import com.simibubi.create.infrastructure.ponder.scenes.KineticsScenes
import dev.sterner.coggle.registry.CoggleBlocks

object CogglePonderIndex {

    val HELPER: PonderRegistrationHelper = PonderRegistrationHelper("coggle")
    val CREATE_HELPER: PonderRegistrationHelper = PonderRegistrationHelper(Create.ID)

    fun register() {

        // Coaxial Gear
        CREATE_HELPER.forComponents(CoggleBlocks.COAXIAL_GEAR)
            .addStoryBoard("cog/small",
                PonderStoryBoard { scene: SceneBuilder?, util: SceneBuildingUtil? ->
                    KineticsScenes.cogAsRelay(
                        scene,
                        util
                    )
                })
        HELPER.forComponents(CoggleBlocks.COAXIAL_GEAR)
            .addStoryBoard("coaxial_gear/shaftless", CoggleScenes::coaxialGearShaftless)
            .addStoryBoard("coaxial_gear/through", CoggleScenes::coaxialGearThrough)


        // Differential
        HELPER.forComponents(CoggleBlocks.DIFFERENTIAL)
            .addStoryBoard("differential", CoggleScenes::differential)


        // Double Cardan Shaft
        HELPER.forComponents(CoggleBlocks.DOUBLE_CARDAN_SHAFT)
            .addStoryBoard("double_cardan_shaft", CoggleScenes::doubleCardanShaft)


        // Planetary Gearset
        CREATE_HELPER.forComponents(CoggleBlocks.PLANETARY_GEARSET)
            .addStoryBoard("cog/speedup",
                PonderStoryBoard { scene: SceneBuilder?, util: SceneBuildingUtil? ->
                    KineticsScenes.cogsSpeedUp(
                        scene,
                        util
                    )
                })
            .addStoryBoard("cog/large",
                PonderStoryBoard { scene: SceneBuilder?, util: SceneBuildingUtil? ->
                    KineticsScenes.largeCogAsRelay(
                        scene,
                        util
                    )
                })
        HELPER.forComponents(CoggleBlocks.PLANETARY_GEARSET)
            .addStoryBoard("planetary_gearset", CoggleScenes::planetaryGearset)
    }
}
