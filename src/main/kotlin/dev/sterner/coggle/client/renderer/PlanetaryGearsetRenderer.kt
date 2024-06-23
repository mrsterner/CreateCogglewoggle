package dev.sterner.coggle.client.renderer


import com.jozufozu.flywheel.backend.Backend
import com.mojang.blaze3d.vertex.PoseStack
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer
import com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock
import com.simibubi.create.content.kinetics.simpleRelays.BracketedKineticBlockEntityRenderer
import com.simibubi.create.foundation.render.CachedBufferer
import com.simibubi.create.foundation.utility.AnimationTickHolder
import dev.sterner.coggle.common.blockentity.PlanetaryGearsetBlockEntity
import dev.sterner.coggle.util.CogglePartials
import dev.sterner.coggle.util.KineticsHelper.rotateToAxis
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.core.Direction
import net.minecraft.util.Mth

class PlanetaryGearsetRenderer(context: BlockEntityRendererProvider.Context?) :
    KineticBlockEntityRenderer<PlanetaryGearsetBlockEntity?>(context) {

    override fun renderSafe(
        planetaryGearsetBlockEntity: PlanetaryGearsetBlockEntity?,
        partialTicks: Float,
        ms: PoseStack,
        buffer: MultiBufferSource,
        light: Int,
        overlay: Int
    ) {
        val state = getRenderedBlockState(planetaryGearsetBlockEntity)
        val axis = state.getValue(RotatedPillarKineticBlock.AXIS)
        val vbSolid = buffer.getBuffer(RenderType.solid())

        val time = AnimationTickHolder.getRenderTime(planetaryGearsetBlockEntity!!.level)
        val offset1 = Mth.PI * getRotationOffsetForPosition(
            planetaryGearsetBlockEntity,
            planetaryGearsetBlockEntity.blockPos,
            axis
        ) / 180f
        val offset2 = Mth.PI * BracketedKineticBlockEntityRenderer.getShaftAngleOffset(
            axis,
            planetaryGearsetBlockEntity.blockPos
        ) / 180f
        val angle = ((time * planetaryGearsetBlockEntity.speed * 3f / 10 + offset1) % 360) / 180 * Mth.PI

        val ringGear = CachedBufferer.partialDirectional(
            CogglePartials.PG_RING_GEAR, state, Direction.get(Direction.AxisDirection.POSITIVE, axis)
        ) { rotateToAxis(axis) }
        kineticRotationTransform(ringGear, planetaryGearsetBlockEntity, axis, angle + offset1, light)
        ringGear.renderInto(ms, vbSolid)

        val sunGear = CachedBufferer.partialDirectional(
            CogglePartials.PG_SUN_GEAR, state, Direction.get(Direction.AxisDirection.POSITIVE, axis)
        ) { rotateToAxis(axis) }
        kineticRotationTransform(
            sunGear, planetaryGearsetBlockEntity, axis,
            (-2 * angle) + offset2, light
        )
        sunGear.renderInto(ms, vbSolid)

        for (direction in Direction.entries) {
            if (direction.axis === axis) continue
            val planetGear = CachedBufferer.partialDirectional(
                CogglePartials.PG_PLANET_GEAR, state, Direction.get(
                    Direction.AxisDirection.POSITIVE, axis
                )
            ) { rotateToAxis(axis) }
            planetGear.translate(direction.step().mul(6.25f / 16f))
            kineticRotationTransform(
                planetGear, planetaryGearsetBlockEntity, axis,
                (2 * angle) + offset2, light
            )
            planetGear.renderInto(ms, vbSolid)
        }
    }
};
