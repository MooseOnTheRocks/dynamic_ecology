package dev.foltz.de;

import dev.foltz.de.block.*;
import dev.foltz.de.plant.Plants;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.stream.Stream;

public class DEMod implements ModInitializer {
    public static final String MODID = "dynamic_ecology";
    public static final Logger LOG = LoggerFactory.getLogger(MODID);

//    public static final Block CONWAY_PLANT = new ConwayPlant();
    public static final Block REACT_DIFF_PLANT = new ReactDiffPlant();
    public static final Block EYE_PLANT = new EyePlant();
    public static final Block SUN_PLANT = new SunPlant();

    public static final IntProperty TESTING_GROWTH_STAGE = IntProperty.of("growth_stage", 0, 3);

    public static void plantTickConway(World world, BlockState blockState, BlockPos pos, Random random) {
        final int RANGE = 2;
        Stream<BlockState> blocksInRange = BlockUtils.streamBlocksInRange(pos, RANGE).map(world::getBlockState);
        int neighbors = (int) blocksInRange.filter(bs -> bs.getBlock() == DEMod.CONWAY_PLANT).count();

        // Death mechanics
        if (neighbors < 2 || neighbors > 6) {
            world.breakBlock(pos, true);
            return;
        }

        // Growth mechanics
        int stage = blockState.get(TESTING_GROWTH_STAGE);
        if (stage < 3) {
            world.setBlockState(pos, blockState.with(TESTING_GROWTH_STAGE, stage + 1));
            return;
        }

        // Spawn mechanics
        if (stage == 3) {
            double x = Math.round(pos.getX() + RANGE - 2 * random.nextDouble() * RANGE);
            double y = Math.round(pos.getY() + RANGE - 2 * random.nextDouble() * RANGE);
            double z = Math.round(pos.getZ() + RANGE - 2 * random.nextDouble() * RANGE);
            BlockPos chosenPos = new BlockPos(x, y, z);
            BlockState chosenBlock = world.getBlockState(chosenPos);
            if (chosenBlock.getBlock() != Blocks.AIR && chosenBlock.getMaterial() != Material.REPLACEABLE_PLANT) {
                return;
            }
            if (!(world.getBlockState(chosenPos.down()).isIn(BlockTags.DIRT))) {
                return;
            }

            // Simulate a dead cell for this plant.
            Stream<BlockState> chosenBlocksInRange = BlockUtils.streamBlocksInRange(chosenPos, RANGE).map(world::getBlockState);
            int chosenNeighbors = (int) chosenBlocksInRange.filter(bs -> bs.getBlock() == DEMod.CONWAY_PLANT).count();
            chosenBlocksInRange = BlockUtils.streamBlocksInRange(chosenPos, RANGE).map(world::getBlockState);
            int chosenFullyGrownNeighbors = (int) chosenBlocksInRange.filter(bs -> bs.getBlock() == DEMod.CONWAY_PLANT && bs.get(TESTING_GROWTH_STAGE) == 3).count();
            if (chosenFullyGrownNeighbors >= 1 && chosenNeighbors >= 2 && chosenNeighbors <= 4) {
                world.setBlockState(chosenPos, DEMod.CONWAY_PLANT.getDefaultState());
            }

            return;
        }
    }

    public static final Block CONWAY_PLANT = ModBlockFactory
            .builder(ModBlockFactory.SETTINGS_PLANT_DEFAULT)
            .withDefaultProperty(Properties.WATERLOGGED, false)
            .withDefaultProperty(TESTING_GROWTH_STAGE, 0)
            .shapeProvider(((blockState, blockView, blockPos, shapeContext)
                    -> BlockUtils.lerpBoundingBoxes(4, Direction.DOWN, 3, 10, 6, 14)
                        [blockState.get(TESTING_GROWTH_STAGE)]))
            .withTick(DEMod::plantTickConway)
            .create();

    public static final BooleanProperty PROPERTY_TWO_TALL = BooleanProperty.of("two_tall");
    public static final Block CUBE_STALK_PLANT = ModBlockFactory.builder(ModBlockFactory.SETTINGS_PLANT_SOLID)
            .withDefaultProperty(PROPERTY_TWO_TALL, false)
            .shapeProvider(((bs, w, bp, sc) -> {
                if (bs.get(PROPERTY_TWO_TALL)) {
                    return Block.createCuboidShape(4, 0, 4, 12, 16, 12);
                }
                else {
                    return Block.createCuboidShape(4, 0, 4, 12, 8, 12);
                }
            }))
            .withBreakPredicate((worldView, blockState, blockPos) -> {
                return worldView.getBlockState(blockPos.down()).isAir();
            })
            .withTick((world, blockState, blockPos, random) -> {
//                if (random.nextFloat() < 0.9) {
//                    return;
//                }
                if (!blockState.get(PROPERTY_TWO_TALL)) {
                    world.setBlockState(blockPos, blockState.with(PROPERTY_TWO_TALL, true));
                }
                else {
                    if (world.getBlockState(blockPos.up()).isAir()) {
                        BlockState bs = blockState.with(PROPERTY_TWO_TALL, false);
                        world.setBlockState(blockPos.up(), bs);
                        bs.neighborUpdate(world, blockPos, blockState.getBlock(), blockPos.up(), false);
                    }
                }
            }).create();

