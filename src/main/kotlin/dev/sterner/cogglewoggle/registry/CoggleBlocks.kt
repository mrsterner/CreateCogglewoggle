package dev.sterner.cogglewoggle.registry

import com.simibubi.create.AllBlocks
import com.simibubi.create.Create.REGISTRATE
import com.simibubi.create.content.kinetics.simpleRelays.BracketedKineticBlockModel
import com.simibubi.create.content.kinetics.simpleRelays.CogWheelBlock
import com.simibubi.create.content.kinetics.simpleRelays.CogwheelBlockItem
import com.simibubi.create.foundation.data.CreateRegistrate
import com.simibubi.create.foundation.data.ModelGen.customItemModel
import com.simibubi.create.foundation.data.TagGen
import com.tterrag.registrate.util.entry.BlockEntry
import com.tterrag.registrate.util.nullness.NonNullFunction
import dev.sterner.cogglewoggle.common.block.CoaxialGearBlock
import dev.sterner.cogglewoggle.common.block.DifferentialBlock
import dev.sterner.cogglewoggle.common.block.DummyDifferentialBlock
import dev.sterner.cogglewoggle.common.block.LongShaftBlock
import dev.sterner.cogglewoggle.common.item.CoaxialGearBlockItem
import net.minecraft.client.resources.model.BakedModel
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.material.MapColor
import java.util.function.Supplier


object CoggleBlocks {

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
            properties: BlockBehaviour.Properties? -> DifferentialBlock(properties)
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
        REGISTRATE.block("dummy_differential") { DummyDifferentialBlock() }
            .initialProperties(DIFFERENTIAL)
            .register()
}
