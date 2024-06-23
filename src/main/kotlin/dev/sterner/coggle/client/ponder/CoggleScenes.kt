package dev.sterner.coggle.client.ponder

import com.simibubi.create.AllBlocks
import com.simibubi.create.AllItems
import com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock
import com.simibubi.create.foundation.ponder.PonderPalette
import com.simibubi.create.foundation.ponder.SceneBuilder
import com.simibubi.create.foundation.ponder.SceneBuildingUtil
import com.simibubi.create.foundation.ponder.element.InputWindowElement
import com.simibubi.create.foundation.utility.Pointing
import dev.sterner.coggle.common.block.CoaxialGearBlock
import dev.sterner.coggle.common.block.DirectionalRotatedPillarKineticBlock
import dev.sterner.coggle.common.block.DoubleCardanShaftBlock
import dev.sterner.coggle.registry.CoggleBlocks
import net.minecraft.core.Direction
import net.minecraft.world.phys.Vec3

object CoggleScenes {
    fun coaxialGearShaftless(scene: SceneBuilder, util: SceneBuildingUtil) {
        scene.title("coaxial_gear_shaftless", "This text is defined in a language file.")
        scene.configureBasePlate(0, 0, 5)
        scene.showBasePlate()

        scene.idle(5)
        scene.world.showSection(util.select.position(2, 0, 5), Direction.NORTH)
        scene.idle(5)
        scene.world.showSection(util.select.fromTo(3, 1, 2, 3, 1, 5), Direction.DOWN)
        scene.idle(5)
        scene.world.showSection(util.select.position(2, 1, 2), Direction.EAST)
        scene.idle(5)
        scene.overlay.showText(60)
            .text("This text is defined in a language file.")
            .attachKeyFrame()
            .pointAt(util.vector.blockSurface(util.grid.at(2, 1, 2), Direction.WEST))
        scene.idle(80)

        scene.world.hideSection(util.select.position(3, 1, 4), Direction.EAST)
        scene.idle(15)
        val belt = scene.world.showIndependentSection(util.select.fromTo(3, 3, 4, 4, 3, 4), Direction.DOWN)
        scene.world.moveSection(belt, Vec3(0.0, -2.0, 0.0), 10)
        scene.idle(10)
        scene.world.showSection(util.select.fromTo(4, 1, 1, 4, 1, 4), Direction.SOUTH)
        scene.idle(5)

        val cogs = arrayOf(intArrayOf(3, 1), intArrayOf(2, 1), intArrayOf(1, 1), intArrayOf(1, 2))
        for (cog in cogs) {
            scene.idle(5)
            scene.world.showSection(util.select.position(cog[0], 1, cog[1]), Direction.EAST)
        }



        scene.overlay.showText(100)
            .text("This text is defined in a language file.")
            .attachKeyFrame()
            .pointAt(util.vector.blockSurface(util.grid.at(1, 1, 2), Direction.UP))
        scene.idle(20)

        scene.effects.rotationDirectionIndicator(util.grid.at(1, 1, 1))
        scene.effects.rotationDirectionIndicator(util.grid.at(1, 1, 2))
        scene.idle(100)

        scene.markAsFinished()
    }

