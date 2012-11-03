package jp.wanda.minecraft.arrowtargetblock;

import jp.wanda.minecraft.core.WandaModBase;
import net.minecraft.src.Block;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.MLProp;
import net.minecraft.src.Material;
import net.minecraftforge.common.Property;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

@Mod(modid = "WandaArrowTarget", name = "Wanda Arrow Target", version = "0.5.0", dependencies = "required-after:WandaResource")
public class mod_WandaArrowTargetBlock extends WandaModBase {

	public static Block block;

	@Init
	public void init(FMLInitializationEvent event) {
		super.init(event);
		FMLLog.info("Init WandaArrowTargetBlock");

		Property propertyBlockID = config.getBlock("ArrowTargetBlock", 150);
		config.save();
		block = new ArrowTargetBlock(propertyBlockID.getInt(), Material.wood);
		block.setBlockName("Arrow Target");
		block.setStepSound(Block.soundWoodFootstep);
		block.setCreativeTab(CreativeTabs.tabBlock);
		GameRegistry.registerBlock(block);
		LanguageRegistry.addName(block, "Arrow Target");
		GameRegistry.addShapelessRecipe(new ItemStack(block), new Object[] {
				new ItemStack(Item.redstone, 1),
				new ItemStack(Block.planks, 1, 0) });
		GameRegistry.addShapelessRecipe(new ItemStack(block), new Object[] {
				new ItemStack(Item.redstone, 1),
				new ItemStack(Block.planks, 1, 1) });
		GameRegistry.addShapelessRecipe(new ItemStack(block), new Object[] {
				new ItemStack(Item.redstone, 1),
				new ItemStack(Block.planks, 1, 2) });
		GameRegistry.addShapelessRecipe(new ItemStack(block), new Object[] {
				new ItemStack(Item.redstone, 1),
				new ItemStack(Block.planks, 1, 3) });
	}

	@Override
	protected String getModID() {
		return "WandaArrowTargetBlock";
	}

}
