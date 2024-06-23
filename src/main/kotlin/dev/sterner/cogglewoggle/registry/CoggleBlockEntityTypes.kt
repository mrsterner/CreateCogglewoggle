package dev.sterner.cogglewoggle.registry

import com.simibubi.create.Create.REGISTRATE
import com.simibubi.create.content.kinetics.simpleRelays.BracketedKineticBlockEntityInstance
import com.simibubi.create.content.kinetics.simpleRelays.BracketedKineticBlockEntityRenderer
import com.tterrag.registrate.util.entry.BlockEntityEntry
import com.tterrag.registrate.util.nullness.NonNullFunction
import dev.sterner.cogglewoggle.common.blockentity.CoaxialGearBlockEntity
import dev.sterner.cogglewoggle.common.blockentity.DifferentialBlockEntity
import dev.sterner.cogglewoggle.common.blockentity.LongShaftBlockEntity
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import java.util.function.BiFunction


object CoggleBlockEntityTypes {


    val COAXIAL_GEAR: BlockEntityEntry<CoaxialGearBlockEntity> = REGISTRATE
        .blockEntity<CoaxialGearBlockEntity>(
            "coaxial_gear"
        ) { type: BlockEntityType<CoaxialGearBlockEntity?>?, pos: BlockPos?, state: BlockState? ->
            CoaxialGearBlockEntity(
                type!!, pos!!, state!!
            )
        }
            .instance({
                BiFunction {
                           materialManager, blockEntity -> BracketedKineticBlockEntityInstance(materialManager, blockEntity)
                }
                      },
                false
        )
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
            .instance(
                {
                    BiFunction {
                               materialManager, blockEntity -> BracketedKineticBlockEntityInstance(materialManager, blockEntity)
                    }
                },
                false
        )
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
        ) { type: BlockEntityType<DifferentialBlockEntity?>?, pos: BlockPos?, state: BlockState? ->
            DifferentialBlockEntity(
                type!!, pos!!, state!!
            )
        }

        .validBlock(DestroyBlocks.DIFFERENTIAL)
        .renderer { { DifferentialRenderer() } }
        .register()
}