    fun coaxialGearThrough(scene: SceneBuilder, util: SceneBuildingUtil) {
        scene.title("coaxial_gear_through", "This text is defined in a language file.")
        scene.configureBasePlate(0, 0, 5)
        scene.showBasePlate()

        val verticalShaft1 = util.select.fromTo(3, 2, 2, 3, 3, 2)
        scene.world.setKineticSpeed(verticalShaft1, 0f)
        scene.world.showSection(verticalShaft1, Direction.DOWN)
        scene.idle(30)

        val coaxialGear1 = util.grid.at(3, 2, 2)
        val longShaft1 = util.grid.at(3, 3, 2)

        scene.overlay.showText(60)
            .text("This text is defined in a language file.")
            .attachKeyFrame()
            .pointAt(util.vector.blockSurface(coaxialGear1, Direction.UP))
        scene.idle(20)
        scene.overlay.showControls(
            InputWindowElement(util.vector.blockSurface(coaxialGear1, Direction.NORTH), Pointing.RIGHT)
                .withItem(CoggleBlocks.COAXIAL_GEAR.asStack()),
            60
        )
        scene.idle(5)
        scene.world.setBlock(
            coaxialGear1,
            CoggleBlocks.COAXIAL_GEAR.getDefaultState().setValue(CoaxialGearBlock.HAS_SHAFT, true),
            false
        )
        scene.world.setBlock(
            longShaft1,
            CoggleBlocks.LONG_SHAFT.getDefaultState().setValue(RotatedPillarKineticBlock.AXIS, Direction.Axis.Y)
                .setValue(DirectionalRotatedPillarKineticBlock.POSITIVE_AXIS_DIRECTION, false),
            false
        )
        scene.idle(55)

        scene.world.showSection(util.select.fromTo(1, 2, 2, 1, 3, 2), Direction.DOWN)
        scene.idle(20)

        val coaxialGear2 = util.grid.at(1, 2, 2)
        val longShaft2 = util.grid.at(1, 3, 2)

        scene.overlay.showText(60)
            .text("This text is defined in a language file.")
            .pointAt(util.vector.blockSurface(coaxialGear2, Direction.UP))
        scene.idle(20)
        scene.overlay.showControls(
            InputWindowElement(util.vector.blockSurface(coaxialGear2, Direction.NORTH), Pointing.RIGHT)
                .withItem(AllBlocks.SHAFT.asStack()),
            60
        )
        scene.idle(5)
        scene.world.setBlock(
            coaxialGear2,
            CoggleBlocks.COAXIAL_GEAR.getDefaultState().setValue(CoaxialGearBlock.HAS_SHAFT, true),
            false
        )
        scene.world.setBlock(
            longShaft2,
            CoggleBlocks.LONG_SHAFT.getDefaultState().setValue(RotatedPillarKineticBlock.AXIS, Direction.Axis.Y)
                .setValue(DirectionalRotatedPillarKineticBlock.POSITIVE_AXIS_DIRECTION, false),
            false
        )
        scene.idle(65)

        scene.overlay.showText(80)
            .text("This text is defined in a language file.")
            .placeNearTarget()
            .attachKeyFrame()
            .colored(PonderPalette.RED)
        scene.idle(100)

        scene.world.showSection(util.select.fromTo(1, 0, 5, 3, 1, 5), Direction.NORTH)
        scene.idle(5)
        scene.world.showSection(util.select.fromTo(1, 1, 4, 3, 1, 4), Direction.DOWN)
        scene.idle(5)
        scene.world.showSection(util.select.fromTo(1, 1, 3, 3, 1, 3), Direction.DOWN)
        scene.idle(5)
        scene.world.showSection(util.select.fromTo(1, 1, 2, 3, 1, 2), Direction.SOUTH)
        scene.world.setKineticSpeed(util.select.position(longShaft1), -32f)
        scene.world.setKineticSpeed(util.select.position(longShaft2), -32f)
        scene.idle(10)

        scene.overlay.showText(80)
            .text("This text is defined in a language file.")
            .attachKeyFrame()
            .pointAt(util.vector.blockSurface(coaxialGear2, Direction.UP))
        scene.idle(100)

        val cogwheel = util.grid.at(2, 2, 2)

        scene.world.showSection(util.select.position(5, 0, 2), Direction.WEST)
        scene.idle(5)
        scene.world.showSection(util.select.fromTo(4, 1, 2, 4, 2, 2), Direction.DOWN)
        scene.world.setKineticSpeed(util.select.position(coaxialGear1), 8f)
        scene.idle(5)
        scene.world.setKineticSpeed(util.select.position(cogwheel), -8f)
        scene.world.showSection(util.select.position(cogwheel), Direction.DOWN)
        scene.world.setKineticSpeed(util.select.position(coaxialGear2), 8f)
        scene.idle(25)

        scene.overlay.showText(80)
            .text("This text is defined in a language file.")
            .attachKeyFrame()
            .pointAt(util.vector.blockSurface(coaxialGear2, Direction.EAST))
        scene.idle(20)

        scene.effects.rotationDirectionIndicator(coaxialGear1)
        scene.effects.rotationDirectionIndicator(coaxialGear2)
        scene.effects.rotationDirectionIndicator(longShaft1)
        scene.effects.rotationDirectionIndicator(longShaft2)

        scene.idle(80)

        scene.markAsFinished()
    }

