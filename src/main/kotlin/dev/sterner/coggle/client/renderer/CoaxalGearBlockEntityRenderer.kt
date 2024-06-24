package dev.sterner.coggle.client.renderer

import com.jozufozu.flywheel.backend.Backend
import com.mojang.blaze3d.vertex.PoseStack
import com.simibubi.create.AllBlocks
import com.simibubi.create.AllPartialModels
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer
import com.simibubi.create.content.kinetics.simpleRelays.BracketedKineticBlockEntity
import com.simibubi.create.content.kinetics.simpleRelays.SimpleKineticBlockEntity
import com.simibubi.create.foundation.render.CachedBufferer
import com.simibubi.create.foundation.utility.AnimationTickHolder
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction


class CoaxalGearBlockEntityRenderer(context: BlockEntityRendererProvider.Context?) :
    KineticBlockEntityRenderer<BracketedKineticBlockEntity>(context) {

    override fun renderSafe(
        be: BracketedKineticBlockEntity, partialTicks: Float, ms: PoseStack,
        buffer: MultiBufferSource, light: Int, overlay: Int
    ) {
        if (!AllBlocks.LARGE_COGWHEEL.has(be.blockState)) {
            super.renderSafe(be, partialTicks, ms, buffer, light, overlay)
            return
        }

        // Large cogs sometimes have to offset their teeth by 11.25 degrees in order to
        // mesh properly
        val axis = getRotationAxisOf(be)
        val facing = Direction.fromAxisAndDirection(axis, Direction.AxisDirection.POSITIVE)
        renderRotatingBuffer(
            be,
            CachedBufferer.partialFacingVertical(AllPartialModels.SHAFTLESS_LARGE_COGWHEEL, be.blockState, facing),
            ms, buffer.getBuffer(RenderType.solid()), light
        )

        val angle = getAngleForLargeCogShaft(be, axis)
        val shaft =
            CachedBufferer.partialFacingVertical(AllPartialModels.COGWHEEL_SHAFT, be.blockState, facing)
        kineticRotationTransform(shaft, be, axis, angle, light)
        shaft.renderInto(ms, buffer.getBuffer(RenderType.solid()))
    }

    companion object {
        fun getAngleForLargeCogShaft(be: SimpleKineticBlockEntity, axis: Direction.Axis): Float {
            val pos = be.blockPos
            val offset = getShaftAngleOffset(axis, pos)
            val time = AnimationTickHolder.getRenderTime(be.level)
            val angle = ((time * be.speed * 3f / 10 + offset) % 360) / 180 * Math.PI.toFloat()
            return angle
        }

        fun getShaftAngleOffset(axis: Direction.Axis, pos: BlockPos): Float {
            var offset = 0f
            val d = (((if ((axis === Direction.Axis.X)) 0 else pos.x) + (if ((axis === Direction.Axis.Y)) 0 else pos.y)
                    + (if ((axis === Direction.Axis.Z)) 0 else pos.z)) % 2).toDouble()
            if (d == 0.0) offset = 22.5f
            return offset
        }
    }
}
