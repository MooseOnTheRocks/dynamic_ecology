package dev.foltz.de.block;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.stream.Stream;

public class CAGridView {
    public World world;
    public BlockPos center;

    public CAGridView(World world, BlockPos center) {
        this.world = world;
        this.center = center;
    }

    public Stream<BlockState> streamBlockStates() {
        return streamBlockPos().map(world::getBlockState);
    }

    public Stream<BlockPos> streamBlockPos() {
        int x = center.getX();
        int y = center.getY();
        int z = center.getZ();
        BlockPos[] blocks = new BlockPos[] {
                new BlockPos(x - 1, y, z - 1),
                new BlockPos(x, y, z - 1),
                new BlockPos(x + 1, y, z - 1),
                new BlockPos(x - 1, y, z),
//                new BlockPos(x, y, z),
                new BlockPos(x + 1, y, z),
                new BlockPos(x - 1, y, z + 1),
                new BlockPos(x, y, z + 1),
                new BlockPos(x + 1, y, z + 1)
        };
        return Arrays.stream(blocks);
    }
}
