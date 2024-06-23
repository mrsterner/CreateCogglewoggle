package dev.sterner.coggle.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import com.simibubi.create.content.kinetics.RotationPropagator;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;

import net.minecraft.core.Direction;

@Mixin(RotationPropagator.class)
public interface RotationPropagatorAccessor {

	@Invoker(
			value = "getAxisModifier",
			remap = false
	)
	public static float invokeGetAxisModifier(KineticBlockEntity be, Direction direction) {
		return 0f; // This return value is ignored.
	};

	@Invoker(
			value = "getConveyedSpeed",
			remap = false
	)
	public static float invokeGetConveyedSpeed(KineticBlockEntity from, KineticBlockEntity to) {
		return 0f; // This return value is ignored
	};
};
