package jp.wanda.minecraft.core;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IInventory;
import net.minecraft.src.ItemStack;

public class WandaInventoryBlock implements IInventory {

	private ItemStack[] stackList;
	private String invName;
	private int row;
	private int column;
	private int displayX;
	private int displayY;

	public WandaInventoryBlock(String invName, int column, int row,
			int displayX, int displayY) {
		this.stackList = new ItemStack[column * row];
		this.invName = invName;
		this.row = row;
		this.column = column;
		this.displayX = displayX;
		this.displayY = displayY;
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
}
