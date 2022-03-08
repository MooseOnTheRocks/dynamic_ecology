package dev.foltz.de;

import dev.foltz.de.block.CABlock;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DEMod implements ModInitializer {
    public static final String MODID = "dynamic_ecology";
    public static final Logger LOG = LoggerFactory.getLogger(MODID);

    public static final Block CABlock = new CABlock();

    @Override
    public void onInitialize() {
        LOG.info("Hello, " + MODID + "!");
        Registry.register(Registry.BLOCK, new Identifier(MODID, "ca_block"), CABlock);
        Registry.register(Registry.ITEM, new Identifier(MODID, "ca_seeds"), new BlockItem(CABlock, new FabricItemSettings().group(ItemGroup.MISC)));
    }
}
