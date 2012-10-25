package jp.wanda.minecraft.harvestsickle;

import java.util.HashMap;
import java.util.logging.Level;

import jp.wanda.minecraft.MaterialTable;
import jp.wanda.minecraft.WandaModBase;
import net.minecraft.src.EnumToolMaterial;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraftforge.common.Property;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

@Mod(modid = "WandaHarvestSickle", name = "Wanda Harvest Sickle", version = "0.3.0", dependencies = "required-after:WandaResource")
public class mod_WandaHarvestSickle extends WandaModBase {

	private HashMap<EnumToolMaterial, HarvestSickle> list;

	@Init
	public void init(FMLInitializationEvent event) {
		super.init(event);
		FMLLog.log(Level.INFO, "Init " + getModID());
		list = new HashMap<EnumToolMaterial, HarvestSickle>();
		Property propertyItemID = config.get("ItemID",
				"general", 5010);
		Property propertyEnableEffect = config.get(
				"EnableEffect", "general", true);
		config.save();
		int itemID = propertyItemID.getInt() - 256;

		for (EnumToolMaterial mat : EnumToolMaterial.values()) {
			if (mat.equals(EnumToolMaterial.WOOD)
					|| mat.equals(EnumToolMaterial.STONE)) {
				continue;
			}
			Object materialObject = null;
			try {
				materialObject = MaterialTable.getMaterial(mat);
			} catch (Exception e) {
				System.out.println("WandaHarvestSickle not support " + mat);
				continue;
			}
			HarvestSickle temp = new HarvestSickle(itemID,
					16 * 2 + MaterialTable.getTextureU(mat), 3, mat);
			temp.setEnableEffect(propertyEnableEffect.getBoolean(true));
			temp.setItemName(MaterialTable.getMaterialName(mat)
					+ " Harvest Sickle");
			LanguageRegistry.addName(temp, MaterialTable.getMaterialName(mat)
					+ " Harvest Sickle");
			GameRegistry.addRecipe(new ItemStack(temp), new Object[] { "XXY",
					"X Y", "X Y", 'X', materialObject, 'Y', Item.stick, });
			list.put(mat, temp);
			itemID++;
		}
	}

	@Override
	protected String getModID() {
		return "WandaHarvestSickle";
	}
}
