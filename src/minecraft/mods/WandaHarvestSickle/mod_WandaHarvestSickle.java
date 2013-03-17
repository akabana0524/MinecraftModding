package mods.WandaHarvestSickle;

import java.util.HashMap;
import java.util.logging.Level;

import mods.WandaCore.MaterialTable;
import mods.WandaCore.WandaModBase;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Property;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

@Mod(modid = "WandaHarvestSickle", name = "Wanda Harvest Sickle", version = "0.3.0", dependencies = "required-after:WandaCore")
public class mod_WandaHarvestSickle extends WandaModBase {

	private HashMap<EnumToolMaterial, HarvestSickle> list;

	@Init
	public void init(FMLInitializationEvent event) {
		super.init(event);
		FMLLog.log(Level.INFO, "Init " + getModID());
		list = new HashMap<EnumToolMaterial, HarvestSickle>();
		Property propertyItemID = config.get("general", "ItemID", 5010);
		Property propertyEnableEffect = config.get("general", "EnableEffect",
				true);
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
			HarvestSickle temp = new HarvestSickle(itemID, 3, mat);
			temp.setEnableEffect(propertyEnableEffect.getBoolean(true));
			temp.setUnlocalizedName(MaterialTable.getMaterialName(mat)
					+ "HarvestSickle");
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
