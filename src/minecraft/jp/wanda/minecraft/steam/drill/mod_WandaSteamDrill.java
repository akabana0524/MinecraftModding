package jp.wanda.minecraft.steam.drill;

import java.util.EnumSet;

import jp.wanda.minecraft.MaterialTable;
import jp.wanda.minecraft.WandaModBase;
import jp.wanda.minecraft.steam.WandaSteamSmallEngine;
import net.minecraft.client.Minecraft;
import net.minecraft.src.EnumToolMaterial;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.KeyBinding;
import net.minecraftforge.client.MinecraftForgeClient;
import WandaResource.WandaKeyRegistry.WandaKey;
import WandaResource.WandaKeyRegistry.WandaKeyListener;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.asm.SideOnly;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLStateEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

@Mod(modid = "WandaSteamDrill", name = "Wanda Steam Drill", version = "0.0.1", dependencies = "required-after:WandaSteamCore")
@NetworkMod(clientSideRequired = true, serverSideRequired = false)
public class mod_WandaSteamDrill extends WandaModBase implements
		WandaKeyListener {

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
		int maxFuelValue = config.getOrCreateIntProperty(
				"Steam Drill Max Fuel", "general", 20 * 600).getInt();
		maxFuelValue = 150;
		config.save();
		registDrill(event, itemID + 0, EnumToolMaterial.IRON, 0, 15,
				maxFuelValue);
		registDrill(event, itemID + 1, EnumToolMaterial.EMERALD, 1, 15,
				maxFuelValue * 3);
	}

	private void registDrill(FMLStateEvent event, int itemID,
			EnumToolMaterial material, int coordRow, int coordColumn,
			int maxFuelValue) {
		WandaSteamDrill drill = new WandaSteamDrill(itemID, coordRow,
				coordColumn, material, maxFuelValue);
		String name = MaterialTable.getMaterialName(material) + " Steam Drill";
		drill.setItemName(name);
		LanguageRegistry.addName(drill, name);
		GameRegistry
				.addRecipe(
						new ItemStack(drill, 1, 0),
						new Object[] {
								" X ",
								"XXX",
								" Y ",
								'X',
								MaterialTable.getMaterial(material),
								'Y',
								Item.itemsList[WandaSteamSmallEngine.grobalShiftedIndex] });
		if (event.getSide() == Side.CLIENT) {
			MinecraftForgeClient.registerItemRenderer(drill.shiftedIndex,
					new WandaSteamMachineRenderer(Minecraft.getMinecraft()));
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void keyDown(WandaKey wandaKey, EnumSet<TickType> types,
			KeyBinding kb, boolean tickEnd, boolean isRepeat) {
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void keyUp(WandaKey wandaKey, EnumSet<TickType> types,
			KeyBinding kb, boolean tickEnd) {
	}
}
