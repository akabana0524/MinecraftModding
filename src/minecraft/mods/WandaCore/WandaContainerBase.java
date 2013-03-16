package mods.WandaCore;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public abstract class WandaContainerBase extends Container {

	public static final int SLOT_SIZE = 18;
	protected EntityPlayer player;
	protected InventoryPlayer playerInventory;
	protected World world;
	protected int x;
	protected int y;
	protected int z;
	protected List<WandaInventoryGroup> extraInventoryList;

	public WandaContainerBase(EntityPlayer player, World world, int x, int y,
			int z) {
		this.player = player;
		this.playerInventory = player.inventory;
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;

		for (int slotIndex = 0; slotIndex < 9; ++slotIndex) {
			addSlotToContainer(new Slot(playerInventory, slotIndex,
					8 + slotIndex * 18, 142));
		}
		for (int rows = 0; rows < 3; ++rows) {
			for (int slotIndex = 0; slotIndex < 9; ++slotIndex) {
				addSlotToContainer(new Slot(playerInventory, slotIndex + rows
						* 9 + 9, 8 + slotIndex * 18, 84 + rows * 18));
			}
		}

		extraInventoryList = new ArrayList<WandaInventoryGroup>();
		setupExtraInventory();
		if (hasTileEntity()) {
			TileEntity entity = world.getBlockTileEntity(x, y, z);
			if (entity != null) {
				if (entity instanceof WandaTileEntityBase) {
					WandaTileEntityBase wandaTileEntity = (WandaTileEntityBase) entity;
					int inventoryIndex = 36;
					for (int i = 0; i < wandaTileEntity.getDataCount(); i++) {
						WandaTileEntityData data = wandaTileEntity.getData(i);
						if (data instanceof WandaInventoryGroup) {
							WandaInventoryGroup inventoryGroup = (WandaInventoryGroup) data;
							addExtraInventory(inventoryGroup);
						}
					}
				}
			}
		}
	}

	protected void setupExtraInventory() {
	}

	abstract protected boolean hasTileEntity();

	@Override
	public boolean canInteractWith(EntityPlayer var1) {
		return true;
	}

	@Override
	public void onCraftGuiClosed(EntityPlayer par1EntityPlayer) {
		super.onCraftGuiClosed(par1EntityPlayer);
		if (!this.world.isRemote) {
			List<ItemStack> dropItemList = new ArrayList<ItemStack>();
			for (WandaInventoryGroup inventory : extraInventoryList) {
				if (inventory.isClosingDrop()) {
					for (int i = 0; i < inventory.getSizeInventory(); i++) {
						ItemStack item = inventory.getStackInSlotOnClosing(i);
						if (item != null) {
							par1EntityPlayer.dropPlayerItem(item);
						}
					}
				}
			}
		}
	}

	protected void addExtraInventory(final WandaInventoryGroup extraInventory) {
		int displayX = extraInventory.getDisplayX();
		int displayY = extraInventory.getDisplayY();
		for (int rows = 0; rows < extraInventory.getRow(); rows++) {
			for (int columns = 0; columns < extraInventory.getColumn(); columns++) {
				addSlotToContainer(new Slot(extraInventory, columns + rows
						* extraInventory.getColumn(), displayX + columns
						* SLOT_SIZE, displayY + rows * SLOT_SIZE) {
					@Override
					public boolean isItemValid(ItemStack par1ItemStack) {
						return extraInventory.isEnablePlayerSet();
					}
				});
			}
		}
		extraInventoryList.add(extraInventory);
	}
}
