package dev.foltz.de.block;

import dev.foltz.de.DEMod;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.Random;
import java.util.stream.Stream;

public class ConwayPlant extends AbstractSingleBlockPlant {
    public static final IntProperty GROWTH_STAGE = IntProperty.of("growth_stage", 0, 3);
    public static final int RANGE = 2;
    public final VoxelShape[] boundingBoxes;

    public ConwayPlant() {
        super();
        this.boundingBoxes = BlockUtils.lerpBoundingBoxes(4, Direction.DOWN, 3, 10, 6, 14);
        setDefaultState(this.getStateManager().getDefaultState().with(GROWTH_STAGE, 0));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(GROWTH_STAGE);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return boundingBoxes[state.get(GROWTH_STAGE)];
    }

    @Override
    public void plantTick(World world, BlockState blockState, BlockPos pos, Random random) {
        Stream<BlockState> blocksInRange = BlockUtils.streamBlocksInRange(pos, RANGE).map(world::getBlockState);
        int neighbors = (int) blocksInRange.filter(bs -> bs.getBlock() == DEMod.CONWAY_PLANT).count();

        // Death mechanics
        if (neighbors < 2 || neighbors > 6) {
            world.breakBlock(pos, true);
            return;
        }

        // Growth mechanics
        int stage = blockState.get(GROWTH_STAGE);
        if (stage < 3) {
            world.setBlockState(pos, blockState.with(GROWTH_STAGE, stage + 1));
            return;
        }

        // Spawn mechanics
        if (stage == 3) {
            double x = Math.round(pos.getX() + RANGE - 2 * random.nextDouble() * RANGE);
            double y = Math.round(pos.getY() + RANGE - 2 * random.nextDouble() * RANGE);
            double z = Math.round(pos.getZ() + RANGE - 2 * random.nextDouble() * RANGE);
            BlockPos chosenPos = new BlockPos(x, y, z);
            BlockState chosenBlock = world.getBlockState(chosenPos);
            if (chosenBlock.getBlock() != Blocks.AIR) {
                return;
            }
            if (!canPlantOnTop(world.getBlockState(chosenPos.down()), world, chosenPos)) {
                return;
            }

            // Simulate a dead cell for this plant.
            Stream<BlockState> chosenBlocksInRange = BlockUtils.streamBlocksInRange(chosenPos, RANGE).map(world::getBlockState);
            int chosenNeighbors = (int) chosenBlocksInRange.filter(bs -> bs.getBlock() == DEMod.CONWAY_PLANT).count();
            chosenBlocksInRange = BlockUtils.streamBlocksInRange(chosenPos, RANGE).map(world::getBlockState);
            int chosenFullyGrownNeighbors = (int) chosenBlocksInRange.filter(bs -> bs.getBlock() == DEMod.CONWAY_PLANT && bs.get(GROWTH_STAGE) == 3).count();
            if (chosenFullyGrownNeighbors >= 1 && chosenNeighbors >= 2 && chosenNeighbors <= 4) {
                world.setBlockState(chosenPos, DEMod.CONWAY_PLANT.getDefaultState());
            }

            return;
        }
    }
}
