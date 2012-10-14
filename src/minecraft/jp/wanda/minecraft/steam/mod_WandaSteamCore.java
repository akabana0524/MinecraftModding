package jp.wanda.minecraft.steam;

import jp.wanda.minecraft.WandaModBase;
import net.minecraft.src.Block;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Material;
import net.minecraftforge.client.MinecraftForgeClient;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

@Mod(modid = "WandaSteamCore", name = "Wanda Steam Core", version = "0.0.1", dependencies = "required-after:WandaResource")
@NetworkMod(clientSideRequired = true, serverSideRequired = false)
public class mod_WandaSteamCore extends WandaModBase {

	public static final String TEXTURE = "/jp/wanda/minecraft/steam/WandaSteamCore.png";
	public static final int GUI_GENERATOR = 1;

	@Override
	protected String getModID() {
		return "WandaSteamCore";
	}

	@Init
	@Override
	public void init(FMLInitializationEvent event) {
		super.init(event);
		FMLLog.info("Init " + getModID());
		if (event.getSide() == Side.CLIENT) {
			MinecraftForgeClient.preloadTexture(TEXTURE);
			MinecraftForgeClient
					.preloadTexture(WandaSteamFuelGenerator.GUI_TEXTURE);
		}
		int fuelID = config.getOrCreateIntProperty("Steam Fuel", "item", 6000)
				.getInt();
		int smallEngineID = config.getOrCreateIntProperty("Steam Engne small",
				"item", 6001).getInt();
		int fuelGeneratorID = config.getOrCreateIntProperty(
				"Steam Fuel Generator", "block", 160).getInt();
		config.save();

		WandaSteamFuel fuel = new WandaSteamFuel(fuelID);
		fuel.setItemName("Steam Fuel");
		GameRegistry.registerFuelHandler(fuel);
		LanguageRegistry.addName(fuel, "Steam Fuel");
		GameRegistry.addShapelessRecipe(new ItemStack(fuel), new Object[] {
				new ItemStack(Item.gunpowder, 1), new ItemStack(Item.coal, 1),
				new ItemStack(Item.bucketWater, 1),
				new ItemStack(Item.bucketWater, 1), });

		WandaSteamFuelGenerator generator = new WandaSteamFuelGenerator(
				fuelGeneratorID, Material.rock, this, GUI_GENERATOR);
		generator.setBlockName("Steam Fuel Generator");
		GameRegistry.registerBlock(generator);
		LanguageRegistry.addName(generator, "Steam Fuel Generator");
		GameRegistry.addRecipe(new ItemStack(generator), new Object[] { "XYX",
				"YZY", "XWX", 'X', Item.ingotIron, 'Y', Block.pistonBase, 'Z',
				Block.stoneOvenIdle, 'W', Item.redstone });
		NetworkRegistry.instance().registerGuiHandler(this,
				WandaSteamFuelGenerator.GUI);
		GameRegistry.registerTileEntity(
				WandaSteamFuelGenerator.GeneratorTileEntity.class,
				"SteamFuelGenerator");

		WandaSteamSmallEngine smallEngine = new WandaSteamSmallEngine(
				smallEngineID);
		smallEngine.setItemName("Steam Engine(small)");
		LanguageRegistry.addName(smallEngine, "Steam Engine(small)");
		GameRegistry.addRecipe(new ItemStack(smallEngine), new Object[] {
				"XXX", "YZW", 'X', Item.ingotIron, 'Y', Item.redstone, 'Z',
				Block.stoneOvenIdle, 'W', Item.slimeBall });

	}
}
