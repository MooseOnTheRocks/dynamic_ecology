package dev.foltz.de;

import dev.foltz.de.block.ConwayPlant;
import dev.foltz.de.block.EyePlant;
import dev.foltz.de.block.ReactDiffPlant;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DEMod implements ModInitializer {
    public static final String MODID = "dynamic_ecology";
    public static final Logger LOG = LoggerFactory.getLogger(MODID);

    public static final Block CONWAY_PLANT = new ConwayPlant();
    public static final Block REACT_DIFF_PLANT = new ReactDiffPlant();
    public static final Block EYE_PLANT = new EyePlant();

    @Override
    public void onInitialize() {
        LOG.info("Hello, " + MODID + "!");
        Registry.register(Registry.BLOCK, new Identifier(MODID, "conway_plant"), CONWAY_PLANT);
        Registry.register(Registry.ITEM, new Identifier(MODID, "conway_plant_seeds"), new BlockItem(CONWAY_PLANT, new FabricItemSettings().group(ItemGroup.MISC)));

        Registry.register(Registry.BLOCK, new Identifier(MODID, "react_diff_plant"), REACT_DIFF_PLANT);
        Registry.register(Registry.ITEM, new Identifier(MODID, "react_diff_plant_seeds"), new BlockItem(REACT_DIFF_PLANT, new FabricItemSettings().group(ItemGroup.MISC)));

        Registry.register(Registry.BLOCK, new Identifier(MODID, "eye_plant"), EYE_PLANT);
        Registry.register(Registry.ITEM, new Identifier(MODID, "eye_plant_seeds"), new BlockItem(EYE_PLANT, new FabricItemSettings().group(ItemGroup.MISC)));
    }
}
