package dev.sterner.coggle.registry

import com.simibubi.create.AllBlocks
import com.simibubi.create.Create
import com.simibubi.create.content.kinetics.simpleRelays.BracketedKineticBlockModel
import com.simibubi.create.content.kinetics.simpleRelays.CogWheelBlock
import com.simibubi.create.content.kinetics.simpleRelays.CogwheelBlockItem
import com.simibubi.create.foundation.data.CreateRegistrate
import com.simibubi.create.foundation.data.ModelGen.customItemModel
import com.simibubi.create.foundation.data.TagGen
import com.tterrag.registrate.util.entry.BlockEntry
import com.tterrag.registrate.util.nullness.NonNullFunction
import dev.sterner.coggle.Cogglewoggle.REGISTRATE
import dev.sterner.coggle.common.block.*
import dev.sterner.coggle.common.item.CoaxialGearBlockItem
import net.minecraft.client.resources.model.BakedModel
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.material.MapColor
import java.util.function.Supplier


object CoggleBlocks {
    fun init() {

    }

    val COAXIAL_GEAR: BlockEntry<CoaxialGearBlock> = REGISTRATE.block<CoaxialGearBlock>("coaxial_gear") {
            properties -> CoaxialGearBlock(properties)
    }
        .initialProperties(AllBlocks.COGWHEEL)
        .properties { p ->
            p
                .sound(SoundType.WOOD)
                .mapColor(MapColor.DIRT)
                .noOcclusion()
        }
        .onRegister(CreateRegistrate.blockModel(Supplier<NonNullFunction<BakedModel?, out BakedModel>> {
            NonNullFunction<BakedModel?, BakedModel> { template: BakedModel? ->
                BracketedKineticBlockModel(template)
            }
        }))
        .transform(TagGen.axeOrPickaxe())
        .item { block, properties -> CoaxialGearBlockItem(block, properties) }
        .build()
        .register()


    val LONG_SHAFT: BlockEntry<LongShaftBlock> = REGISTRATE.block<LongShaftBlock>(
        "long_shaft"
    ) { properties: BlockBehaviour.Properties? ->
        LongShaftBlock(
            properties
        )
    }
        .initialProperties(AllBlocks.SHAFT)
        .onRegister(CreateRegistrate.blockModel {
            NonNullFunction<BakedModel?, BakedModel> { template: BakedModel? ->
                BracketedKineticBlockModel(
                    template
                )
            }
        })
        .register()

    val DIFFERENTIAL: BlockEntry<DifferentialBlock> = REGISTRATE.block<DifferentialBlock>("differential") {
            properties: BlockBehaviour.Properties? -> DifferentialBlock(properties!!)
    }
        .initialProperties(AllBlocks.LARGE_COGWHEEL)
        .properties { p ->
            p
                .noOcclusion()
                .sound(SoundType.WOOD)
                .mapColor(MapColor.DIRT)
        }
        .transform(TagGen.axeOrPickaxe())
        .item { block: CogWheelBlock?, builder: Item.Properties? -> CogwheelBlockItem(block, builder) }
        .transform(customItemModel())
        .register()

    val DUMMY_DIFFERENTIAL: BlockEntry<DummyDifferentialBlock> =
        REGISTRATE.block<DummyDifferentialBlock>("dummy_differential") {
                properties: BlockBehaviour.Properties? -> DummyDifferentialBlock(properties)
        }
            .initialProperties(DIFFERENTIAL)
            .register()

    val PLANETARY_GEARSET: BlockEntry<PlanetaryGearsetBlock> = REGISTRATE.block<PlanetaryGearsetBlock>(
        "planetary_gearset"
    ) { properties: BlockBehaviour.Properties? ->
        PlanetaryGearsetBlock(
            properties
        )
    }
        .initialProperties(AllBlocks.LARGE_COGWHEEL)
        .properties { p: BlockBehaviour.Properties ->
            p
                .noOcclusion()
                .sound(SoundType.WOOD)
                .mapColor(MapColor.DIRT)
        }.transform(TagGen.axeOrPickaxe())
        .item { block: PlanetaryGearsetBlock?, builder: Item.Properties? ->
            CogwheelBlockItem(
                block,
                builder
            )
        }
        .transform(customItemModel())
        .register()
}
