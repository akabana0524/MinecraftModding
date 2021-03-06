package mods.WandaCore.packet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

public class WandaPacketHandlerRegistry implements IPacketHandler {
	public static final String CHANNEL = "WandaGeneral";

	public interface WandaPacektHandler {
		void onPacketData(String subChannel, byte[] data,
				INetworkManager manager, Packet250CustomPayload origin,
				Player player);
	}

	public static Packet250CustomPayload createWandaPacket(String subChannel,
			byte[] data, boolean isChunkDataPacket) {
		try {
			Packet250CustomPayload ret = new Packet250CustomPayload();
			byte[] packetData = null;
			ret.channel = CHANNEL;
			byte[] subChannelData = subChannel.getBytes();
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(bout);
			dos.writeUTF(subChannel);
			if (data != null) {
				dos.writeShort(data.length);
				dos.write(data);
			} else {
				dos.writeShort(0);
			}
			packetData = bout.toByteArray();

			ret.length = packetData.length;
			ret.isChunkDataPacket = isChunkDataPacket;
			ret.data = packetData;
			return ret;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public WandaPacketHandlerRegistry() {
	}

	private static Map<String, List<WandaPacektHandler>> registedMap;
	static {
		registedMap = new HashMap<String, List<WandaPacektHandler>>();
	}

	public static void registerPacketChannel(String channel,
			WandaPacektHandler packetHandler) {
		if (!registedMap.containsKey(channel)) {
			registedMap.put(channel, new ArrayList<WandaPacektHandler>());
		}
		registedMap.get(channel).add(packetHandler);

	}

	@Override
	public void onPacketData(INetworkManager manager,
			Packet250CustomPayload packet, Player player) {
		FMLLog.info("WandaCore:onPacketData:" + packet.channel);
		if (packet.channel.equals(CHANNEL)) {
			ByteArrayInputStream bin = new ByteArrayInputStream(packet.data);
			DataInputStream din = new DataInputStream(bin);
			try {
				String subChannel = din.readUTF();
				byte[] data = new byte[din.readShort()];
				din.readFully(data);
				if (registedMap.containsKey(subChannel)) {
					List<WandaPacektHandler> list = registedMap.get(subChannel);
					for (WandaPacektHandler handler : list) {
						handler.onPacketData(subChannel, data, manager, packet,
								player);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
