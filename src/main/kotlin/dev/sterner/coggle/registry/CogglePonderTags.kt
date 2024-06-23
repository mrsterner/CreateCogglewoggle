package dev.sterner.coggle.registry


import com.simibubi.create.foundation.ponder.PonderRegistry
import com.simibubi.create.foundation.ponder.PonderTag
import com.simibubi.create.infrastructure.ponder.AllPonderTags
import dev.sterner.coggle.Cogglewoggle

object CogglePonderTags {

    val COGGLE: PonderTag = PonderTag(Cogglewoggle.asResource("coggle"))
        .item(CoggleBlocks.DIFFERENTIAL.asItem())
        .addToIndex()

    fun register() {

        PonderRegistry.TAGS.forTag(COGGLE)
            .add(CoggleBlocks.COAXIAL_GEAR)
            .add(CoggleBlocks.DIFFERENTIAL)
            .add(CoggleBlocks.DOUBLE_CARDAN_SHAFT)
            .add(CoggleBlocks.PLANETARY_GEARSET)


        PonderRegistry.TAGS.forTag(AllPonderTags.KINETIC_RELAYS)
            .add(CoggleBlocks.COAXIAL_GEAR)
            .add(CoggleBlocks.DIFFERENTIAL)
            .add(CoggleBlocks.DOUBLE_CARDAN_SHAFT)
            .add(CoggleBlocks.PLANETARY_GEARSET)

    }
};
