package jp.wanda.minecraft.core;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.Packet;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.TileEntity;

abstract public class WandaTileEntityBase extends TileEntity {

	protected int tileEntityVersion;
	protected List<WandaTileEntityData> tileEntityDataList;

	public WandaTileEntityBase() {
		tileEntityDataList = new ArrayList<WandaTileEntityData>();
	}

	protected void registTileEntityData(WandaTileEntityData data) {
		tileEntityDataList.add(data);
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

	abstract public byte[] getAuxillaryInfoPacketData();

	abstract public void setAuxillaryInfoPacketData(DataInputStream din)
			throws IOException;

	abstract public String getChannel();

	@Override
	public Packet getAuxillaryInfoPacket() {
		return getPacket(this);
	}

	public static Packet getPacket(WandaTileEntityBase entity) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);

		int x = entity.xCoord;
		int y = entity.yCoord;
		int z = entity.zCoord;
		int version = entity.getTileEntityVersion();
		byte[] data = entity.getAuxillaryInfoPacketData();

		try {
			dos.writeInt(x);
			dos.writeInt(y);
			dos.writeInt(z);
			dos.writeInt(version);
			dos.writeInt(data.length);
			dos.write(data);
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

		Packet250CustomPayload packet = new Packet250CustomPayload();
		packet.channel = entity.getChannel();
		packet.data = bos.toByteArray();
		packet.length = bos.size();
		packet.isChunkDataPacket = true;

		return packet;
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
}
