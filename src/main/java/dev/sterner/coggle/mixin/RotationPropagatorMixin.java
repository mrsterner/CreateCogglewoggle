package dev.sterner.coggle.mixin;

import dev.sterner.coggle.common.block.DirectionalRotatedPillarKineticBlock;
import dev.sterner.coggle.common.blockentity.LongShaftBlockEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.simibubi.create.content.kinetics.RotationPropagator;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

@Mixin(RotationPropagator.class)
public class RotationPropagatorMixin {

	/**
	 * Allow Kinetic Blocks to rotate the Gear end of a Long Shaft.
	 * @param from
	 * @param to
	 * @param cir
	 */
	@Inject(
			method = "getRotationSpeedModifier(Lcom/simibubi/create/content/kinetics/base/KineticBlockEntity;Lcom/simibubi/create/content/kinetics/base/KineticBlockEntity;)F",
			at = @At("TAIL"),
			cancellable = true,
			remap = false
	)
	private static void inGetRotationSpeedModifier(KineticBlockEntity from, KineticBlockEntity to, CallbackInfoReturnable<Float> cir) {
		BlockPos fromBlockPos = from.getBlockPos();
		if (to instanceof LongShaftBlockEntity) {
			Direction direction = DirectionalRotatedPillarKineticBlock.Companion.getDirection(to.getBlockState());
			if (LongShaftBlockEntity.Companion.connectedToLongShaft(from, to, to.getBlockPos().subtract(fromBlockPos))) {
				/* Copied from Create (see Axis <-> Axis) */
				cir.setReturnValue(1 / RotationPropagatorAccessor.invokeGetAxisModifier(from, direction.getOpposite()));
			};
		};
	};
};
