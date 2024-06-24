package dev.sterner.coggle.common.block

import com.simibubi.create.AllBlocks
import com.simibubi.create.AllShapes
import com.simibubi.create.content.kinetics.base.KineticBlockEntity
import com.simibubi.create.content.kinetics.simpleRelays.CogWheelBlock
import com.simibubi.create.content.kinetics.simpleRelays.ShaftBlock
import dev.sterner.coggle.registry.CoggleBlockEntityTypes
import dev.sterner.coggle.registry.CoggleBlocks
import dev.sterner.coggle.util.CoggleShapes
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.Direction.AxisDirection
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape


class CoaxialGearBlock(properties: Properties?) : CogWheelBlock(false, properties) {

    init {
        registerDefaultState(defaultBlockState().setValue(HAS_SHAFT, false))
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block?, BlockState?>) {
        builder.add(HAS_SHAFT)
        super.createBlockStateDefinition(builder)
    }

    override fun getShape(
        state: BlockState,
        worldIn: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape {
        return (if (state.getValue<Boolean>(HAS_SHAFT)) AllShapes.SMALL_GEAR else CoggleShapes.COAXIAL_GEAR)[state.getValue<Direction.Axis>(
            AXIS
        )]
    }

    override fun onPlace(state: BlockState, worldIn: Level, pos: BlockPos, oldState: BlockState, isMoving: Boolean) {
        if (state.getValue(HAS_SHAFT)) updatePropagationOfLongShaft(oldState, worldIn, pos)
        super.onPlace(state, worldIn, pos, oldState, isMoving)
    }

    override fun onWrenched(state: BlockState, context: UseOnContext): InteractionResult {
        if (state.getValue<Boolean>(HAS_SHAFT)) {
            if (tryRemoveBracket(context)) {
                return InteractionResult.SUCCESS
            } else if (tryRemoveLongShaft(state, context.level, context.clickedPos, false)) {
                val player: Player? = context.player
                if (player != null && player.isCreative()) player.getInventory()
                    .placeItemBackInInventory(CoggleBlocks.COAXIAL_GEAR.asStack())
                return InteractionResult.SUCCESS
            }
        }

        return super.onWrenched(state, context)
    }

    override fun updateShape(
        state: BlockState,
        facing: Direction,
        facingState: BlockState?,
        level: LevelAccessor,
        currentPos: BlockPos,
        facingPos: BlockPos?
    ): BlockState {
        if (facing.getAxis() === state.getValue(AXIS)) updatePropagationOfLongShaft(state, level, currentPos)
        return super.updateShape(state, facing, facingState, level, currentPos, facingPos)
    }

    override fun onRemove(state: BlockState, world: Level, pos: BlockPos, newState: BlockState, isMoving: Boolean) {
        if (state !== newState && newState.isAir && !isMoving) {
            if (tryRemoveLongShaft(state, world, pos, true)) {
                Block.popResource(world, pos, AllBlocks.SHAFT.asStack())
            }
        }

        super.onRemove(state, world, pos, newState, isMoving)
    }

    /**
     * @param removing True if the Coaxial Gear is being mined, false if it is just being wrenched
     */
    protected fun tryRemoveLongShaft(state: BlockState, level: Level, pos: BlockPos, removing: Boolean): Boolean {
        val thisAxis = state.getValue(AXIS)
        for (axisDirection in Direction.AxisDirection.values()) {
            val longShaftPos = pos.relative(Direction.get(axisDirection, thisAxis))
            val longShaftState: BlockState = level.getBlockState(longShaftPos)
            if (CoggleBlocks.LONG_SHAFT.has(longShaftState)) {
                if (longShaftState.getValue<Direction.Axis>(AXIS) === thisAxis && (longShaftState.getValue(
                        DirectionalRotatedPillarKineticBlock.POSITIVE_AXIS_DIRECTION
                    ) != (axisDirection === Direction.AxisDirection.POSITIVE))
                ) {
                    if (!level.isClientSide()) {
                        if (!removing) level.setBlockAndUpdate(
                            pos,
                            AllBlocks.SHAFT.defaultState.setValue(AXIS, thisAxis)
                        )
                        level.setBlockAndUpdate(longShaftPos, AllBlocks.SHAFT.defaultState.setValue(AXIS, thisAxis))
                    }

                    return true
                }
            }
        }

        return false
    }

    override fun use(
        state: BlockState,
        world: Level,
        pos: BlockPos,
        player: Player,
        hand: InteractionHand?,
        ray: BlockHitResult?
    ): InteractionResult {
        if (player.isShiftKeyDown() || !player.mayBuild()) return InteractionResult.PASS
        val stack: ItemStack = player.getItemInHand(hand)
        if (AllBlocks.SHAFT.isIn(stack) && (!state.getValue<Boolean>(HAS_SHAFT))) {
            if (tryMakeLongShaft(state, world, pos, Direction.getFacingAxis(player, state.getValue(AXIS)))) {
                if (!player.isCreative() && !world.isClientSide()) stack.shrink(1)
                return InteractionResult.sidedSuccess(world.isClientSide())
            } else {
                /*
                player.displayClientMessage(
                    DestroyLang.translate("tooltip.coaxial_gear.shaft_too_short").style(ChatFormatting.RED).component(),
                    true
                )

                 */
                return InteractionResult.SUCCESS
            }
        }

        return InteractionResult.PASS
    }

    override fun hasShaftTowards(world: LevelReader?, pos: BlockPos?, state: BlockState?, face: Direction?): Boolean {
        return false
    }

    override fun getBlockEntityType(): BlockEntityType<out KineticBlockEntity?> {
        return CoggleBlockEntityTypes.COAXIAL_GEAR.get()
    }

    companion object {
        val HAS_SHAFT: BooleanProperty = BooleanProperty.create("has_shaft")

        fun isCoaxialGear(state: BlockState): Boolean {
            return state.block is CoaxialGearBlock
        }

        fun isCoaxialGear(block: Block?): Boolean {
            return block is CoaxialGearBlock
        }

        fun updatePropagationOfLongShaft(state: BlockState, level: LevelReader, pos: BlockPos) {
            if (isCoaxialGear(state) && state.getValue<Boolean>(HAS_SHAFT) && !level.isClientSide) {
                val axis = state.getValue(AXIS)
                for (axisDirection in Direction.AxisDirection.entries) {
                    val longShaftPos = pos.relative(Direction.get(axisDirection, axis))
                    val longShaftState = level.getBlockState(pos.relative(Direction.get(axisDirection, axis)))
                    if ((CoggleBlocks.LONG_SHAFT.has(longShaftState) && longShaftState.getValue<Direction.Axis>(
                            AXIS
                        ) === axis) && longShaftState.getValue(DirectionalRotatedPillarKineticBlock.POSITIVE_AXIS_DIRECTION) == (axisDirection !== Direction.AxisDirection.POSITIVE)
                    ) {
                        level.getBlockEntity(longShaftPos, CoggleBlockEntityTypes.LONG_SHAFT.get())
                            .ifPresent { be ->
                                be.updateSpeed = true
                            }
                        return
                    }
                }
            }
        }

        fun tryMakeLongShaft(state: BlockState, level: Level, pos: BlockPos, preferredDirection: Direction): Boolean {
            val axis = state.getValue(AXIS)
            if (preferredDirection.getAxis() !== axis) return false
            for (direction in arrayOf<Direction>(preferredDirection, preferredDirection.getOpposite())) {
                val shaftPos = pos.relative(direction)
                val shaftState: BlockState = level.getBlockState(shaftPos)
                if (!ShaftBlock.isShaft(shaftState)) continue
                if (shaftState.getValue(AXIS) !== axis) continue
                // Creation was successful
                if (!level.isClientSide()) {
                    level.setBlockAndUpdate(
                        shaftPos,
                        CoggleBlocks.LONG_SHAFT.getDefaultState().setValue(AXIS, axis).setValue(
                            DirectionalRotatedPillarKineticBlock.POSITIVE_AXIS_DIRECTION,
                            direction.getAxisDirection() !== AxisDirection.POSITIVE
                        )
                    )
                    level.setBlockAndUpdate(
                        pos, CoggleBlocks.COAXIAL_GEAR.getDefaultState().setValue(AXIS, axis).setValue(
                            HAS_SHAFT, true
                        )
                    )
                }

                return true
            }

            return false
        }
    }
};
