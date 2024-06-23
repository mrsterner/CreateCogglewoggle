package dev.sterner.coggle.common.item

import com.simibubi.create.AllBlocks
import com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock
import com.simibubi.create.content.kinetics.simpleRelays.CogwheelBlockItem
import com.simibubi.create.content.kinetics.simpleRelays.ShaftBlock
import com.simibubi.create.foundation.placement.IPlacementHelper
import com.simibubi.create.foundation.placement.PlacementHelpers
import com.simibubi.create.foundation.placement.PlacementOffset
import dev.sterner.coggle.common.block.CoaxialGearBlock
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import java.util.function.Predicate

class CoaxialGearBlockItem(block: CoaxialGearBlock?, properties: Properties?) :
    CogwheelBlockItem(block, properties) {
    init {
        PlacementHelpers.register(GearOnShaftPlacementHelper())
        PlacementHelpers.register(ShaftOnGearPlacementHelper())
    }

    override fun useOn(context: UseOnContext): InteractionResult {
        val level = context.level
        val pos = context.clickedPos
        val state = level.getBlockState(pos)
        val player = context.player ?: return InteractionResult.PASS
        if (ShaftBlock.isShaft(state)) {
            if (CoaxialGearBlock.tryMakeLongShaft(
                    state,
                    level,
                    pos,
                    Direction.getFacingAxis(player, state.getValue<Direction.Axis>(RotatedPillarKineticBlock.AXIS))
                )
            ) {
                if (!level.isClientSide() && !player.isCreative) {
                    context.itemInHand.shrink(1)
                }

                return InteractionResult.sidedSuccess(level.isClientSide())
            } else {
                /*TODO
                player.displayClientMessage(
                    DestroyLang.translate("tooltip.coaxial_gear.shaft_too_short").style(ChatFormatting.RED).component(),
                    true
                )

                 */
                return InteractionResult.SUCCESS
            }
        }

        return super.useOn(context)
    }

    private class GearOnShaftPlacementHelper : IPlacementHelper {
        override fun getItemPredicate(): Predicate<ItemStack> {
            return Predicate<ItemStack> { stack: ItemStack ->
                stack.item is BlockItem && CoaxialGearBlock.isCoaxialGear(
                    (stack.item as BlockItem).block
                )
            }
        }

        override fun getStatePredicate(): Predicate<BlockState> {
            return Predicate { state: BlockState? -> ShaftBlock.isShaft(state) }
        }

        override fun getOffset(
            player: Player,
            world: Level,
            state: BlockState,
            pos: BlockPos,
            ray: BlockHitResult
        ): PlacementOffset {
            return PlacementOffset.success(
                pos
            ) { s: BlockState ->
                s.setValue(
                    RotatedPillarKineticBlock.AXIS,
                    state.getValue(RotatedPillarKineticBlock.AXIS)
                )
            }
        }
    }

    private class ShaftOnGearPlacementHelper : IPlacementHelper {
        override fun getItemPredicate(): Predicate<ItemStack> {
            return Predicate { stack: ItemStack? -> AllBlocks.SHAFT.isIn(stack) }
        }

        override fun getStatePredicate(): Predicate<BlockState> {
            return Predicate { state -> CoaxialGearBlock.isCoaxialGear(state) }
        }

        override fun getOffset(
            player: Player,
            world: Level,
            state: BlockState,
            pos: BlockPos,
            ray: BlockHitResult
        ): PlacementOffset {
            return PlacementOffset.success(
                pos
            ) { s: BlockState ->
                s.setValue(
                    RotatedPillarKineticBlock.AXIS,
                    state.getValue(RotatedPillarKineticBlock.AXIS)
                )
            }
        }
    }
};
