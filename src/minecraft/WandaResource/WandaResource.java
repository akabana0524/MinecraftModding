package WandaResource;

import java.util.ArrayList;
import java.util.List;

import mods.WandaCore.WandaTileEntityBase;
import mods.WandaCore.packet.WandaPacketHandlerRegistry;
import mods.WandaCore.packet.WandaPacketHandlerRegistry.WandaPacektHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.Player;

@Mod(modid = "WandaResource", name = "Wanda Resource", version = "0.3.0")
@NetworkMod(clientSideRequired = true, serverSideRequired = true, channels = { "WandaGeneral" }, packetHandler = WandaPacketHandlerRegistry.class)
public class WandaResource {
	@SidedProxy(clientSide = "WandaResource.Client", serverSide = "WandaResource.Server")
	public static Proxy proxy;

	@Init
	public void init(FMLInitializationEvent event) {
		FMLLog.info("Init WandaResource");
		WandaPacketHandlerRegistry.registerPacketChannel("WandaTileEntity",
				new WandaPacektHandler() {

					@Override
					public void onPacketData(String subChannel, byte[] data,
							INetworkManager manager,
							Packet250CustomPayload origin, Player player) {
						ByteArrayDataInput din = ByteStreams
								.newDataInput(data);
						int x, y, z;
						byte facing;
						try {
							x = din.readInt();
							y = din.readInt();
							z = din.readInt();
							int version = din.readInt();

							List<byte[]> tileEntityDataList = new ArrayList<byte[]>();
							int tileEntityDataNum = din.readInt();
							for (int i = 0; i < tileEntityDataNum; i++) {
								int binaryLength = din.readInt();
								byte[] temp = new byte[binaryLength];
								din.readFully(temp);
								tileEntityDataList.add(temp);
							}
							World world = ((EntityPlayer) player).worldObj;
							TileEntity tileEntity = world.getBlockTileEntity(x,
									y, z);

							if (tileEntity instanceof WandaTileEntityBase) {
								WandaTileEntityBase wandaTileEntityBase = (WandaTileEntityBase) tileEntity;
								wandaTileEntityBase
										.setTileEntityData(tileEntityDataList);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
	}
}
