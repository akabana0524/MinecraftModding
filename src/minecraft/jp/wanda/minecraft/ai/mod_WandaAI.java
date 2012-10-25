package jp.wanda.minecraft.ai;

import jp.wanda.minecraft.WandaModBase;
import jp.wanda.minecraft.steam.mod_WandaSteamCore;
import net.minecraft.src.BiomeGenBase;
import net.minecraft.src.EnumCreatureType;
import net.minecraft.src.Material;
import net.minecraft.src.ModelZombie;
import net.minecraft.src.RenderBiped;
import net.minecraftforge.client.MinecraftForgeClient;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

@Mod(modid = "WandaAI", name = "Wanda AI", version = "0.0.1", dependencies = "required-after:WandaResource")
@NetworkMod(clientSideRequired = true, serverSideRequired = false)
public class mod_WandaAI extends WandaModBase {

	public static int GUI_WandaAIWorkbench = 1;

	@Override
	protected String getModID() {
		return "WandaAI";
	}

	@Init
	public void init(FMLInitializationEvent event) {
		super.init(event);

		FMLLog.info("Init " + getModID());
		if (event.getSide() == Side.CLIENT) {
			initClient();
		}
		int blockAIWorkBench = config.getOrCreateIntProperty("AI WorkBench",
				"block", 180).getInt();
		config.save();
		WandaAIWorkbench workBench = new WandaAIWorkbench(this,
				GUI_WandaAIWorkbench, blockAIWorkBench, Material.rock);
		workBench.setBlockName("AI WorkBench");
		GameRegistry.registerBlock(workBench);
		LanguageRegistry.addName(workBench, "AI WorkBench");
		NetworkRegistry.instance().registerGuiHandler(this,
				WandaAIWorkbench.GUI);

		// Entityを登録
		EntityRegistry.registerGlobalEntityID(WandaAISampleMob.class,
				"AISampleMob", 240, 0xFF000000, 0xFFFFFF00);
		EntityRegistry.registerModEntity(WandaAISampleMob.class, "AISampleMob",
				0, this, 250, 5, true);
		// Entityの名前を登録
		LanguageRegistry.instance().addStringLocalization(
				"entity.AISampleMob.name", "en_US", "AISampleMob");
	}

	@SideOnly(Side.CLIENT)
	private void initClient() {
		MinecraftForgeClient.preloadTexture(mod_WandaSteamCore.TEXTURE);
		MinecraftForgeClient.preloadTexture(WandaAIWorkbench.GUI_TEXTURE);
		RenderingRegistry.registerEntityRenderingHandler(
				WandaAISampleMob.class,
				new RenderBiped(new ModelZombie(), 0.5F));

	}

}
