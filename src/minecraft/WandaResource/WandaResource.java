package WandaResource;

import jp.wanda.minecraft.core.WandaBasePacketHandler;
import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;

@Mod(modid = "WandaResource", name = "Wanda Resource", version = "0.3.0")
@NetworkMod(clientSideRequired = true, serverSideRequired = true, channels = { "WandaSteamFG" }, packetHandler = WandaBasePacketHandler.class)
public class WandaResource {
	@SidedProxy(clientSide = "WandaResource.Client", serverSide = "WandaResource.Server")
	public static Proxy proxy;

	@Init
	public void init(FMLInitializationEvent event) {
		FMLLog.info("Init WandaResource");

		if (event.getSide() == Side.CLIENT) {
			KeyBindingRegistry.registerKeyBinding(new WandaKeyHandler());
		}
	}

}
