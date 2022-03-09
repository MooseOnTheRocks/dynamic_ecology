package dev.foltz.de.block;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.Random;


public abstract class CAPlant extends PlantBlock {
    public static final IntProperty GROWTH_STAGE = IntProperty.of("growth_stage", 0, 3);
    public final VoxelShape[] boundingBoxes;

    public CAPlant(int numStages, double minWidth, double maxWidth, double minHeight, double maxHeight) {
        super(FabricBlockSettings.of(Material.PLANT).noCollision().solidBlock((state, world, pos) -> false).nonOpaque());
        setDefaultState(this.getStateManager().getDefaultState().with(GROWTH_STAGE, 0));
        boundingBoxes = BlockUtils.lerpBoundingBoxes(4, Direction.DOWN, minWidth, maxWidth, minHeight, maxHeight);
    }

    public abstract void plantTick(World world, BlockState chosenBlockState, BlockPos chosenPos, Random random);

    @Override
    protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
        return floor.isIn(BlockTags.DIRT);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return boundingBoxes[state.get(CAPlant.GROWTH_STAGE)];
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(GROWTH_STAGE);
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
        this.plantTick(world, state, pos, random);
    }
}
