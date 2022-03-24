package dev.foltz.de.plant.plants;

import dev.foltz.de.block.BlockUtils;
import dev.foltz.de.block.ModBlockFactory;
import dev.foltz.de.plant.Plant;
import dev.foltz.de.plant.PlantBehavior;
import dev.foltz.de.plant.PlantState;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.IntProperty;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.Direction;

import java.util.function.Function;

import static dev.foltz.de.util.PlantUtil.*;

public class ConwayPlant {
    public static final Plant CONWAY_PLANT = new Plant("conway");

    public static final IntProperty GROWTH_STAGE = IntProperty.of("growth_stage", 0, 3);

    public static final Function<PlantState, Integer> NEIGHBORS = state -> state.neighbors(2, 1);

    public static final PlantBehavior CONWAY_PLANT_BEHAVIOR = new PlantBehavior()
            .withAction(all(eq(GROWTH_STAGE, 3), lte(NEIGHBORS, 5)), state -> state.spawnNearby(2, 1))
            .withTransition(all(gte(NEIGHBORS, 7), eq(GROWTH_STAGE, 0)), T_DEAD)
            .withTransition(all(gte(NEIGHBORS, 7), gt(GROWTH_STAGE, 0)), dec(GROWTH_STAGE))
            .withTransition(lt(GROWTH_STAGE, 3), inc(GROWTH_STAGE));

    public static final Block CONWAY_PLANT_BLOCK = ModBlockFactory
            .builder(ModBlockFactory.SETTINGS_PLANT_DEFAULT)
            .withDefaultProperty(GROWTH_STAGE, 0)
            .withShape(((blockState, blockView, blockPos, shapeContext)
                    -> BlockUtils.lerpBoundingBoxes(4, Direction.DOWN, 3, 10, 6, 14)
                    [blockState.get(GROWTH_STAGE)]))
            .canExist((world, state, pos) -> {
                BlockState bs = world.getBlockState(pos.down());
                return bs.isIn(BlockTags.DIRT);
            })
            .withTick((world, blockState, blockPos, random) -> new PlantState(CONWAY_PLANT, world, blockPos).tick())
            .create();

}
