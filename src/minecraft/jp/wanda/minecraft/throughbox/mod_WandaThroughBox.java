package jp.wanda.minecraft.throughbox;

import net.minecraft.src.Block;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Material;
import net.minecraftforge.common.Property;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import jp.wanda.minecraft.arrowtargetblock.ArrowTargetBlock;
import jp.wanda.minecraft.core.WandaModBase;
import jp.wanda.minecraft.steam.WandaSteamFuelGenerator;

@Mod(modid = "WandaThroughBox", name = "Wanda Through Box", version = "0.0.1", dependencies = "required-after:WandaResource")
public class mod_WandaThroughBox extends WandaModBase {

	public static Block block;

	@Override
	protected String getModID() {
		return "WandaThroughBox";
	}

	@Init
	public void init(FMLInitializationEvent event) {
		super.init(event);
		FMLLog.info("Init " + getModID());
		GameRegistry.registerTileEntity(ThroughBoxTileEntity.class,
				"WandaThroughBox");
		Property propertyBlockID = config.getBlock("ThroughBox", 151);
		config.save();
		block = new ThroughBox(propertyBlockID.getInt(), Material.wood);
		block.setBlockName("Through Box");
		block.setStepSound(Block.soundWoodFootstep);
		block.setCreativeTab(CreativeTabs.tabBlock);
		GameRegistry.registerBlock(block);
		LanguageRegistry.addName(block, "Through Box");
		GameRegistry.addRecipe(new ItemStack(block), new Object[] { "X X",
				"YZY", "YYY", 'X', Item.ingotIron, 'Y', Block.planks, 'Z',
				Block.dispenser });
	}

}
