package dev.foltz.de.plant.plants;

import dev.foltz.de.block.ModBlockFactory;
import dev.foltz.de.plant.*;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.tag.BlockTags;

import java.util.function.Predicate;

import static dev.foltz.de.util.PlantUtil.*;

public class RainPlant {
    public static final Plant RAIN_PLANT = new Plant("rain");

    public static final BooleanProperty WILTED = BooleanProperty.of("wilted");
    public static final Predicate<PlantState> IS_WET = state -> state.isInTheRain() || state.nearWater(2, 2, false);
    public static final Predicate<PlantState> IS_WILTED = require(WILTED, true);

    public static final PlantBehavior RAIN_PLANT_BEHAVIOR = new PlantBehavior()
            .withAction(all(not(IS_WILTED), IS_WET), state -> state.spawnNearby(1, 1))
            .withTransition(all(IS_WILTED, IS_WET), apply(WILTED, false))
            .withTransition(all(IS_WILTED, not(IS_WET)), T_DEAD)
            .withTransition(all(not(IS_WILTED), not(IS_WET)), apply(WILTED, true));

    public static final FabricBlockSettings RAIN_PLANT_BLOCK_SETTINGS = FabricBlockSettings
            .of(Material.PLANT)
            .noCollision()
            .solidBlock((state, world, pos) -> false)
            .nonOpaque()
            .breakInstantly()
            .sounds(BlockSoundGroup.GRASS);

    public static final Block RAIN_PLANT_BLOCK = ModBlockFactory
            .builder(RAIN_PLANT_BLOCK_SETTINGS)
            .withDefaultProperty(RainPlant.WILTED, true)
            .withShape(((blockState, blockView, blockPos, shapeContext) -> blockState.get(RainPlant.WILTED)
                    ? Block.createCuboidShape(2, 0, 2, 14, 5, 14)
                    : Block.createCuboidShape(2, 0, 2, 13, 9, 13)
            ))
            .canExist((world, state, pos) -> {
                BlockState bs = world.getBlockState(pos.down());
                return bs.isIn(BlockTags.DIRT) || bs.isIn(BlockTags.SAND);
            })
            .withTick((world, blockState, blockPos, random) -> new PlantState(RAIN_PLANT, world, blockPos).tick())
            .create();
}
