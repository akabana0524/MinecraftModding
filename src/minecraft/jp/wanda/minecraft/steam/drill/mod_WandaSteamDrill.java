package jp.wanda.minecraft.steam.drill;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.EnumSet;

import jp.wanda.minecraft.core.MaterialTable;
import jp.wanda.minecraft.core.WandaKeyRegistry.WandaKey;
import jp.wanda.minecraft.core.WandaKeyRegistry.WandaKeyListener;
import jp.wanda.minecraft.core.WandaModBase;
import jp.wanda.minecraft.core.packet.WandaPacketHandlerRegistry;
import jp.wanda.minecraft.core.packet.WandaPacketHandlerRegistry.WandaPacektHandler;
import jp.wanda.minecraft.steam.WandaSteamSmallEngine;
import net.minecraft.client.Minecraft;
import net.minecraft.src.Block;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.EnumToolMaterial;
import net.minecraft.src.INetworkManager;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.KeyBinding;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.Event;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.asm.SideOnly;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLStateEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

@Mod(modid = "WandaSteamDrill", name = "Wanda Steam Drill", version = "0.0.1", dependencies = "required-after:WandaSteamCore")
@NetworkMod(clientSideRequired = true, serverSideRequired = false)
public class mod_WandaSteamDrill extends WandaModBase implements
		WandaKeyListener {

	public static mod_WandaSteamDrill instance;

	public static class NetworkHandler implements WandaPacektHandler {
		@Override
		public void onPacketData(String subChannel, byte[] data,
				INetworkManager manager, Packet250CustomPayload origin,
				Player player) {
			if (player instanceof EntityPlayerMP) {
				EntityPlayerMP mp = (EntityPlayerMP) player;
				World par2World = mp.worldObj;
				DataInputStream din = new DataInputStream(
						new ByteArrayInputStream(data));
				int x;
				int y;
				int z;
				int orientation;
				try {
					x = din.readInt();
					y = din.readInt();
					z = din.readInt();
					orientation = din.readByte();
					if (orientation == 0 || orientation == 1) {
						for (int i = 0; i < 3; i++) {
							for (int j = 0; j < 3; j++) {
								if (i == 1 && j == 1) {
									continue;
								}
								breakBlock(par2World, mp, x - 1 + j, y, z - 1
										+ i, orientation);
							}
						}

					} else if (orientation == 2 || orientation == 3) {
						for (int i = 0; i < 3; i++) {
							for (int j = 0; j < 3; j++) {
								if (i == 1 && j == 1) {
									continue;
								}
								breakBlock(par2World, mp, x - 1 + j, y - 1 + i,
										z, orientation);
							}
						}
					} else if (orientation == 4 || orientation == 5) {
						for (int i = 0; i < 3; i++) {
							for (int j = 0; j < 3; j++) {
								if (i == 1 && j == 1) {
									continue;
								}
								breakBlock(par2World, mp, x, y - 1 + i, z - 1
										+ j, orientation);
							}
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					if (din != null) {
						try {
							din.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

	public static class EventHook {
		@ForgeSubscribe
		public void recieveEvent(PlayerInteractEvent event) {
			FMLLog.info("PlayerInteractEvent");
		}

	}

	private static void breakBlock(World world, EntityPlayer player, int x,
			int y, int z, int orientation) {
		int blockID = world.getBlockId(x, y, z);
		int metaData = world.getBlockMetadata(x, y, z);
		Block block = Block.blocksList[blockID];
		if (block == null) {
			return;
		}
		world.playAuxSFX(2001, x, y, z, blockID + (metaData << 12));
		block.harvestBlock(world, player, x, y, z, metaData);
		world.setBlockWithNotify(x, y, z, 0);
	}

	@Override
	protected String getModID() {
		return "WandaSteamDrill";
	}

	public mod_WandaSteamDrill() {
		instance = this;
	}

	@Init
	@Override
	public void init(FMLInitializationEvent event) {
		super.init(event);
		FMLLog.info("Init " + getModID());
		MinecraftForge.EVENT_BUS.register(new EventHook());
		WandaPacketHandlerRegistry.registerPacketChannel(getModID(),
				new NetworkHandler());
		int itemID = config.getItem("SteamDrill", 6100).getInt();
		int maxFuelValue = config.get("SteamDrillMaxFuel", "general", 20 * 600)
				.getInt();
		// maxFuelValue = 20;
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
