package jp.wanda.minecraft.core;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.src.NetworkManager;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
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

					List<byte[]> tileEntityDataList = new ArrayList<byte[]>();
					int tileEntityDataNum = data.readInt();
					for (int i = 0; i < tileEntityDataNum; i++) {
						int binaryLength = data.readInt();
						byte[] temp = new byte[binaryLength];
						data.readFully(temp);
						tileEntityDataList.add(temp);
					}
					World world = getWorld();
					TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

					if (tileEntity instanceof WandaTileEntityBase) {
						WandaTileEntityBase wandaTileEntityBase = (WandaTileEntityBase) tileEntity;
						wandaTileEntityBase
								.setTileEntityData(tileEntityDataList);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
				break;
			}
		}

	}

	@SideOnly(Side.CLIENT)
	private World getWorld() {
		return Minecraft.getMinecraft().theWorld;
	}

}