    fun differential(scene: SceneBuilder, util: SceneBuildingUtil) {
        scene.title("differential", "This text is defined in a language file.")
        scene.configureBasePlate(1, 0, 5)
        scene.showBasePlate()

        val westBigGear = util.grid.at(0, 0, 3)
        val eastBigGear = util.grid.at(6, 0, 1)
        val westBottomSmallGear = util.grid.at(0, 1, 2)
        val westTopSmallGear = util.grid.at(0, 2, 2)
        val eastBottomSmallGear = util.grid.at(6, 1, 2)
        val eastTopSmallGear = util.grid.at(6, 2, 2)
        val westOuterShaft = util.grid.at(1, 2, 2)
        val westInnerShaft = util.grid.at(2, 2, 2)
        val eastOuterShaft = util.grid.at(5, 2, 2)
        val eastInnerShaft = util.grid.at(4, 2, 2)
        val differential = util.grid.at(3, 2, 2)
        val westSpeedometer = util.grid.at(1, 1, 2)
        val eastSpeedometer = util.grid.at(5, 1, 2)
        val middleSmallGear = util.grid.at(3, 3, 3)
        val middleSpeedometer = util.grid.at(2, 3, 3)

        val west = util.select.position(westBottomSmallGear)
            .add(util.select.position(westTopSmallGear))
            .add(util.select.position(westOuterShaft))
            .add(util.select.position(westInnerShaft))
            .add(util.select.position(westSpeedometer))

        val east = util.select.position(eastBigGear)
            .add(util.select.position(eastBottomSmallGear))
            .add(util.select.position(eastTopSmallGear))
            .add(util.select.position(eastOuterShaft))
            .add(util.select.position(eastInnerShaft))
            .add(util.select.position(eastSpeedometer))

        val center = util.select.position(differential)
            .add(util.select.position(middleSmallGear))
            .add(util.select.position(middleSpeedometer))

        val back = util.select.fromTo(3, 0, 5, 4, 2, 5)

        scene.idle(10)
        val bigGearElement = scene.world.showIndependentSection(util.select.position(westBigGear), Direction.EAST)
        scene.world.showSection(util.select.position(eastBigGear), Direction.WEST)
        scene.idle(5)
        scene.world.showSection(util.select.position(westBottomSmallGear), Direction.EAST)
        scene.world.showSection(util.select.position(eastBottomSmallGear), Direction.WEST)
        scene.idle(5)
        scene.world.showSection(util.select.position(westTopSmallGear), Direction.EAST)
        scene.world.showSection(util.select.position(eastTopSmallGear), Direction.WEST)
        scene.idle(5)
        scene.world.showSection(util.select.position(westOuterShaft), Direction.DOWN)
        scene.world.showSection(util.select.position(eastOuterShaft), Direction.DOWN)
        scene.idle(5)
        scene.world.showSection(util.select.position(westInnerShaft), Direction.DOWN)
        scene.world.showSection(util.select.position(eastInnerShaft), Direction.DOWN)
        scene.idle(5)
        val differentialElement = scene.world.showIndependentSection(util.select.position(differential), Direction.DOWN)
        scene.idle(10)

        scene.overlay.showText(80)
            .text("This text is defined in a language file.")
            .pointAt(util.vector.topOf(differential))
            .attachKeyFrame()
        scene.idle(20)
        scene.effects.rotationSpeedIndicator(westOuterShaft)
        scene.effects.rotationSpeedIndicator(eastOuterShaft)
        scene.idle(20)
        scene.effects.rotationSpeedIndicator(differential)
        scene.idle(60)

        scene.world.showSection(util.select.position(eastSpeedometer), Direction.EAST)
        scene.idle(5)
        scene.world.showSection(util.select.position(westSpeedometer), Direction.WEST)
        scene.idle(5)
        scene.world.showSection(util.select.position(middleSmallGear), Direction.DOWN)
        scene.idle(5)
        scene.world.showSection(util.select.position(middleSpeedometer), Direction.DOWN)
        scene.idle(10)

        scene.overlay.showText(120)
            .text("This text is defined in a language file.")
            .attachKeyFrame()
        scene.idle(20)
        scene.overlay.showOutline(PonderPalette.BLUE, "east", util.select.position(eastSpeedometer), 100)
        scene.overlay.showText(100)
            .text("This text is defined in a language file.")
            .colored(PonderPalette.BLUE)
            .independent(40)
        scene.idle(20)
        scene.overlay.showOutline(PonderPalette.RED, "west", util.select.position(westSpeedometer), 80)
        scene.overlay.showText(80)
            .text("This text is defined in a language file.")
            .colored(PonderPalette.RED)
            .independent(60)
        scene.idle(20)
        scene.overlay.showOutline(PonderPalette.FAST, "total", util.select.position(middleSpeedometer), 60)
        scene.overlay.showText(60)
            .text("This text is defined in a language file.")
            .colored(PonderPalette.FAST)
            .independent(80)
        scene.idle(80)

        scene.overlay.showText(170)
            .text("This text is defined in a language file.")
            .attachKeyFrame()
        scene.idle(10)
        scene.world.multiplyKineticSpeed(center, 8 / 14f)
        scene.world.multiplyKineticSpeed(west, 1 / 1000f)
        scene.world.moveSection(bigGearElement, util.vector.of(-1.0, 0.0, 0.0), 10)
        scene.idle(15)
        scene.world.rotateSection(bigGearElement, 0.0, 0.0, 180.0, 10)
        scene.idle(15)
        scene.world.moveSection(bigGearElement, util.vector.of(1.0, 0.0, 0.0), 10)
        scene.idle(10)
        scene.world.rotateSection(bigGearElement, 0.0, 0.0, 180.0, 0)
        scene.world.setKineticSpeed(util.select.position(westBigGear), -3f)
        scene.world.multiplyKineticSpeed(center, 2 / 8f)
        scene.world.multiplyKineticSpeed(west, -1000f)
        scene.idle(20)
        scene.overlay.showOutline(PonderPalette.BLUE, "east", util.select.position(eastSpeedometer), 100)
        scene.overlay.showText(100)
            .text("This text is defined in a language file.")
            .colored(PonderPalette.BLUE)
            .independent(40)
        scene.idle(20)
        scene.overlay.showOutline(PonderPalette.RED, "west", util.select.position(westSpeedometer), 80)
        scene.overlay.showText(80)
            .text("This text is defined in a language file.")
            .colored(PonderPalette.RED)
            .independent(60)
        scene.idle(20)
        scene.overlay.showOutline(PonderPalette.FAST, "total", util.select.position(middleSpeedometer), 60)
        scene.overlay.showText(60)
            .text("This text is defined in a language file.")
            .colored(PonderPalette.FAST)
            .independent(80)
        scene.idle(70)
        scene.world.hideSection(
            util.select.position(middleSmallGear).add(util.select.position(middleSpeedometer)),
            Direction.SOUTH
        )
        scene.idle(10)

        scene.overlay.showText(80)
            .text("This text is defined in a language file.")
            .colored(PonderPalette.RED)
            .attachKeyFrame()
        scene.idle(100)

        scene.overlay.showText(60)
            .text("This text is defined in a language file.")
            .colored(PonderPalette.GREEN)
            .pointAt(util.vector.blockSurface(differential, Direction.WEST))
        scene.idle(80)
        scene.world.setKineticSpeed(util.select.position(differential), 0f)
        scene.world.hideSection(east, Direction.EAST)
        scene.world.hideSection(west, Direction.WEST)
        scene.world.hideIndependentSection(bigGearElement, Direction.WEST)
        scene.idle(10)
        scene.world.setKineticSpeed(east, 0f)
        scene.world.setKineticSpeed(west, 0f)
        scene.idle(10)
        scene.world.rotateSection(differentialElement, 0.0, 90.0, 0.0, 10)
        scene.idle(10)
        scene.overlay.showText(60)
            .text("This text is defined in a language file.")
            .colored(PonderPalette.RED)
            .pointAt(util.vector.blockSurface(differential, Direction.NORTH))
        scene.idle(80)

        scene.overlay.showText(80)
            .text("This text is defined in a language file.")
            .pointAt(util.vector.blockSurface(differential, Direction.UP))
            .attachKeyFrame()
        scene.idle(20)
        scene.world.showSection(back, Direction.NORTH)
        scene.idle(5)
        for (z in 5 downTo 2) {
            scene.world.showSection(util.select.position(util.grid.at(4, 3, z)), Direction.DOWN)
            scene.idle(5)
        }

        scene.world.destroyBlock(differential)
        scene.idle(60)

        scene.markAsFinished()
    }

