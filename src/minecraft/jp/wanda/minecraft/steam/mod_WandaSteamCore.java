package jp.wanda.minecraft.steam;

import java.util.EnumSet;

import jp.wanda.minecraft.WandaModBase;
import jp.wanda.minecraft.core.packet.WandaPacketHandlerRegistry;
import jp.wanda.minecraft.core.packet.WandaPacketHandlerRegistry.WandaPacektHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.src.Block;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.INetworkManager;
import net.minecraft.src.InventoryPlayer;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.KeyBinding;
import net.minecraft.src.Material;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraftforge.client.MinecraftForgeClient;

import org.lwjgl.input.Keyboard;

import WandaResource.WandaKeyRegistry;
import WandaResource.WandaKeyRegistry.WandaKey;
import WandaResource.WandaKeyRegistry.WandaKeyListener;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.asm.SideOnly;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

@Mod(modid = "WandaSteamCore", name = "Wanda Steam Core", version = "0.0.1", dependencies = "required-after:WandaResource")
@NetworkMod(clientSideRequired = true, serverSideRequired = false)
public class mod_WandaSteamCore extends WandaModBase implements
		WandaKeyListener {
	public static WandaKey FUEL_KEY = new WandaKey() {

		@Override
		public boolean isRepeat() {
			return false;
		}

		@Override
		public String getName() {
			return "Wanda Steam Fuel";
		}

		@Override
		public int getDefaultKeyboard() {
			return Keyboard.KEY_F;
		}
	};

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
			WandaKeyRegistry.registerWandaKeyListener(FUEL_KEY, this);
		}
		WandaPacketHandlerRegistry.registerPacketChannel("WandaSteamFuel",
				new WandaPacektHandler() {
					@Override
					public void onPacketData(String subChannel, byte[] data,
							INetworkManager manager,
							Packet250CustomPayload origin, Player player) {
						EntityPlayer entityPlayer = (EntityPlayer) player;
						InventoryPlayer inv = entityPlayer.inventory;
						if (inv.hasItem(WandaSteamFuel.globalShiftedIndex)) {
							ItemStack itemStack = entityPlayer
									.getCurrentEquippedItem();
							if (itemStack != null) {
								Item item = itemStack.getItem();
								if (item instanceof WandaSteamFuelMachine) {
									WandaSteamFuel.addFuel(itemStack);
									inv.consumeInventoryItem(WandaSteamFuel.globalShiftedIndex);
								}
							}
						}

					}
				});
		int fuelID = config.get("Steam Fuel", "item", 6000)
				.getInt();
		int smallEngineID = config.get("Steam Engine small",
				"item", 6001).getInt();
		int fuelGeneratorID = config.get(
				"Steam Fuel Generator", "block", 160).getInt();
		int fuelEnergy = config.get("Steam Fuel Energy",
				"general", 20 * 600).getInt();
		config.save();

		WandaSteamFuel fuel = new WandaSteamFuel(fuelID);
		WandaSteamFuel.fuelGainEnergy = fuelEnergy;
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

	@SideOnly(Side.CLIENT)
	@Override
	public void keyDown(WandaKey wandaKey, EnumSet<TickType> types,
			KeyBinding kb, boolean tickEnd, boolean isRepeat) {
		if (wandaKey == FUEL_KEY) {
			Minecraft mc = Minecraft.getMinecraft();
			if (mc.currentScreen == null) {
				if (!tickEnd) {
					FMLClientHandler.instance().sendPacket(
							WandaPacketHandlerRegistry.createWandaPacket(
									"WandaSteamFuel", null, false));
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void keyUp(WandaKey wandaKey, EnumSet<TickType> types,
			KeyBinding kb, boolean tickEnd) {
	}
}
