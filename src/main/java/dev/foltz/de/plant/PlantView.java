package dev.foltz.de.plant;

import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.stream.Stream;

public class PlantView {
    public final World world;
    public final BlockPos center;
    public final int rangeXZ;
    public final int rangeY;
    public final boolean distEuclid;

    public PlantView(World world, BlockPos center, int rangeXZ, int rangeY, boolean distEuclid) {
        this.world = world;
        this.center = center;
        this.rangeXZ = rangeXZ;
        this.rangeY = rangeY;
        this.distEuclid = distEuclid;
    }

    public double volume() {
        double xz = (2 * (rangeXZ + 0.5));
        double y = (2 * (rangeY + 0.5));
        return xz * y * xz;
    }

    public Stream<BlockPos> streamBlockPos() {
        int x = center.getX();
        int y = center.getY();
        int z = center.getZ();
        BlockPos p1 = new BlockPos(x - rangeXZ, y - rangeY, z - rangeXZ);
        BlockPos p2 = new BlockPos(x + rangeXZ, y + rangeY, z + rangeXZ);
        if (distEuclid) {
            double maxDist = (rangeXZ * rangeXZ + rangeY * rangeY + rangeXZ * rangeXZ);
            return BlockPos.stream(p1, p2)
                    .filter(pos -> center.getSquaredDistance(pos) <= maxDist * maxDist);
        }
        else {
            return BlockPos.stream(p1, p2);
        }
    }

    public long count(Block block) {
        return streamBlockPos()
                .filter(bp -> world.getBlockState(bp).getBlock() == block)
                .count();
    }

    public long count(Fluid fluid) {
        return streamBlockPos()
                .filter(bp -> world.getFluidState(bp).getFluid() == fluid)
                .count();
    }

    public double density(Block block) {
        return count(block) / volume();
    }

    public double density(Fluid fluid) {
        return count(fluid) / volume();
    }

    public double distance(BlockPos target) {
        return Math.sqrt(center.getSquaredDistance(target));
    }
}
