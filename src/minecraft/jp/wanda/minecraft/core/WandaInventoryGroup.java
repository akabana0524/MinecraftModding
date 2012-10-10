package jp.wanda.minecraft.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IInventory;
import net.minecraft.src.ItemStack;

public class WandaInventoryGroup implements IInventory, WandaTileEntityData {

	private ItemStack[] stackList;
	private String invName;
	private int row;
	private int column;
	private int displayX;
	private int displayY;
	private boolean closingDrop;

	public WandaInventoryGroup(String invName, int column, int row,
			int displayX, int displayY, boolean closingDrop) {
		this.stackList = new ItemStack[column * row];
		this.invName = invName;
		this.row = row;
		this.column = column;
		this.displayX = displayX;
		this.displayY = displayY;
		this.closingDrop = closingDrop;
	}

	@Override
	public String getInvName() {
		return invName;
	}

	@Override
	public int getSizeInventory() {
		return stackList.length;
	}

	@Override
	public ItemStack getStackInSlot(int var1) {
		return stackList[var1];
	}

	@Override
	public ItemStack decrStackSize(int var1, int var2) {
		if (stackList[var1] != null) {
			ItemStack ret = stackList[var1];
			stackList[var1] = null;
			return ret;
		} else {
			return null;
		}
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int var1) {
		if (stackList[var1] != null) {
			ItemStack ret = stackList[var1];
			stackList[var1] = null;
			return ret;
		} else {
			return null;
		}
	}

	@Override
	public void setInventorySlotContents(int var1, ItemStack var2) {
		this.stackList[var1] = var2;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public void onInventoryChanged() {
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer var1) {
		return true;
	}

	@Override
	public void openChest() {
	}

	@Override
	public void closeChest() {
	}

	public int getColumn() {
		return column;
	}

	public int getRow() {
		return row;
	}

	public int getDisplayX() {
		return displayX;
	}

	public int getDisplayY() {
		return displayY;
	}

	public boolean isClosingDrop() {
		return closingDrop;
	}

	@Override
	public String getName() {
		return getInvName();
	}

	@Override
	public byte[] getBinary() {
		byte[] ret = null;
		ByteArrayOutputStream bout = null;
		DataOutputStream dout = null;
		bout = new ByteArrayOutputStream();
		dout = new DataOutputStream(bout);
		try {
			dout.writeInt(stackList.length);
			for (ItemStack stack : stackList) {
				dout.writeInt(stack.itemID);
				dout.writeInt(stack.stackSize);
				dout.writeInt(stack.getItemDamage());
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bout != null) {
				try {
					bout.close();
					ret = bout.toByteArray();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return ret;
	}

	@Override
	public void readBinary(byte[] binary) {
		ByteArrayInputStream bin = null;
		DataInputStream dis = null;
		bin = new ByteArrayInputStream(binary);
		dis = new DataInputStream(bin);
		try {
			int length = dis.readInt();
			for (int i = 0; i < length; i++) {
				int itemID = dis.readInt();
				int stackSize = dis.readInt();
				int itemDamage = dis.readInt();
				stackList[i] = new ItemStack(itemID, stackSize, itemDamage);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bin != null) {
				try {
					bin.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
