package dev.sterner.coggle.registry

import com.simibubi.create.content.kinetics.simpleRelays.BracketedKineticBlockEntityInstance
import com.simibubi.create.content.kinetics.simpleRelays.BracketedKineticBlockEntityRenderer
import com.tterrag.registrate.util.entry.BlockEntityEntry
import com.tterrag.registrate.util.nullness.NonNullFunction
import com.tterrag.registrate.util.nullness.NonNullSupplier
import dev.sterner.coggle.Cogglewoggle.REGISTRATE
import dev.sterner.coggle.client.renderer.CoaxalGearBlockEntityRenderer
import dev.sterner.coggle.client.renderer.DifferentialRenderer
import dev.sterner.coggle.client.renderer.DoubleCardanShaftRenderer
import dev.sterner.coggle.client.renderer.PlanetaryGearsetRenderer
import dev.sterner.coggle.common.blockentity.*
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import java.util.function.BiFunction


object CoggleBlockEntityTypes {
    fun init() {

    }

    val COAXIAL_GEAR: BlockEntityEntry<CoaxialGearBlockEntity> = REGISTRATE
        .blockEntity<CoaxialGearBlockEntity>(
            "coaxial_gear"
        ) { type: BlockEntityType<CoaxialGearBlockEntity?>?, pos: BlockPos?, state: BlockState? ->
            CoaxialGearBlockEntity(
                type!!, pos!!, state!!
            )
        }
        .instance({ BiFunction { materialManager, blockEntity -> BracketedKineticBlockEntityInstance(materialManager, blockEntity) } }, false)

        .validBlocks(CoggleBlocks.COAXIAL_GEAR)
        .renderer {
            NonNullFunction<BlockEntityRendererProvider.Context?, BlockEntityRenderer<in CoaxialGearBlockEntity?>> { context: BlockEntityRendererProvider.Context? ->
                BracketedKineticBlockEntityRenderer(
                    context!!
                )
            }
        }
        .register()


    val LONG_SHAFT: BlockEntityEntry<LongShaftBlockEntity> = REGISTRATE
        .blockEntity<LongShaftBlockEntity>(
            "long_shaft"
        ) { type: BlockEntityType<LongShaftBlockEntity?>?, pos: BlockPos?, state: BlockState? ->
            LongShaftBlockEntity(
                type!!, pos!!, state!!
            )
        }
        .instance({ BiFunction { materialManager, blockEntity -> BracketedKineticBlockEntityInstance(materialManager, blockEntity) } }, false)
        .validBlocks(CoggleBlocks.LONG_SHAFT)
        .renderer {
            NonNullFunction<BlockEntityRendererProvider.Context?, BlockEntityRenderer<in LongShaftBlockEntity?>> { context: BlockEntityRendererProvider.Context? ->
                BracketedKineticBlockEntityRenderer(
                    context!!
                )
            }
        }
        .register()


    val DIFFERENTIAL: BlockEntityEntry<DifferentialBlockEntity> = REGISTRATE
        .blockEntity<DifferentialBlockEntity>(
            "differential"
        ) { type, pos, state ->
            DifferentialBlockEntity(type, pos!!, state!!)
        }
        .validBlock(CoggleBlocks.DIFFERENTIAL)
        .renderer {
            NonNullFunction { context -> DifferentialRenderer(context!!) }
        }
        .register()


    val PLANETARY_GEARSET: BlockEntityEntry<PlanetaryGearsetBlockEntity> = REGISTRATE
        .blockEntity<PlanetaryGearsetBlockEntity>("planetary_gearset") { type, pos, state -> PlanetaryGearsetBlockEntity(type!!, pos!!, state!!) }
        .validBlocks(CoggleBlocks.PLANETARY_GEARSET)
        .renderer { NonNullFunction { context -> PlanetaryGearsetRenderer(context!!) } }
        .register()


    val DOUBLE_CARDAN_SHAFT: BlockEntityEntry<DoubleCardanShaftBlockEntity> = REGISTRATE
        .blockEntity<DoubleCardanShaftBlockEntity>("double_cardan_shaft") { type, pos, state ->  DoubleCardanShaftBlockEntity(type!!, pos!!, state!!) }
        .validBlock(CoggleBlocks.DOUBLE_CARDAN_SHAFT)
        .renderer {  NonNullFunction { context -> DoubleCardanShaftRenderer(context!!) }  }
        .register()
}
