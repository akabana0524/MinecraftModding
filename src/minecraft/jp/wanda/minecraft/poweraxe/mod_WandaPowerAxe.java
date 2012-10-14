package jp.wanda.minecraft.poweraxe;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.wanda.minecraft.MaterialTable;
import jp.wanda.minecraft.WandaModBase;
import jp.wanda.minecraft.core.packet.WandaPacketHandlerRegistry;
import jp.wanda.minecraft.core.packet.WandaPacketHandlerRegistry.WandaPacektHandler;
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
import net.minecraftforge.common.Property;
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
import cpw.mods.fml.common.network.IConnectionHandler;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

@Mod(modid = "WandaPowerAxe", name = "Wanda Power Axe", version = "0.4.0", dependencies = "required-after:WandaResource")
@NetworkMod(clientSideRequired = true, serverSideRequired = false, versionBounds = "[0.4.0]")
public class mod_WandaPowerAxe extends WandaModBase implements WandaKeyListener {

	public static Map<EnumToolMaterial, PowerAxe> axe;
	private static mod_WandaPowerAxe instance;

	private static Map<String, Property> enable;
	@MLProp(name = "ItemID", info = "assign an ItemID", min = 4096, max = 32000)
	public static int BaseItemID = 5000;

	private double damageRatio;
	private List<Integer> axeItemIDList;
	private double exhaustionRatio;

	public static class NetworkHandler implements WandaPacektHandler {

		@Override
		public void onPacketData(String subChannel, byte[] data,
				NetworkManager manager, Packet250CustomPayload origin,
				Player player) {
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
	}

	@Init
	public void init(FMLInitializationEvent event) {
		super.init(event);
		WandaPacketHandlerRegistry.registerPacketChannel(getModID(),
				new NetworkHandler());
		instance = this;
		FMLLog.info("Init WandaPowerAxe");
		if (event.getSide() == Side.CLIENT) {
			WandaKeyRegistry.registerWandaKeyListener(WandaKey.TOGGLE, this);
		}
		Property propertyItemID = config.getOrCreateIntProperty("ItemID",
				"general", 5000);
		Property propertyEnableEffect = config.getOrCreateBooleanProperty(
				"EnableEffect", "general", true);
		Property propertyTarget = config.getOrCreateProperty("TargetBlockID",
				"general", "");
		Property propertyHorizontalRange = config.getOrCreateIntProperty(
				"HorizontalRange", "general", 10);
		Property propertyVerticalRange = config.getOrCreateIntProperty(
				"VerticalRange", "general", 50);
		Property propertyDamageRatio = config.getOrCreateProperty(
				"DamageRatio", "general", "0.2");
		Property propertyExhaustionRatio = config.getOrCreateProperty(
				"ExhaustionRatio", "general", "0.03125");

		config.save();
		damageRatio = Double.parseDouble(propertyDamageRatio.value);
		exhaustionRatio = Double.parseDouble(propertyExhaustionRatio.value);
		axe = new HashMap<EnumToolMaterial, PowerAxe>();
		enable = new HashMap<String, Property>();
		axeItemIDList = new ArrayList<Integer>();
		int itemID = propertyItemID.getInt();
		int iconX = 0;
		Map<EnumToolMaterial, Item> normalItemMap = new HashMap<EnumToolMaterial, Item>() {
			{
				put(EnumToolMaterial.WOOD, Item.axeWood);
				put(EnumToolMaterial.STONE, Item.axeStone);
				put(EnumToolMaterial.IRON, Item.axeSteel);
				put(EnumToolMaterial.EMERALD, Item.axeDiamond);
				put(EnumToolMaterial.GOLD, Item.axeGold);
			}
		};
		for (EnumToolMaterial mat : EnumToolMaterial.values()) {
			Object materialObject = null;
			try {
				materialObject = MaterialTable.getMaterial(mat);
			} catch (Exception e) {
				System.out.println("WandaPowerAxe not support " + mat);
				continue;
			}
			PowerAxe temp = new PowerAxe(itemID - 256,
					16 * 1 + MaterialTable.getTextureU(mat), mat, this);
			temp.setEnableEffect(propertyEnableEffect.getBoolean(true));
			temp.setHorizontalRange(propertyHorizontalRange.getInt());
			temp.setVerticalRange(propertyVerticalRange.getInt());
			temp.setTargetBlockList(propertyTarget.value);
			temp.setItemName(MaterialTable.getMaterialName(mat) + " Power Axe");
			LanguageRegistry.addName(temp, MaterialTable.getMaterialName(mat)
					+ " Power Axe");
			GameRegistry.addRecipe(new ItemStack(temp), new Object[] { "XX",
					"XY", " Z", 'X', materialObject, 'Y', Item.stick, 'Z',
					Item.leather });
			GameRegistry.addRecipe(new ItemStack(temp), new Object[] { "X",
					"Y", 'X', normalItemMap.get(mat), 'Y', Item.leather });
			axe.put(mat, temp);
			axeItemIDList.add(itemID);
			itemID++;
			iconX++;
		}

	}

	@Override
	protected String getModID() {
		return "WandaPowerAxe";
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void keyDown(WandaKey wandaKey, EnumSet<TickType> types,
			KeyBinding kb, boolean tickEnd, boolean isRepeat) {
		if (!tickEnd) {
			Minecraft mc = Minecraft.getMinecraft();
			if (mc.currentScreen == null) {
				ItemStack item = mc.thePlayer.getCurrentEquippedItem();
				if (item != null
						&& instance.axeItemIDList.contains(item.itemID)) {
					FMLClientHandler.instance().sendPacket(
							WandaPacketHandlerRegistry.createWandaPacket(
									getModID(), null, false));
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
