package mods.WandaCore;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.List;

import mods.WandaCore.packet.WandaPacketHandlerRegistry;
import mods.WandaCore.tileentity.WandaFacingData;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.tileentity.TileEntity;

abstract public class WandaTileEntityBase extends TileEntity {

	public static final String SUB_CHANNEL = "WandaTileEntity";
	protected int tileEntityVersion;
	protected List<WandaTileEntityData> tileEntityDataList;
	protected WandaFacingData facing;

	public WandaTileEntityBase() {
		tileEntityDataList = new ArrayList<WandaTileEntityData>();
	}

	protected void registTileEntityData(WandaTileEntityData data) {
		tileEntityDataList.add(data);
		if (data instanceof WandaFacingData) {
			facing = (WandaFacingData) data;
		}
	}

	protected int getDataCount() {
		return tileEntityDataList.size();
	}

	protected WandaTileEntityData getData(int index) {
		return tileEntityDataList.get(index);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		tileEntityVersion = nbt.getInteger("TileEntityVersion");
		for (WandaTileEntityData data : tileEntityDataList) {
			data.readBinary(nbt.getByteArray(data.getName()));
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setInteger("TileEntityVersion", getTileEntityVersion());
		for (WandaTileEntityData data : tileEntityDataList) {
			nbt.setByteArray(data.getName(), data.getBinary());
		}

	}

	abstract public int getTileEntityVersion();

	public String getSubChannel() {
		return SUB_CHANNEL;
	}

	@Override
	public Packet getDescriptionPacket() {
		return getPacket(this);
	}

	public static Packet getPacket(WandaTileEntityBase entity) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);

		int x = entity.xCoord;
		int y = entity.yCoord;
		int z = entity.zCoord;
		int version = entity.getTileEntityVersion();

		try {
			dos.writeInt(x);
			dos.writeInt(y);
			dos.writeInt(z);
			dos.writeInt(version);
			int tileEntityDataNum = entity.tileEntityDataList.size();
			dos.writeInt(tileEntityDataNum);
			for (int i = 0; i < tileEntityDataNum; i++) {
				WandaTileEntityData temp = entity.tileEntityDataList.get(i);
				byte[] binary = temp.getBinary();
				dos.writeInt(binary.length);
				dos.write(binary);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return WandaPacketHandlerRegistry.createWandaPacket(
				entity.getSubChannel(), bos.toByteArray(), true);
	}

	public void initialize(WandaContainerBase generatorContainer) {
	}

	public void setTileEntityData(List<byte[]> tileEntityDataList) {
		if (this.tileEntityDataList.size() == tileEntityDataList.size()) {
			for (int i = 0; i < this.tileEntityDataList.size(); i++) {
				this.tileEntityDataList.get(i).readBinary(
						tileEntityDataList.get(i));
			}
		}

	}

	public boolean hasFacing() {
		return facing != null;
	}

	public WandaFacingData getFacing() {
		return facing;
	}
}
