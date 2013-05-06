package mods.WandaCore;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class WandaInventoryGroup implements IInventory, WandaTileEntityData {

	private ItemStack[] stackList;
	private String invName;
	private int row;
	private int column;
	private int displayX;
	private int displayY;
	private boolean closingDrop;
	private boolean enablePlayerSet;

	public WandaInventoryGroup(String invName, int column, int row,
			int displayX, int displayY, boolean closingDrop,
			boolean enablePlayerSet) {
		this.stackList = new ItemStack[column * row];
		this.invName = invName;
		this.row = row;
		this.column = column;
		this.displayX = displayX;
		this.displayY = displayY;
		this.closingDrop = closingDrop;
		this.enablePlayerSet = enablePlayerSet;
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
			if (stackList[var1].stackSize <= var2) {
				ItemStack ret = stackList[var1];
				stackList[var1] = null;
				onInventoryChanged();
				return ret;
			} else {
				ItemStack itemstack = stackList[var1].splitStack(var2);

				if (stackList[var1].stackSize == 0) {
					stackList[var1] = null;
				}

				onInventoryChanged();
				return itemstack;

			}
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

	public void setInventorySlotContents(int par1, ItemStack par2ItemStack) {
		stackList[par1] = par2ItemStack;

		if (par2ItemStack != null
				&& par2ItemStack.stackSize > this.getInventoryStackLimit()) {
			par2ItemStack.stackSize = this.getInventoryStackLimit();
		}

		this.onInventoryChanged();
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

	public boolean isEnablePlayerSet() {
		return enablePlayerSet;
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
				if (stack == null) {
					dout.writeInt(-1);
				} else {
					dout.writeInt(stack.itemID);
					dout.writeInt(stack.stackSize);
					dout.writeInt(stack.getItemDamage());
				}
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
				if (itemID != -1) {
					int stackSize = dis.readInt();
					int itemDamage = dis.readInt();
					stackList[i] = new ItemStack(itemID, stackSize, itemDamage);
				}
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

	public boolean containsItem(Item item) {
		return containsItem(item, 1, 0);
	}

	public boolean containsItem(Item item, int count, int damage) {
		for (ItemStack stack : stackList) {
			if (stack == null) {
				continue;
			}
			if (item.itemID == stack.itemID && stack.stackSize >= count
					&& stack.getItemDamage() == damage) {
				return true;
			}
		}
		return false;
	}

	public boolean removeItem(ItemStack itemStack) {
		for (int i = 0; i < stackList.length; i++) {
			ItemStack stack = stackList[i];
			if (stack != null) {
				if (itemStack.itemID == stack.itemID
						&& stack.stackSize >= itemStack.stackSize
						&& stack.getItemDamage() == itemStack.getItemDamage()) {
					stack.stackSize -= itemStack.stackSize;
					if (stack.stackSize == 0) {
						stackList[i] = null;
					}
					return true;
				}
			}
		}
		return false;
	}

	public boolean addItem(ItemStack itemStack) {
		int nullIndex = -1;
		for (int i = 0; i < stackList.length; i++) {
			ItemStack stack = stackList[i];
			if (stack == null) {
				nullIndex = i;
				continue;
			}
			if (itemStack.itemID == stack.itemID
					&& stack.getItemDamage() == itemStack.getItemDamage()) {
				stack.stackSize += itemStack.stackSize;
				if (stack.stackSize > stack.getMaxStackSize()) {
					itemStack.stackSize -= stack.stackSize
							- stack.getMaxStackSize();
					stack.stackSize = stack.getMaxStackSize();
				}
				return true;
			}
		}
		if (nullIndex != -1) {
			stackList[nullIndex] = itemStack;
			return true;
		}
		return false;
	}

	@Override
	/**
	 * If this returns false, the inventory name will be used as an unlocalized name, and translated into the player's
	 * language. Otherwise it will be used directly.
	 */
	public boolean isInvNameLocalized() {
		return false;
	}

	@Override
	/**
	 * Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot.
	 */
	public boolean isStackValidForSlot(int i, ItemStack itemstack) {
		return false;
	}
}