    fun doubleCardanShaft(scene: SceneBuilder, util: SceneBuildingUtil) {
        scene.title("double_cardan_shaft", "This text is defined in a language file.")
        scene.configureBasePlate(0, 0, 5)
        scene.showBasePlate()

        val dcs = util.grid.at(2, 1, 2)

        scene.world.showSection(util.select.position(1, 0, 5), Direction.NORTH)
        val shafts = arrayOf(
            intArrayOf(2, 5),
            intArrayOf(2, 4),
            intArrayOf(2, 3),
            intArrayOf(2, 2),
            intArrayOf(3, 2),
            intArrayOf(4, 2)
        )
        for (shaft in shafts) {
            scene.idle(5)
            scene.world.showSection(util.select.position(shaft[0], 1, shaft[1]), Direction.DOWN)
        }



        scene.idle(10)
        scene.overlay.showText(100)
            .text("This text is defined in a language file.")
            .pointAt(util.vector.centerOf(dcs))
            .attachKeyFrame()
        scene.idle(120)

        val secondShaft = util.select.fromTo(0, 1, 2, 1, 1, 2)
        scene.world.showSection(secondShaft, Direction.DOWN)
        scene.idle(20)

        scene.overlay.showText(100)
            .text("This text is defined in a language file.")
            .attachKeyFrame()
        scene.idle(20)
        scene.overlay.showControls(
            InputWindowElement(util.vector.blockSurface(dcs, Direction.NORTH), Pointing.RIGHT)
                .withItem(AllItems.WRENCH.asStack()),
            20
        )
        scene.idle(5)
        scene.world.setBlock(
            dcs,
            DoubleCardanShaftBlock.getBlockstateConnectingDirections(Direction.SOUTH, Direction.UP),
            false
        )
        scene.world.setKineticSpeed(util.select.fromTo(3, 1, 2, 4, 1, 2), 0f)
        scene.idle(25)
        scene.overlay.showControls(
            InputWindowElement(util.vector.blockSurface(dcs, Direction.NORTH), Pointing.RIGHT)
                .withItem(AllItems.WRENCH.asStack()),
            20
        )
        scene.idle(5)
        scene.world.setBlock(
            dcs,
            DoubleCardanShaftBlock.getBlockstateConnectingDirections(Direction.SOUTH, Direction.WEST),
            false
        )
        scene.world.setKineticSpeed(secondShaft, 16f)
        scene.idle(15)

        scene.markAsFinished()
    }

