package mods.WandaSteamCore;

import java.util.EnumSet;

import mods.WandaCore.WandaKeyRegistry;
import mods.WandaCore.WandaKeyRegistry.WandaKey;
import mods.WandaCore.WandaKeyRegistry.WandaKeyListener;
import mods.WandaCore.WandaModBase;
import mods.WandaCore.packet.WandaPacketHandlerRegistry;
import mods.WandaCore.packet.WandaPacketHandlerRegistry.WandaPacektHandler;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Mod(modid = "WandaSteamCore", name = "Wanda Steam Core", version = "0.0.1", dependencies = "required-after:WandaCore")
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
						if (inv.hasItem(WandaSteamFuel.globalItemID)) {
							ItemStack itemStack = entityPlayer
									.getCurrentEquippedItem();
							if (itemStack != null) {
								Item item = itemStack.getItem();
								if (item instanceof WandaSteamFuelMachine) {
									WandaSteamFuel.addFuel(itemStack);
									inv.consumeInventoryItem(WandaSteamFuel.globalItemID);
								}
							}
						}

					}
				});
		int fuelID = config.get("item", "Steam Fuel", 6000).getInt();
		int smallEngineID = config.get("item", "Steam Engine small", 6001)
				.getInt();
		int fuelGeneratorID = config.get("block", "Steam Fuel Generator", 1100)
				.getInt();
		int fuelEnergy = config.get("general", "Steam Fuel Energy", 20 * 600)
				.getInt();
		config.save();

		WandaSteamFuel fuel = new WandaSteamFuel(fuelID);
		WandaSteamFuel.fuelGainEnergy = fuelEnergy;
		fuel.setUnlocalizedName("SteamFuel");
		GameRegistry.registerFuelHandler(fuel);
		LanguageRegistry.addName(fuel, "Steam Fuel");
		GameRegistry.addShapelessRecipe(new ItemStack(fuel), new Object[] {
				new ItemStack(Item.gunpowder, 1), new ItemStack(Item.coal, 1),
				new ItemStack(Item.bucketWater, 1),
				new ItemStack(Item.bucketWater, 1), });

		WandaSteamFuelGenerator generator = new WandaSteamFuelGenerator(
				fuelGeneratorID, Material.rock, this, GUI_GENERATOR);
		generator.setUnlocalizedName("SteamFuelGenerator");
		GameRegistry.registerBlock(generator, generator.getUnlocalizedName());
		LanguageRegistry.addName(generator, "Steam Fuel Generator");
		GameRegistry.addRecipe(new ItemStack(generator), new Object[] { "XYX",
				"YZY", "XWX", 'X', Item.ingotIron, 'Y', Block.pistonBase, 'Z',
				Block.furnaceIdle, 'W', Item.redstone });
		NetworkRegistry.instance().registerGuiHandler(this,
				WandaSteamFuelGenerator.GUI);
		GameRegistry.registerTileEntity(
				WandaSteamFuelGenerator.GeneratorTileEntity.class,
				"SteamFuelGenerator");

		WandaSteamSmallEngine smallEngine = new WandaSteamSmallEngine(
				smallEngineID);
		smallEngine.setUnlocalizedName("SteamEngineSmall");
		LanguageRegistry.addName(smallEngine, "Steam Engine(small)");
		GameRegistry.addRecipe(new ItemStack(smallEngine), new Object[] {
				"XXX", "YZW", 'X', Item.ingotIron, 'Y', Item.redstone, 'Z',
				Block.furnaceIdle, 'W', Item.slimeBall });
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
