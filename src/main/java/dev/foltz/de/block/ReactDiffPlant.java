package dev.foltz.de.block;

import dev.foltz.de.DEMod;
import net.minecraft.block.*;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.Random;
import java.util.stream.Stream;

public class ReactDiffPlant extends AbstractSingleBlockPlant {
    public static final int ACTIVATION_RANGE = 1;
    public static final int INHIBITION_RANGE = 4;
    public static int RANGE = Math.max(ACTIVATION_RANGE, INHIBITION_RANGE);
    public static final IntProperty GROWTH_STAGE = IntProperty.of("growth_stage", 0, 2);

    public final VoxelShape[] boundingBoxes;

    public ReactDiffPlant() {
        super();
        this.boundingBoxes = BlockUtils.lerpBoundingBoxes(3, Direction.DOWN, 5, 8, 4, 13);
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
        Stream<BlockPos> blocksInRange = BlockUtils.streamBlocksInRange(pos, RANGE);
        double stage1Amount = 2d;
        double stage2Amount = 12d;
        double INC = 4d;
        double DEC = -0.6d;
        double V = blocksInRange
                .filter(bp -> world.getBlockState(bp).getBlock() == DEMod.REACT_DIFF_PLANT)
                .map(bp -> {
                    double dx = pos.getX() - bp.getX();
                    double dy = pos.getY() - bp.getY();
                    double dz = pos.getZ() - bp.getZ();
                    double dist = Math.sqrt(dx*dx + dy*dy + dz*dz);
                    BlockState bs = world.getBlockState(bp);
                    if (dist <= ACTIVATION_RANGE) {
                        return INC;
                    }
                    else if (dist <= INHIBITION_RANGE) {
                        return DEC;
                    }
                    else {
                        return 0d;
                    }
                })
                .reduce(0d, Double::sum);

        // Death mechanics
        if (V <= 0) {
            int stage = blockState.get(GROWTH_STAGE);
            if (stage == 0) {
                world.breakBlock(pos, true);
            }
            else {
                world.setBlockState(pos, blockState.with(GROWTH_STAGE, stage - 1));
            }
            return;
        }
        // Spawn mechanics
        else if (V >= 0) {
            int stage = blockState.get(GROWTH_STAGE);
            if (stage == 0 && V >= stage1Amount) {
                world.setBlockState(pos, blockState.with(GROWTH_STAGE, stage + 1));
            }
            else if (stage == 1 && V >= stage2Amount) {
                world.setBlockState(pos, blockState.with(GROWTH_STAGE, stage + 1));
            }

            if (blockState.get(GROWTH_STAGE) < 1) {
                return;
            }
            double x = Math.round(pos.getX() + ACTIVATION_RANGE - 2 * random.nextDouble() * ACTIVATION_RANGE);
            double y = Math.round(pos.getY() + ACTIVATION_RANGE - 2 * random.nextDouble() * ACTIVATION_RANGE);
            double z = Math.round(pos.getZ() + ACTIVATION_RANGE - 2 * random.nextDouble() * ACTIVATION_RANGE);
            BlockPos chosenPos = new BlockPos(x, y, z);
            BlockState chosenBlock = world.getBlockState(chosenPos);
            if (chosenBlock.getBlock() != Blocks.AIR && chosenBlock.getMaterial() != Material.REPLACEABLE_PLANT) {
                return;
            }
            if (!canPlantOnTop(world.getBlockState(chosenPos.down()), world, chosenPos)) {
                return;
            }

            // Simulate a dead cell for this plant.
            double _V = BlockUtils.streamBlocksInRange(pos, RANGE)
                    .filter(bp -> world.getBlockState(bp).getBlock() == DEMod.REACT_DIFF_PLANT)
                    .map(bp -> {
                        double dx = pos.getX() - bp.getX();
                        double dy = pos.getY() - bp.getY();
                        double dz = pos.getZ() - bp.getZ();
                        double dist = Math.sqrt(dx*dx + dy*dy + dz*dz);
                        if (dist <= ACTIVATION_RANGE) {
                            return INC;
                        }
                        else if (dist <= INHIBITION_RANGE) {
                            return DEC;
                        }
                        else {
                            return 0d;
                        }
                    })
                    .reduce(0d, Double::sum);
            if (_V > 0) {
                world.setBlockState(chosenPos, DEMod.REACT_DIFF_PLANT.getDefaultState());
            }

            return;
        }
    }
}
