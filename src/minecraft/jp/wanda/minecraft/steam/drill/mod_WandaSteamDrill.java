package jp.wanda.minecraft.steam.drill;

import jp.wanda.minecraft.MaterialTable;
import jp.wanda.minecraft.WandaModBase;
import net.minecraft.src.EnumToolMaterial;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.LanguageRegistry;

@Mod(modid = "WandaSteamDrill", name = "Wanda Steam Drill", version = "0.0.1", dependencies = "required-after:WandaSteamCore")
@NetworkMod(clientSideRequired = true, serverSideRequired = false)
public class mod_WandaSteamDrill extends WandaModBase {

	@Override
	protected String getModID() {
		return "WandaSteamDrill";
	}

	@Init
	@Override
	public void init(FMLInitializationEvent event) {
		super.init(event);
		FMLLog.info("Init " + getModID());
		int itemID = config.getOrCreateIntProperty("Steam Drill", "item", 6100)
				.getInt();
		config.save();
		registDrill(itemID + 0, EnumToolMaterial.IRON, 0, 15);
		registDrill(itemID + 1, EnumToolMaterial.EMERALD, 1, 15);
	}

	private void registDrill(int itemID, EnumToolMaterial iron, int coordRow,
			int coordColumn) {
		WandaSteamDrill drill = new WandaSteamDrill(itemID, coordRow,
				coordColumn, iron);
		String name = MaterialTable.getMaterialName(iron) + " Steam Drill";
		drill.setItemName(name);
		LanguageRegistry.addName(drill, name);
	}
}
