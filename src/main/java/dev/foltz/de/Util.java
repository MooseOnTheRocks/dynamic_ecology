package dev.foltz.de;

import net.minecraft.block.Block;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;

public class Util {
    public static VoxelShape[] lerpBoundingBoxes(int numStages, Direction direction, double minWidth, double maxWidth, double minHeight, double maxHeight) {
        VoxelShape[] boundingBoxPerStage = new VoxelShape[numStages];
        for (int i = 0; i < numStages; i++) {
            double p = (double) i / (double) (numStages - 1);
            double width = MathHelper.lerp(p, minWidth, maxWidth);
            double height = MathHelper.lerp(p, minHeight, maxHeight);
            double dw = (16 - width) / 2;

            boundingBoxPerStage[i] = switch(direction) {
                case UP     -> Block.createCuboidShape(dw, 16 - height, dw, 16 - dw, 16, 16 - dw);
                case DOWN   -> Block.createCuboidShape(dw, 0, dw, 16 - dw, height, 16 - dw);
                case NORTH  -> Block.createCuboidShape(dw, dw, 0, 16 - dw, 16 - dw, height);
                case EAST   -> Block.createCuboidShape(16 - height, dw, dw, 16, 16 - dw, 16 - dw);
                case SOUTH  -> Block.createCuboidShape(dw, dw, 16 - height, 16 - dw, 16 - dw, 16);
                case WEST   -> Block.createCuboidShape(0, dw, dw, height, 16 - dw, 16 - dw);
            };
        }

        return boundingBoxPerStage;
    }
}
