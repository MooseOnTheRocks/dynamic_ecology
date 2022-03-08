package dev.foltz.de.block;

import dev.foltz.de.DEMod;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

import java.util.Random;


//Any live cell with fewer than two live neighbours dies, as if by underpopulation.
//Any live cell with two or three live neighbours lives on to the next generation.
//Any live cell with more than three live neighbours dies, as if by overpopulation.
//Any dead cell with exactly three live neighbours becomes a live cell, as if by reproduction.

public class CAPlant extends PlantBlock {
    public static final IntProperty GROWTH_STAGE = IntProperty.of("growth_stage", 0, 3);

    public CAPlant() {
        super(FabricBlockSettings.of(Material.PLANT).noCollision().solidBlock((state, world, pos) -> false).nonOpaque());
        setDefaultState(this.getStateManager().getDefaultState().with(GROWTH_STAGE, 0));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(GROWTH_STAGE);
    }

    @Override
    protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
        return floor.isIn(BlockTags.DIRT);
    }

    @Override
    public boolean isTranslucent(BlockState state, BlockView world, BlockPos pos) {
        return state.getFluidState().isEmpty();
    }

    @Override
    public boolean hasRandomTicks(BlockState state) {
        return true;
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        int range = 2; // 5x5
        double x = pos.getX() + range - 2 * random.nextDouble() * range;
        double y = pos.getY() + range - 2 * random.nextDouble() * range;
        double z = pos.getZ() + range - 2 * random.nextDouble() * range;
        BlockPos chosenPos = new BlockPos(x, y, z);
        BlockState chosenBlockState = world.getBlockState(chosenPos);
        Block chosenBlock = chosenBlockState.getBlock();
        if (chosenBlock != Blocks.AIR && chosenBlock != DEMod.CAPlant) {
            return;
        }
        if (!canPlantOnTop(world.getBlockState(chosenPos.down()), world, chosenPos)) {
            return;
        }
        DEMod.LOG.info("Ticking CA Grid: " + chosenPos + ", " + chosenBlockState);
        CAGridView gv = new CAGridView(world, chosenPos, range);
        long neighborCount = gv.streamBlockStates()
            .filter(bs -> bs.getBlock() == DEMod.CAPlant)
            .count();
        long fullyGrownNeighbors = gv.streamBlockStates()
                .filter(bs -> bs.getBlock() == DEMod.CAPlant)
                .filter(bs -> bs.get(GROWTH_STAGE) == 3)
                .count();

        // Dead cell
        if (chosenBlock == Blocks.AIR) {
            if (fullyGrownNeighbors >= 1 && neighborCount >= 2 && neighborCount <= 4) {
                world.setBlockState(chosenPos, DEMod.CAPlant.getDefaultState());
            }
        }
        // Live cell
        else if (chosenBlock == DEMod.CAPlant) {
            if (neighborCount < 2 || neighborCount > 6) {
                world.breakBlock(chosenPos, true);
            }
            else {
                int stage = chosenBlockState.get(GROWTH_STAGE);
                if (stage < 3) {
                    world.setBlockState(chosenPos, chosenBlockState.with(GROWTH_STAGE, stage + 1));
                }
            }
        }

//        System.out.println("Neighbors: " + neighborCount);
    }
}
