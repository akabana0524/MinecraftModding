package jp.wanda.minecraft.powerpickaxe;

import java.io.File;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import WandaResource.WandaKeyRegistry;
import WandaResource.WandaKeyRegistry.WandaKey;
import WandaResource.WandaKeyRegistry.WandaKeyListener;

import jp.wanda.minecraft.MaterialTable;
import jp.wanda.minecraft.WandaModBase;
import jp.wanda.minecraft.poweraxe.mod_WandaPowerAxe;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.EnumToolMaterial;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.KeyBinding;
import net.minecraft.src.MLProp;
import net.minecraft.src.NetHandler;
import net.minecraft.src.NetLoginHandler;
import net.minecraft.src.NetworkManager;
import net.minecraft.src.Packet;
import net.minecraft.src.Packet1Login;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.asm.SideOnly;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.network.IConnectionHandler;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

@Mod(modid = "WandaPowerPickaxe", name = "Wanda Power Pickaxe", version = "0.4.0", dependencies = "required-after:WandaResource")
@NetworkMod(clientSideRequired = true, serverSideRequired = false, channels = { "WandaPwrPckax" }, packetHandler = mod_WandaPowerPickaxe.NetworkHandler.class, connectionHandler = mod_WandaPowerPickaxe.NetworkHandler.class, versionBounds = "[0.4.0]")
public class mod_WandaPowerPickaxe extends WandaModBase implements
		WandaKeyListener {

	public static class NetworkHandler implements IPacketHandler,
			IConnectionHandler {
		@Override
		public void onPacketData(NetworkManager manager,
				Packet250CustomPayload packet, Player player) {
			if (player instanceof EntityPlayerMP) {
				EntityPlayerMP new_name = (EntityPlayerMP) player;
				Property propertyEnable = instance.getEnable(new_name.username);
				boolean enable = propertyEnable.getBoolean(true);
				propertyEnable.value = "" + (!enable);
				new_name.sendChatToPlayer(instance.getModID()
						+ " "
						+ (propertyEnable.getBoolean(true) ? "Enabled"
								: "Disabled"));
				instance.config.save();
			}
		}

		@Override
		public void playerLoggedIn(Player player, NetHandler netHandler,
				NetworkManager manager) {
		}

		@Override
		public String connectionReceived(NetLoginHandler netHandler,
				NetworkManager manager) {
			return null;
		}

		@Override
		public void connectionOpened(NetHandler netClientHandler,
				String server, int port, NetworkManager manager) {
		}

		@Override
		public void connectionOpened(NetHandler netClientHandler,
				MinecraftServer server, NetworkManager manager) {
		}

		@Override
		public void connectionClosed(NetworkManager manager) {
		}

		@Override
		public void clientLoggedIn(NetHandler clientHandler,
				NetworkManager manager, Packet1Login login) {
		}
	}

	public static Map<EnumToolMaterial, PowerPickaxe> list;
	private static mod_WandaPowerPickaxe instance;
	private static Map<String, Property> enable;
	private List<Integer> itemIDList;

	private double damageRatio;
	private double exhaustionRatio;

	@Init
	public void init(FMLInitializationEvent event) {
		super.init(event);
		FMLLog.log(Level.INFO, "Init " + getModID());
		instance = this;
		if (event.getSide() == Side.CLIENT) {
			WandaKeyRegistry.instance.registerWandaKeyListener(WandaKey.TOGGLE, this);
		}
		Property propertyItemID = config.getOrCreateIntProperty("ItemID",
				"general", 5030);
		Property propertyTarget = config.getOrCreateProperty("TargetBlockID",
				"general", "");
		Property propertyEnableOre = config.getOrCreateBooleanProperty(
				"EnableOre", "general", true);
		Property propertyEnableRedstone = config.getOrCreateBooleanProperty(
				"EnableRedstone", "general", true);
		Property propertyEnableGrowstone = config.getOrCreateBooleanProperty(
				"EnableGrowstone", "general", true);
		Property propertyMaxRange = config.getOrCreateProperty("MaxRange",
				"general", "10.0");
		Property propertyEnableEffect = config.getOrCreateBooleanProperty(
				"EnableEffect", "general", true);
		Property propertyDamageRatio = config.getOrCreateProperty(
				"DamageRatio", "general", "1");
		Property propertyExhaustionRatio = config.getOrCreateProperty(
				"ExhaustionRatio", "general", "0.05");

		int itemID = propertyItemID.getInt();
		int iconX = 0;
		config.save();

		list = new HashMap<EnumToolMaterial, PowerPickaxe>();
		itemIDList = new ArrayList<Integer>();
		enable = new HashMap<String, Property>();
		damageRatio = Double.parseDouble(propertyDamageRatio.value);
		exhaustionRatio = Double.parseDouble(propertyExhaustionRatio.value);

		Map<EnumToolMaterial, Item> normalItemMap = new HashMap<EnumToolMaterial, Item>() {
			{
				put(EnumToolMaterial.WOOD, Item.pickaxeWood);
				put(EnumToolMaterial.STONE, Item.pickaxeStone);
				put(EnumToolMaterial.IRON, Item.pickaxeSteel);
				put(EnumToolMaterial.EMERALD, Item.pickaxeDiamond);
				put(EnumToolMaterial.GOLD, Item.pickaxeGold);
			}
		};
		for (EnumToolMaterial mat : EnumToolMaterial.values()) {
			Object materialObject = null;
			try {
				materialObject = MaterialTable.getMaterial(mat);
			} catch (Exception e) {
				System.out.println(getModID() + " not support " + mat);
				continue;
			}
			PowerPickaxe temp = new PowerPickaxe(itemID - 256, iconX, mat, this);
			temp.setEnableGrowstone(propertyEnableGrowstone.getBoolean(true));
			temp.setEnableRedstoneOre(propertyEnableRedstone.getBoolean(true));
			temp.setEnableOre(propertyEnableOre.getBoolean(true));
			temp.setTargetBlockList(propertyTarget.value);
			temp.setMaxRange(propertyMaxRange.value);
			temp.setEnableEffect(propertyEnableEffect.getBoolean(true));
			temp.setItemName(MaterialTable.getMaterialName(mat)
					+ " Power Pickaxe");
			LanguageRegistry.addName(temp, MaterialTable.getMaterialName(mat)
					+ " Power Pickaxe");
			GameRegistry.addRecipe(new ItemStack(temp), new Object[] { "XXX",
					" Y ", " Z ", 'X', materialObject, 'Y', Item.stick, 'Z',
					Item.leather });
			GameRegistry.addRecipe(new ItemStack(temp), new Object[] { "X",
					"Y", 'X', normalItemMap.get(mat), 'Y', Item.leather });
			list.put(mat, temp);
			itemIDList.add(itemID);
			itemID++;
			iconX++;
		}
	}

	@Override
	protected String getModID() {
		return "WandaPowerPickaxe";
	}

	public static Packet getPacket() {
		Packet250CustomPayload pkt = new Packet250CustomPayload();
		pkt.channel = "WandaPwrPckax";
		pkt.length = 0;
		pkt.isChunkDataPacket = false;
		return pkt;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void keyDown(WandaKey wandaKey, EnumSet<TickType> types,
			KeyBinding kb, boolean tickEnd, boolean isRepeat) {
		if (!tickEnd) {
			Minecraft mc = Minecraft.getMinecraft();
			if (mc.currentScreen == null) {
				ItemStack item = mc.thePlayer.getCurrentEquippedItem();
				if (item != null && instance.itemIDList.contains(item.itemID)) {
					FMLClientHandler.instance().sendPacket(getPacket());
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void keyUp(WandaKey wandaKey, EnumSet<TickType> types,
			KeyBinding kb, boolean tickEnd) {
	}

	private Property getEnable(String username) {
		Property propertyEnable = config.getOrCreateBooleanProperty(username,
				"Enable", true);
		return propertyEnable;
	}

	public boolean isEnable(String username) {
		return getEnable(username).getBoolean(true);
	}

	public double getDamageRatio() {
		return damageRatio;
	}

	public double getExhaustionRatio() {
		return exhaustionRatio;
	}
}
