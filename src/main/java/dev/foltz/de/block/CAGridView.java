package dev.foltz.de.block;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class CAGridView {
    public World world;
    public BlockPos center;
    public int range;

    public CAGridView(World world, BlockPos center, int range) {
        this.world = world;
        this.center = center;
        this.range = range;
    }

    public Stream<BlockState> streamBlockStates() {
        return streamBlockPos().map(world::getBlockState);
    }

    public Stream<BlockPos> streamBlockPos() {
        int x = center.getX();
        int y = center.getY();
        int z = center.getZ();
        BlockPos p1 = new BlockPos(x - range, y - range, z - range);
        BlockPos p2 = new BlockPos(x + range, y + range, z + range);
        return BlockPos.stream(p1, p2);
    }
}