    public static final BooleanProperty WILTED = BooleanProperty.of("wilted");
    public static void tickRainPlant(World world, BlockState blockState, BlockPos blockPos, Random random) {
        final int range = 3;
        final int SPAWN_RANGE = 1;
        boolean inTheRain = world.isRaining() && world.isSkyVisible(blockPos);
        boolean wilted = blockState.get(WILTED);
        boolean nearWater = BlockUtils.streamBlocksInRange(blockPos, range)
                .anyMatch(bp -> world.getFluidState(bp).getFluid() == Fluids.WATER);

        // Reproduce
        if (inTheRain || nearWater) {
            if (wilted) {
                world.setBlockState(blockPos, blockState.with(WILTED, false));
                return;
            }
            double x = Math.round(blockPos.getX() + SPAWN_RANGE - 2 * random.nextDouble() * SPAWN_RANGE);
            double y = Math.round(blockPos.getY() + SPAWN_RANGE - 2 * random.nextDouble() * SPAWN_RANGE);
            double z = Math.round(blockPos.getZ() + SPAWN_RANGE - 2 * random.nextDouble() * SPAWN_RANGE);
            BlockPos chosenPos = new BlockPos(x, y, z);
            BlockState chosenBlock = world.getBlockState(chosenPos);
            if (!(chosenBlock.getBlock() == Blocks.AIR || chosenBlock.getMaterial() == Material.REPLACEABLE_PLANT)) {
                return;
            }
            if (!world.getBlockState(chosenPos.down()).isIn(BlockTags.DIRT)) {
                return;
            }
            world.setBlockState(chosenPos, blockState.getBlock().getDefaultState());
        }
        // Wilt and die
        else {
            if (wilted) {
                world.breakBlock(blockPos, true);
            }
            else {
                world.setBlockState(blockPos, blockState.with(WILTED, true));
            }
        }
    }

    public static final Block RAIN_PLANT = ModBlockFactory
            .builder(ModBlockFactory.SETTINGS_PLANT_DEFAULT)
            .withDefaultProperty(WILTED, true)
            .shapeProvider(((blockState, blockView, blockPos, shapeContext) -> blockState.get(WILTED)
                    ? Block.createCuboidShape(2, 0, 2, 14, 5, 14)
                    : Block.createCuboidShape(2, 0, 2, 13, 9, 13)
            ))
            .withTick(DEMod::tickRainPlant)
            .create();

    @Override
    public void onInitialize() {
        LOG.info("Hello, " + MODID + "!");

//        Registry.register(Registry.BLOCK, new Identifier(MODID, "cube_stalk_plant"), CUBE_STALK_PLANT);
//        Registry.register(Registry.ITEM, new Identifier(MODID, "cube_stalk_plant_seeds"), new BlockItem(CUBE_STALK_PLANT, new FabricItemSettings().group(ItemGroup.MISC)));

        registerBlock("cube_stalk_plant", CUBE_STALK_PLANT);
        registerBlockItem("cube_stalk_plant_seeds", CUBE_STALK_PLANT);

        registerBlock("conway_plant", CONWAY_PLANT);
        registerBlockItem("conway_plant_seeds", CONWAY_PLANT);

        registerBlock("react_diff_plant", REACT_DIFF_PLANT);
        registerBlockItem("react_diff_plant_seeds", REACT_DIFF_PLANT);

        registerBlock("eye_plant", EYE_PLANT);
        registerBlockItem("eye_plant_seeds", EYE_PLANT);

        registerBlock("sun_plant", SUN_PLANT);
        registerBlockItem("sun_plant_seeds", SUN_PLANT);

        registerBlock("rain_plant", RAIN_PLANT);
        registerBlockItem("rain_plant_seeds", RAIN_PLANT);

//        Registry.register(Registry.BLOCK, new Identifier(MODID, "conway_plant"), CONWAY_PLANT);
//        Registry.register(Registry.ITEM, new Identifier(MODID, "conway_plant_seeds"), new BlockItem(CONWAY_PLANT, new FabricItemSettings().group(ItemGroup.MISC)));

//        Registry.register(Registry.BLOCK, new Identifier(MODID, "conway_plant"), CONWAY_PLANT);
//        Registry.register(Registry.ITEM, new Identifier(MODID, "conway_plant_seeds"), new BlockItem(CONWAY_PLANT, new FabricItemSettings().group(ItemGroup.MISC)));

//        Registry.register(Registry.BLOCK, new Identifier(MODID, "react_diff_plant"), REACT_DIFF_PLANT);
//        Registry.register(Registry.ITEM, new Identifier(MODID, "react_diff_plant_seeds"), new BlockItem(REACT_DIFF_PLANT, new FabricItemSettings().group(ItemGroup.MISC)));

//        Registry.register(Registry.BLOCK, new Identifier(MODID, "eye_plant"), EYE_PLANT);
//        Registry.register(Registry.ITEM, new Identifier(MODID, "eye_plant_seeds"), new BlockItem(EYE_PLANT, new FabricItemSettings().group(ItemGroup.MISC)));
//
//        Registry.register(Registry.BLOCK, new Identifier(MODID, "sun_plant"), SUN_PLANT);
//        Registry.register(Registry.ITEM, new Identifier(MODID, "sun_plant_seeds"), new BlockItem(SUN_PLANT, new FabricItemSettings().group(ItemGroup.MISC)));
    }

    public static Block registerBlock(String name, Block block) {
        return Registry.register(Registry.BLOCK, new Identifier(MODID, name), block);
    }

    public static BlockItem registerBlockItem(String name, Block block) {
        return Registry.register(Registry.ITEM, new Identifier(MODID, name), new BlockItem(block, new FabricItemSettings().group(ItemGroup.MISC)));
    }

    public static Item registerItem(String name, Item item) {
        return Registry.register(Registry.ITEM, new Identifier(MODID, name), item);
    }
}
