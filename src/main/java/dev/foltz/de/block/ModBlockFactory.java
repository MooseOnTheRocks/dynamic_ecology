package dev.foltz.de.block;

import com.mojang.datafixers.util.Function4;
import dev.foltz.de.IFunction4V;
import dev.foltz.de.IPredicate3;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.BiPredicate;

public class ModBlockFactory {
    public static final FabricBlockSettings SETTINGS_PLANT_DEFAULT =
            FabricBlockSettings.of(Material.PLANT)
                    .noCollision()
                    .solidBlock((state, world, pos) -> false)
                    .nonOpaque()
                    .breakInstantly()
                    .sounds(BlockSoundGroup.GRASS);

    public static final FabricBlockSettings SETTINGS_PLANT_SOLID =
            FabricBlockSettings.of(Material.PLANT)
                    .nonOpaque()
                    .breakInstantly()
                    .sounds(BlockSoundGroup.STEM);

    public static ModBlockBuilder builder(AbstractBlock.Settings settings) {
        return new ModBlockBuilder(settings);
    }

    /*
     *  Blocks have:
     *      Block Settings
     *      Block Properties
     *      Bounding Boxes
     *
     *
     */
    public static class ModBlockBuilder {
        protected FabricBlockSettings blockSettings;
        protected Map<Property<?>, Object> defaultProperties;
        protected Function4<BlockState, BlockView, BlockPos, ShapeContext, VoxelShape> shapeProvider;
        protected IFunction4V<World, BlockState, BlockPos, Random> randomTick;
        protected IPredicate3<WorldView, BlockState, BlockPos> shouldBreak;

        public ModBlockBuilder(AbstractBlock.Settings settings) {
            blockSettings = FabricBlockSettings.copyOf(settings);
            defaultProperties = new HashMap<>();
            shapeProvider = (s, w, p, c) -> Block.createCuboidShape(0, 0, 0, 16, 16, 16);
            randomTick = (w, bs, bp, r) -> {};
            shouldBreak = (w, bs, bp) -> false;
        }

        public ModBlockBuilder withTick(IFunction4V<World, BlockState, BlockPos, Random> tick) {
            this.randomTick = tick;
            return this;
        }

        public ModBlockBuilder withBreakPredicate(IPredicate3<WorldView, BlockState, BlockPos> breakPred) {
            this.shouldBreak = breakPred;
            return this;
        }

        public ModBlockBuilder shapeProvider(Function4<BlockState, BlockView, BlockPos, ShapeContext, VoxelShape> provider) {
            this.shapeProvider = provider;
            return this;
        }

        public ModBlockBuilder(AbstractBlock block) {
            blockSettings = FabricBlockSettings.copyOf(block);
        }

        public <T extends Comparable<T>, V extends T> ModBlockBuilder withDefaultProperty(Property<T> property, V value) {
            this.defaultProperties.put(property, value);
            return this;
        }

        public ModBlock create() {
            return new ModBlock(blockSettings) {
                @Override
                public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
                    return shapeProvider.apply(state, world, pos, context);
                }

                @Override
                public Map<Property<?>, ?> defaultBlockProperties() {
                    return Map.copyOf(defaultProperties);
                }

                @Override
                public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
                    if (!state.canPlaceAt(world, pos)) {
                        world.breakBlock(pos, true);
                    }
                }

                @Override
                public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
                    randomTick.apply(world, state, pos, random);
                }

                @Override
                public boolean hasRandomTicks(BlockState state) {
                    return true;
                }

                @Override
                public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
                    return !shouldBreak.test(world, state, pos);
                }

                @Override
                public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
                    if (!state.canPlaceAt(world, pos)) {
                        world.createAndScheduleBlockTick(pos, this, 1);
                    }

                    return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
                }
            };
        }
    }
}
