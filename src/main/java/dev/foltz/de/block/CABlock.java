package dev.foltz.de.block;

import dev.foltz.de.DEMod;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

import java.util.Random;


//Any live cell with fewer than two live neighbours dies, as if by underpopulation.
//Any live cell with two or three live neighbours lives on to the next generation.
//Any live cell with more than three live neighbours dies, as if by overpopulation.
//Any dead cell with exactly three live neighbours becomes a live cell, as if by reproduction.

public class CABlock extends PlantBlock {
    public CABlock() {
        super(FabricBlockSettings.of(Material.PLANT).noCollision().solidBlock((state, world, pos) -> false).nonOpaque());
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
        double range = 1f;
        double x = pos.getX() + range / 2 - random.nextDouble() * range;
        double y = pos.getY() + range / 2 - random.nextDouble() * range;
        double z = pos.getZ() + range / 2 - random.nextDouble() * range;
        BlockPos chosenPos = new BlockPos(x, y, z);
        BlockState chosenBlockState = world.getBlockState(chosenPos);
        Block chosenBlock = chosenBlockState.getBlock();
        if (chosenBlock != Blocks.AIR && chosenBlock != DEMod.CABlock) {
            return;
        }
        DEMod.LOG.info("Ticking CA Grid: " + chosenPos + ", " + chosenBlockState);
        CAGridView gv = new CAGridView(world, chosenPos);
        long neighborCount = gv.streamBlockStates()
            .filter(bs -> bs.getBlock() == DEMod.CABlock)
            .count();

        // Dead cell
        if (chosenBlock == Blocks.AIR) {
            if (neighborCount == 3) {
                world.setBlockState(chosenPos, DEMod.CABlock.getDefaultState());
            }
        }
        // Live cell
        else if (chosenBlock == DEMod.CABlock) {
            if (neighborCount < 2 || neighborCount > 3) {
                world.breakBlock(chosenPos, true);
            }
        }

//        System.out.println("Neighbors: " + neighborCount);
    }
}
