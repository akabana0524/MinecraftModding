package mods.WandaCore;

import java.io.File;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.Configuration;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.relauncher.Side;

public abstract class WandaModBase {
	protected Configuration config;

	public WandaModBase() {
	}

	public void init(FMLInitializationEvent event) {
		File file = null;
		if (event.getSide() == Side.CLIENT) {
			file = new File(Minecraft.getMinecraftDir(), "config/WandaMod/"
					+ getModID() + ".cfg");
		} else {
			file = new File("config/WandaMod/" + getModID() + ".cfg");
		}
		config = new Configuration(file);
		config.load();
	}

	public void preInit(FMLPreInitializationEvent evt) {
	}

	public void postInit(FMLPostInitializationEvent evt) {
	}

	public void serverStarting(FMLServerStartingEvent event) {
	}

	public void serverStarted(FMLServerStartedEvent event) {
	}

	public void serverStopping(FMLServerStoppingEvent event) {
	}

	abstract protected String getModID();
}
