package dev.foltz.de.block;

import com.mojang.datafixers.util.Function4;
import dev.foltz.de.AbstractModBlock;
import dev.foltz.de.IFunction4V;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ModBlockFactory {
    public static final FabricBlockSettings SETTINGS_PLANT_DEFAULT =
            FabricBlockSettings.of(Material.PLANT)
                    .noCollision()
                    .solidBlock((state, world, pos) -> false)
                    .nonOpaque()
                    .breakInstantly()
                    .sounds(BlockSoundGroup.GRASS);

    public static ModBlockBuilder builder(AbstractBlock.Settings settings) {
        return new ModBlockBuilder(settings);
    }

    /*
     *  Blocks have bounding boxes.
     *  Blocks have block properties.
     */
    public static class ModBlockBuilder {
        protected FabricBlockSettings blockSettings;
        protected Map<Property<?>, Object> defaultProperties;
        protected Function4<BlockState, BlockView, BlockPos, ShapeContext, VoxelShape> shapeProvider;
        protected IFunction4V<World, BlockState, BlockPos, Random> plantTick;

        public ModBlockBuilder(AbstractBlock.Settings settings) {
            blockSettings = FabricBlockSettings.copyOf(settings);
            defaultProperties = new HashMap<>();
            shapeProvider = (s, w, p, c) -> Block.createCuboidShape(0, 0, 0, 16, 16, 16);
            plantTick = (w, bs, bp, r) -> {};
        }

        public ModBlockBuilder withPlantTick(IFunction4V<World, BlockState, BlockPos, Random> plantTick) {
            this.plantTick = plantTick;
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

        public AbstractModBlock create() {
            return new AbstractModBlock(blockSettings) {
                @Override
                public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
                    return shapeProvider.apply(state, world, pos, context);
                }

                @Override
                public Map<Property<?>, ?> defaultBlockProperties() {
                    return Map.copyOf(defaultProperties);
                }

                @Override
                public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
                    plantTick.apply(world, state, pos, random);
                }

                @Override
                public boolean hasRandomTicks(BlockState state) {
                    return true;
                }
            };
        }
    }
}
