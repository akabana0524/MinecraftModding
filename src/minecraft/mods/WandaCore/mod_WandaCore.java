package mods.WandaCore;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.event.FMLInitializationEvent;

@Mod(modid = "WandaCore", name = "Wanda Core", version = "0.5.0")
public class mod_WandaCore {

	@Init
	public void init(FMLInitializationEvent event) {
		FMLLog.info("Init WandaCore");
	}

}
