package dev.sterner.coggle.client.renderer


import com.mojang.blaze3d.vertex.PoseStack
import com.simibubi.create.content.kinetics.base.KineticBlockEntity
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer
import com.simibubi.create.content.kinetics.simpleRelays.BracketedKineticBlockEntityRenderer
import com.simibubi.create.foundation.render.CachedBufferer
import com.simibubi.create.foundation.utility.AnimationTickHolder
import dev.sterner.coggle.common.block.DirectionalRotatedPillarKineticBlock
import dev.sterner.coggle.common.blockentity.DifferentialBlockEntity
import dev.sterner.coggle.util.CogglePartials
import dev.sterner.coggle.util.KineticsHelper.rotateToFace
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.core.Direction
import net.minecraft.util.Mth

class DifferentialRenderer(context: BlockEntityRendererProvider.Context?) :
    KineticBlockEntityRenderer<DifferentialBlockEntity>(context) {
    // It thinks getLevel() might be null
    override fun renderSafe(
        differential: DifferentialBlockEntity,
        partialTicks: Float,
        ms: PoseStack,
        buffer: MultiBufferSource,
        light: Int,
        overlay: Int
    ) {
        //if (Backend.canUseInstancing(planetaryGearsetBlockEntity.getLevel())) return;
        if (!differential.hasLevel()) return

        val state = getRenderedBlockState(differential)
        val face = DirectionalRotatedPillarKineticBlock.getDirection(state)
        val axis = face.axis
        val vbSolid = buffer.getBuffer(RenderType.solid())

        val time = AnimationTickHolder.getRenderTime(differential.level)
        val ringGearOffset = Mth.PI * getRotationOffsetForPosition(differential, differential.blockPos, axis) / 180f
        val ringGearAngle = ((time * differential.speed * 3f / 10 + ringGearOffset) % 360) / 180 * Mth.PI

        val inputPos = differential.blockPos.relative(face)
        val controlPos = differential.blockPos.relative(face.opposite)

        val inputBE = differential.level!!.getBlockEntity(inputPos)
        val controlBE = differential.level!!.getBlockEntity(controlPos)

        val inputShaftOffset = Mth.PI * BracketedKineticBlockEntityRenderer.getShaftAngleOffset(axis, inputPos) / 180f
        val controlShaftOffset =
            Mth.PI * BracketedKineticBlockEntityRenderer.getShaftAngleOffset(axis, controlPos) / 180f

        var inputCogAngle = 0f
        var controlCogAngle = 0f

        if (differential.propagatesToMe(inputPos, face.opposite) && inputBE is KineticBlockEntity) inputCogAngle =
            (time * differential.getPropagatedSpeed(inputBE, face) * 3f / 10 % 360) / 180 * Mth.PI
        if (differential.propagatesToMe(controlPos, face) && controlBE is KineticBlockEntity) controlCogAngle =
            (time * differential.getPropagatedSpeed(controlBE, face.opposite) * 3f / 10 % 360) / 180 * Mth.PI

        val ringGear = CachedBufferer.partialDirectional(
            CogglePartials.DIFFERENTIAL_RING_GEAR, state, face
        ) { rotateToFace(face) }
        kineticRotationTransform(ringGear, differential, axis, ringGearAngle + ringGearOffset, light)
        ringGear.renderInto(ms, vbSolid)

        val eastGear = CachedBufferer.partialDirectional(
            CogglePartials.DIFFERENTIAL_EAST_GEAR, state, face
        ) { rotateToFace(face) }
        kineticRotationTransform(eastGear, differential, axis, ringGearAngle + ringGearOffset, light)
        kineticRotationTransform(
            eastGear, differential, if (axis === Direction.Axis.X) Direction.Axis.Z else Direction.Axis.X,
            ((controlCogAngle - inputCogAngle) / 2) * (if (axis === Direction.Axis.Z) -1 else 1), light
        )
        eastGear.renderInto(ms, vbSolid)

        val westGear = CachedBufferer.partialDirectional(
            CogglePartials.DIFFERENTIAL_WEST_GEAR, state, face
        ) { rotateToFace(face) }
        kineticRotationTransform(westGear, differential, axis, ringGearAngle + ringGearOffset, light)
        kineticRotationTransform(
            westGear, differential, if (axis === Direction.Axis.X) Direction.Axis.Z else Direction.Axis.X,
            ((inputCogAngle - controlCogAngle) / 2) * (if (axis === Direction.Axis.Z) -1 else 1), light
        )
        westGear.renderInto(ms, vbSolid)

        val topGear = CachedBufferer.partialDirectional(
            CogglePartials.DIFFERENTIAL_CONTROL_GEAR, state, face
        ) { rotateToFace(face) }
        kineticRotationTransform(topGear, differential, axis, controlCogAngle + ringGearOffset, light)
        topGear.renderInto(ms, vbSolid)

        val topShaft = CachedBufferer.partialDirectional(
            CogglePartials.DIFFERENTIAL_CONTROL_SHAFT, state, face
        ) { rotateToFace(face) }
        kineticRotationTransform(topShaft, differential, axis, controlCogAngle + controlShaftOffset, light)
        topShaft.renderInto(ms, vbSolid)

        val bottomGear = CachedBufferer.partialDirectional(
            CogglePartials.DIFFERENTIAL_INPUT_GEAR, state, face
        ) { rotateToFace(face) }
        kineticRotationTransform(bottomGear, differential, axis, inputCogAngle + ringGearOffset, light)
        bottomGear.renderInto(ms, vbSolid)

        val bottomShaft = CachedBufferer.partialDirectional(
            CogglePartials.DIFFERENTIAL_INPUT_SHAFT, state, face
        ) { rotateToFace(face) }
        kineticRotationTransform(bottomShaft, differential, axis, inputCogAngle + inputShaftOffset, light)
        bottomShaft.renderInto(ms, vbSolid)
    }
};
