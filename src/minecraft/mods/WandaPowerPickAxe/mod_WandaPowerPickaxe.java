package mods.WandaPowerPickAxe;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import mods.WandaCore.MaterialTable;
import mods.WandaCore.WandaKeyRegistry;
import mods.WandaCore.WandaKeyRegistry.WandaKey;
import mods.WandaCore.WandaKeyRegistry.WandaKeyListener;
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
import net.minecraftforge.common.Property;
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

@Mod(modid = "WandaPowerPickaxe", name = "Wanda Power Pickaxe", version = "0.4.0", dependencies = "required-after:WandaCore")
@NetworkMod(clientSideRequired = true, serverSideRequired = false, versionBounds = "[0.4.0]")
public class mod_WandaPowerPickaxe extends WandaModBase implements
		WandaKeyListener {

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
		WandaPacketHandlerRegistry.registerPacketChannel(getModID(),
				new NetworkHandler());
		instance = this;
		if (event.getSide() == Side.CLIENT) {
			WandaKeyRegistry.registerWandaKeyListener(WandaKey.TOGGLE, this);
		}
		Property propertyItemID = config.get("general", "ItemID", 5030);
		Property propertyTarget = config.get("general", "TargetBlockID", "");
		Property propertyEnableOre = config.get("general", "EnableOre", true);
		Property propertyEnableRedstone = config.get("general",
				"EnableRedstone", true);
		Property propertyEnableGrowstone = config.get("general",
				"EnableGrowstone", true);
		Property propertyMaxRange = config.get("general", "MaxRange", "10.0");
		Property propertyEnableEffect = config.get("general", "EnableEffect",
				true);
		Property propertyDamageRatio = config
				.get("general", "DamageRatio", "1");
		Property propertyExhaustionRatio = config.get("ExhaustionRatio",
				"general", "0.05");

		int itemID = propertyItemID.getInt();
		int iconX = 0;
		config.save();

		list = new HashMap<EnumToolMaterial, PowerPickaxe>();
		itemIDList = new ArrayList<Integer>();
		enable = new HashMap<String, Property>();
		damageRatio = Double.parseDouble(propertyDamageRatio.getString());
		exhaustionRatio = Double.parseDouble(propertyExhaustionRatio
				.getString());

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
			temp.setTargetBlockList(propertyTarget.getString());
			temp.setMaxRange(propertyMaxRange.getString());
			temp.setEnableEffect(propertyEnableEffect.getBoolean(true));
			temp.setUnlocalizedName(MaterialTable.getMaterialName(mat)
					+ "PowerPickaxe");
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

	@SideOnly(Side.CLIENT)
	@Override
	public void keyDown(WandaKey wandaKey, EnumSet<TickType> types,
			KeyBinding kb, boolean tickEnd, boolean isRepeat) {
		if (!tickEnd) {
			Minecraft mc = Minecraft.getMinecraft();
			if (mc.currentScreen == null) {
				ItemStack item = mc.thePlayer.getCurrentEquippedItem();
				if (item != null && instance.itemIDList.contains(item.itemID)) {
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
