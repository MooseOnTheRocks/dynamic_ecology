package dev.foltz.de.block;

import dev.foltz.de.plant.Plants;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

import java.util.Random;

public class EyePlant extends AbstractSingleBlockPlant {
    public final VoxelShape BOUNDING_BOX = Block.createCuboidShape(0, 0, 0, 16, 1, 16);
    public static final BooleanProperty EYES_OPEN = BooleanProperty.of("eyes_open");

    public EyePlant() {
        super();
        setDefaultState(getStateManager().getDefaultState().with(EYES_OPEN, false));
    }

    @Override
    public void plantTick(World world, BlockState blockState, BlockPos pos, Random random) {
        boolean isSkyVisible = world.isSkyVisible(pos);
        int lightLevel = world.getLightLevel(pos);
        int sunlightLevel = world.getLightLevel(LightType.SKY, pos);

        // Death mechanics
        boolean eyesOpen = blockState.get(EYES_OPEN);
        if (lightLevel > 7 && (isSkyVisible || sunlightLevel >= 13)) {
            if (!blockState.get(EYES_OPEN)) {
                world.breakBlock(pos, true);
                return;
            }
        }

        // Eye mechanics
        if (lightLevel <= 7) {
            if (!eyesOpen) {
                world.setBlockState(pos, blockState.with(EYES_OPEN, true));
                return;
            }
        }
        else {
            if (eyesOpen) {
                world.setBlockState(pos, blockState.with(EYES_OPEN, false));
                return;
            }
        }

        // Spawn mechanics
        if (eyesOpen) {
            Direction dir = Direction.values()[random.nextInt(Direction.values().length)];
            BlockPos chosenPos = pos.offset(dir);
            BlockState chosenBlock = world.getBlockState(chosenPos);
            if (chosenBlock.getBlock() != Blocks.AIR && chosenBlock.getMaterial() != Material.REPLACEABLE_PLANT) {
                return;
            }
            if (!canPlantOnTop(world.getBlockState(chosenPos.down()), world, chosenPos)) {
                return;
            }
            world.setBlockState(chosenPos, Plants.EYE_PLANT_BLOCK.getDefaultState());
        }
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (entity.getPos().distanceTo(new Vec3d(pos.getX() + 0.5, entity.getPos().y, pos.getZ() + 0.5)) <= 0.5d) {
            if (state.get(EYES_OPEN)) {
                world.setBlockState(pos, state.with(EYES_OPEN, false));
            }
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(EYES_OPEN);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return BOUNDING_BOX;
    }
}
