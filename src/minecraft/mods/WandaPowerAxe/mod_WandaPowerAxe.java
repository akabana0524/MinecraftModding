package mods.WandaPowerAxe;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mods.WandaCore.MaterialTable;
import mods.WandaCore.WandaModBase;
import mods.WandaCore.packet.WandaPacketHandlerRegistry;
import mods.WandaCore.packet.WandaPacketHandlerRegistry.WandaPacektHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.src.MLProp;
import net.minecraftforge.common.Property;
import WandaResource.WandaKeyRegistry;
import WandaResource.WandaKeyRegistry.WandaKey;
import WandaResource.WandaKeyRegistry.WandaKeyListener;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

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
				INetworkManager manager, Packet250CustomPayload origin,
				Player player) {
			if (player instanceof EntityPlayerMP) {
				EntityPlayerMP new_name = (EntityPlayerMP) player;
				Property propertyEnable = instance.getEnable(new_name.username);
				boolean enable = propertyEnable.getBoolean(true);
				propertyEnable.set(!enable);
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
		Property propertyItemID = config.get("general", "ItemID", 5000);
		Property propertyEnableEffect = config.get("general", "EnableEffect",
				true);
		Property propertyTarget = config.get("general", "TargetBlockID", "");
		Property propertyHorizontalRange = config.get("general",
				"HorizontalRange", 10);
		Property propertyVerticalRange = config.get("general", "VerticalRange",
				50);
		Property propertyDamageRatio = config.get("general", "DamageRatio",
				"0.2");
		Property propertyExhaustionRatio = config.get("general",
				"ExhaustionRatio", "0.03125");

		config.save();
		damageRatio = Double.parseDouble(propertyDamageRatio.getString());
		exhaustionRatio = Double.parseDouble(propertyExhaustionRatio
				.getString());
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
			temp.setTargetBlockList(propertyTarget.getString());
			temp.setUnlocalizedName(MaterialTable.getMaterialName(mat)
					+ "PowerAxe");
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
		Property propertyEnable = config.get("Enable", username, true);
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
