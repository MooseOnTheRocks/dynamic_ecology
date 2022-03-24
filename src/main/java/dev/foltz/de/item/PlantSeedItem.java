package dev.foltz.de.item;

import dev.foltz.de.plant.plants.CubeStalkPlant;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class PlantSeedItem extends BlockItem {
    public PlantSeedItem(Block block, Settings settings) {
        super(block, settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        BlockState blockState = context.getWorld().getBlockState(context.getBlockPos());
        if (blockState.getBlock() == CubeStalkPlant.CUBE_STALK_PLANT_BLOCK && context.getSide() == Direction.UP && !blockState.get(CubeStalkPlant.TWO_TALL)) {
            System.out.println("Turning two_tall into one_tall");
            world.setBlockState(pos, blockState.with(CubeStalkPlant.TWO_TALL, true));
            world.updateNeighbors(pos, blockState.getBlock());
            if (context.getPlayer() == null || !context.getPlayer().getAbilities().creativeMode) {
                context.getStack().decrement(1);
            }

            return ActionResult.success(context.getWorld().isClient);
        }
        else {
            return super.useOnBlock(context);
        }
    }
}
