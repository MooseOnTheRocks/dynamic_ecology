package dev.foltz.de.plant;

import dev.foltz.de.DEMod;
import dev.foltz.de.block.*;
import dev.foltz.de.item.PlantSeedItem;
import dev.foltz.de.plant.plants.RainPlant;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.Direction;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

import static dev.foltz.de.plant.plants.ConwayPlant.*;
import static dev.foltz.de.plant.plants.CubeStalkPlant.*;
import static dev.foltz.de.plant.plants.RainPlant.*;

public class Plants {
    public static final Map<Plant, Block> ALL_PLANT_BLOCKS = new HashMap<>();
    public static final Map<Plant, BlockItem> ALL_PLANT_SEEDS = new HashMap<>();

    public static void registerAllPlants() {
        registerPlantComponents(CONWAY_PLANT, CONWAY_PLANT_BEHAVIOR, CONWAY_PLANT_BLOCK);
        registerPlantSeed(CONWAY_PLANT);

        registerPlantBlock(REACT_DIFF_PLANT, REACT_DIFF_PLANT_BLOCK);
        registerPlantSeed(REACT_DIFF_PLANT);

        registerPlantBlock(EYE_PLANT, EYE_PLANT_BLOCK);
        registerPlantSeed(EYE_PLANT);

        registerPlantBlock(SUN_PLANT, SUN_PLANT_BLOCK);
        registerPlantSeed(SUN_PLANT);

        registerPlantComponents(RAIN_PLANT, RAIN_PLANT_BEHAVIOR, RAIN_PLANT_BLOCK);
        registerPlantSeed(RAIN_PLANT);

        registerPlantComponents(CUBE_STALK_PLANT, CUBE_STALK_PLANT_BEHAVIOR, CUBE_STALK_PLANT_BLOCK);
        registerPlantSeed(CUBE_STALK_PLANT);
    }

    /*********************
     * Plant definitions *
     *********************/

    /* Plants */

    public static final Plant REACT_DIFF_PLANT = new Plant("react_diff");
    public static final Plant EYE_PLANT = new Plant("eye");
    public static final Plant SUN_PLANT = new Plant("sun");


    /* Block Properties */



    /* Blocks */
    public static final Block REACT_DIFF_PLANT_BLOCK = new ReactDiffPlant();
    public static final Block EYE_PLANT_BLOCK = new EyePlant();
    public static final Block SUN_PLANT_BLOCK = new SunPlant();





    /* Registration Functions */
    public static Plant registerPlantComponents(Plant plant, PlantBehavior behavior, Block block) {
        registerPlantBehavior(plant, behavior);
        registerPlantBlock(plant, block);
        return plant;
    }

    public static PlantBehavior registerPlantBehavior(Plant plant, PlantBehavior behavior) {
        plant.withBehavior(behavior);
        return behavior;
    }

    public static Block registerPlantBlock(Plant plant, Block plantBlock) {
        Block registered = DEMod.registerBlock(plant.name + "_plant", plantBlock);
        plant.withBlock(registered);
        ALL_PLANT_BLOCKS.put(plant, registered);
        return registered;
    }

    public static BlockItem registerPlantSeed(Plant plant) {
        Block plantBlock = ALL_PLANT_BLOCKS.get(plant);
        BlockItem blockItem = (BlockItem) DEMod.registerItem(plant.name + "_plant_seed", new PlantSeedItem(plantBlock, new FabricItemSettings().group(ItemGroup.MISC)));
        ALL_PLANT_SEEDS.put(plant, blockItem);
        return blockItem;
    }
}
