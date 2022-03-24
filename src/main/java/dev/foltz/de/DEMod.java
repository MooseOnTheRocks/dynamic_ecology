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

    @Override
    public void onInitialize() {
        LOG.info("Hello, " + MODID + "!");
        Plants.registerAllPlants();
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
