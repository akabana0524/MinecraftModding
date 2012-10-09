package jp.wanda.minecraft.core;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import samples.containersamplemod.InventoryNoop;

import net.minecraft.src.InventoryBasic;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.Packet;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.TileEntity;
import cpw.mods.fml.common.FMLLog;

abstract public class WandaTileEntityBase extends TileEntity {

	protected List<WandaInventoryBlock> inventoryBlockList;
	protected int tileEntityVersion;
	protected int inventoryNum;

	public WandaTileEntityBase() {
		inventoryBlockList = new ArrayList<WandaInventoryBlock>();
	}

	protected void addInventory(WandaInventoryBlock inventory) {
		inventoryBlockList.add(inventory);
		inventoryNum += inventory.getSizeInventory();
	}

	protected int getInventoryBlockCount() {
		return inventoryBlockList.size();
	}

	protected WandaInventoryBlock getInventoryBlock(int index) {
		return inventoryBlockList.get(index);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		tileEntityVersion = nbt.getInteger("TileEntityVersion");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setInteger("TileEntityVersion", getTileEntityVersion());
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
			int extraInventroryNum = entity.inventoryBlockList.size();
			dos.writeInt(entity.inventoryNum);
			for (int i = 0; i < extraInventroryNum; i++) {
				WandaInventoryBlock inventoryBlock = entity.inventoryBlockList
						.get(i);
				for (int j = 0; j < inventoryBlock.getSizeInventory(); j++) {
					ItemStack stack = inventoryBlock.getStackInSlot(j);
					if (stack == null) {
						dos.writeInt(0);
					} else {
						dos.writeInt(stack.itemID);
						dos.writeInt(stack.getItemDamage());
						dos.writeInt(stack.stackSize);
					}
				}
			}
			dos.writeInt(data.length);
			dos.write(data);
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
}
