package jp.wanda.minecraft.core;

import java.util.ArrayList;
import java.util.List;

import jp.wanda.minecraft.steam.WandaSteamFuelGenerator.GeneratorTileEntity;

import net.minecraft.src.Container;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IInventory;
import net.minecraft.src.InventoryPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Slot;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;

public abstract class WandaContainerBase extends Container {

	public static final int SLOT_SIZE = 18;
	protected EntityPlayer player;
	protected InventoryPlayer playerInventory;
	protected World world;
	protected int x;
	protected int y;
	protected int z;
	protected List<WandaInventoryBlock> extraInventoryList;

	public WandaContainerBase(EntityPlayer player, World world, int x, int y,
			int z) {
		this.player = player;
		this.playerInventory = player.inventory;
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;

		for (int rows = 0; rows < 3; ++rows) {
			for (int slotIndex = 0; slotIndex < 9; ++slotIndex) {
				addSlotToContainer(new Slot(playerInventory, slotIndex + rows
						* 9 + 9, 8 + slotIndex * 18, 84 + rows * 18));
			}
		}

		for (int slotIndex = 0; slotIndex < 9; ++slotIndex) {
			addSlotToContainer(new Slot(playerInventory, slotIndex,
					8 + slotIndex * 18, 142));
		}
		extraInventoryList = new ArrayList<WandaInventoryBlock>();
		if (hasTileEntity()) {
			TileEntity entity = world.getBlockTileEntity(x, y, z);
			if (entity != null) {
				if (entity instanceof WandaTileEntityBase) {
					WandaTileEntityBase wandaTileEntity = (WandaTileEntityBase) entity;
					for (int i = 0; i < wandaTileEntity
							.getInventoryBlockCount(); i++) {
						addExtraInventory(wandaTileEntity.getInventoryBlock(i));
					}
				}
			}
		}
	}

	abstract protected boolean hasTileEntity();

	abstract protected int getInventoryBlockX(int index);

	abstract protected int getInventoryBlockY(int index);

	@Override
	public ItemStack transferStackInSlot(int par1) {
		return null;
	}

	@Override
	public boolean canInteractWith(EntityPlayer var1) {
		return true;
	}

	@Override
	public void onCraftGuiClosed(EntityPlayer par1EntityPlayer) {
		super.onCraftGuiClosed(par1EntityPlayer);
		if (!this.world.isRemote) {
			List<ItemStack> dropItemList = new ArrayList<ItemStack>();
			for (IInventory inventory : extraInventoryList) {
				for (int i = 0; i < inventory.getSizeInventory(); i++) {
					ItemStack item = inventory.getStackInSlotOnClosing(i);
					if (item != null) {
						par1EntityPlayer.dropPlayerItem(item);
					}
				}
			}
		}
	}

	protected void addExtraInventory(WandaInventoryBlock extraInventory) {
		int displayX = extraInventory.getDisplayX();
		int displayY = extraInventory.getDisplayY();
		for (int rows = 0; rows < extraInventory.getRow(); rows++) {
			for (int columns = 0; columns < extraInventory.getColumn(); columns++) {
				addSlotToContainer(new Slot(extraInventory, columns + rows
						* extraInventory.getColumn(), displayX + columns
						* SLOT_SIZE, displayY + rows * SLOT_SIZE));
			}
		}
		extraInventoryList.add(extraInventory);
	}
}
