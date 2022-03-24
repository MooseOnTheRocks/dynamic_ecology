package dev.foltz.de.plant;

import dev.foltz.de.util.PlantUtil;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluids;
import net.minecraft.state.property.Property;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Optional;

public class PlantState {
    public final Plant plant;
    public final World world;
    public final BlockPos pos;

    public PlantState(Plant plant, World world, BlockPos pos) {
        this.plant = plant;
        this.world = world;
        this.pos = pos;
    }

    public void tick() {
        if (plant.behavior == null) {
            return;
        }

        plant.behavior.performAction(new PlantState(plant, world, pos));
        Optional<BlockState> nextState = plant.behavior.nextState(new PlantState(plant, world, pos));
        if (nextState.isPresent()) {
            world.setBlockState(pos, nextState.get());
        }
        else {
            world.breakBlock(pos, true);
        }
    }

    public boolean spawnNearby(int rangeXZ, int rangeY) {
        double x = PlantUtil.randomRange(pos.getX(), rangeXZ);
        double y = PlantUtil.randomRange(pos.getY(), rangeY);
        double z = PlantUtil.randomRange(pos.getZ(), rangeXZ);
        BlockPos chosenPos = new BlockPos(x, y, z);
        BlockState chosenBlockState = world.getBlockState(chosenPos);
        if ((chosenBlockState.isIn(BlockTags.REPLACEABLE_PLANTS) || chosenBlockState.isAir()) && canPlace(chosenPos)) {
            world.setBlockState(chosenPos, getBlockState().getBlock().getDefaultState());
            return true;
        }
        return false;
    }

    public int neighbors(int rangeXZ, int rangeY) {
        return (int) new PlantView(world, pos, rangeXZ, rangeY, false).count(plant.block);
    }

    public boolean canPlace(BlockPos pos) {
        return getBlockState().canPlaceAt(world, pos);
//        return plant.canPlace(world, pos);
    }

    public boolean nearWater(int rangeXZ, int rangeY, boolean distEuclid) {
        return new PlantView(world, pos, rangeXZ, rangeY, distEuclid).count(Fluids.WATER) > 0
                || new PlantView(world, pos, rangeXZ, rangeY, distEuclid).count(Fluids.FLOWING_WATER) > 0;
    }

    public boolean isInTheRain() {
        return world.isRaining() && world.isSkyVisible(pos);
    }

    public <T extends Comparable<T>, P extends Property<T>> T get(P property) {
        return getBlockState().get(property);
    }

    public BlockState getBlockState() {
        return world.getBlockState(pos);
    }
}
