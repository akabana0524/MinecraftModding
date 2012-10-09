package jp.wanda.minecraft.core;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.src.NetworkManager;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import samples.tileentitysamplemod.TileEntitySampleMod;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

public class WandaBasePacketHandler implements IPacketHandler {

	public enum PacketType {
		TILE_ENTITY,
	}

	private static Map<String, PacketType> REGISTED_MAP;
	static {
		REGISTED_MAP = new HashMap<String, WandaBasePacketHandler.PacketType>();
	}

	public static void registerTileEntityPacketChannel(String channel,
			PacketType packetType) {
		REGISTED_MAP.put(channel, packetType);
	}

	@Override
	public void onPacketData(NetworkManager manager,
			Packet250CustomPayload packet, Player player) {
		if (REGISTED_MAP.containsKey(packet.channel)) {
			switch (REGISTED_MAP.get(packet.channel)) {
			case TILE_ENTITY: {
				ByteArrayDataInput data = ByteStreams.newDataInput(packet.data);
				int x, y, z;
				byte facing;
				try {
					x = data.readInt();
					y = data.readInt();
					z = data.readInt();
					int version = data.readInt();
					int stackNum = data.readInt();
					int dataLength = data.readInt();
					byte[] auxillaryInfo = new byte[dataLength];
					data.readFully(auxillaryInfo);

					World world = TileEntitySampleMod.proxy.getClientWorld();
					TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

					if (tileEntity instanceof WandaTileEntityBase) {
						WandaTileEntityBase tileEntityshelf = (WandaTileEntityBase) tileEntity;
						tileEntityshelf
								.setAuxillaryInfoPacketData(new DataInputStream(
										new ByteArrayInputStream(auxillaryInfo)));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
				break;
			}
		}

	}

}