    fun planetaryGearset(scene: SceneBuilder, util: SceneBuildingUtil) {
        scene.title("planetary_gearset", "This text is defined in a language file.")
        scene.configureBasePlate(0, 0, 3)
        scene.showBasePlate()

        scene.world.showSection(util.select.position(1, 0, 3), Direction.NORTH)
        scene.idle(5)
        scene.world.showSection(util.select.position(1, 1, 2), Direction.DOWN)
        scene.idle(5)
        scene.world.showSection(util.select.fromTo(1, 2, 0, 1, 2, 1), Direction.DOWN)
        scene.idle(5)
        scene.world.showSection(util.select.position(2, 3, 1), Direction.DOWN)
        scene.idle(5)

        scene.overlay.showText(100)
            .text("This text is defined in a language file.")
            .attachKeyFrame()
            .pointAt(util.vector.blockSurface(util.grid.at(1, 2, 1), Direction.WEST))
        scene.idle(120)

        scene.overlay.showText(100)
            .text("This text is defined in a language file.")
            .attachKeyFrame()
            .pointAt(util.vector.blockSurface(util.grid.at(1, 2, 0), Direction.NORTH))
        scene.idle(20)

        scene.effects.rotationDirectionIndicator(util.grid.at(1, 2, 1))
        scene.effects.rotationDirectionIndicator(util.grid.at(1, 2, 0))
        scene.idle(100)

        scene.markAsFinished()
    }
}
