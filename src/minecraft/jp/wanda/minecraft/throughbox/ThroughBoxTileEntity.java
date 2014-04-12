package jp.wanda.minecraft.throughbox;

import java.util.List;

import jp.wanda.minecraft.core.WandaTileEntityBase;
import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.Block;
import net.minecraft.src.EntityItem;
import net.minecraft.src.IInventory;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.TileEntity;
import cpw.mods.fml.common.FMLLog;

public class ThroughBoxTileEntity extends WandaTileEntityBase {

	private AxisAlignedBB aabb;

	public ThroughBoxTileEntity() {
		aabb = AxisAlignedBB.getBoundingBox(0, 0, 0, 0, 0, 0);
	}

	@Override
	public int getTileEntityVersion() {
		return 0;
	}

	@Override
	public String getChannel() {
		return "ThroughBox";
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		if (!worldObj.isRemote) {
			if (worldObj.rand.nextInt(5) == 0) {
				int targetBlockX = xCoord;
				int targetBlockY = yCoord;
				int targetBlockZ = zCoord;
				switch (getBlockMetadata()) {
				case 0:
					targetBlockY--;
					break;
				case 2:
					targetBlockZ--;
					break;
				case 3:
					targetBlockZ++;
					break;
				case 4:
					targetBlockX--;
					break;
				case 5:
					targetBlockX++;
					break;
				}
				int blockID = worldObj.getBlockId(targetBlockX, targetBlockY,
						targetBlockZ);
				int blockMeta = worldObj.getBlockMetadata(targetBlockX,
						targetBlockY, targetBlockZ);
				Block targetBlock = Block.blocksList[blockID];
				if (targetBlock == null || targetBlock.hasTileEntity(blockMeta)) {
					updateAABB();
					List list = worldObj.getEntitiesWithinAABB(
							EntityItem.class, aabb);
					for (Object item : list) {
						EntityItem aabb = (EntityItem) item;
						if (targetBlock == null) {
							double startPosX = xCoord;
							double startPosY = yCoord;
							double startPosZ = zCoord;
							double motionX = 0;
							double motionY = 0;
							double motionZ = 0;
							switch (getBlockMetadata()) {
							case 0:
								startPosX += 0.5;
								startPosZ += 0.5;
								motionY = 0.1;
								break;
							case 2:
								startPosY += 0.5;
								startPosX += 0.5;
								motionZ = -0.1;
								break;
							case 3:
								startPosY += 0.5;
								startPosX += 0.5;
								startPosZ += 1;
								motionZ = 0.1;
								break;
							case 4:
								startPosY += 0.5;
								startPosZ += 0.5;
								motionX = -0.1;
								break;
							case 5:
								startPosY += 0.5;
								startPosZ += 0.5;
								startPosX += 1;
								motionX = 0.1;
								break;
							}
							EntityItem temp = new EntityItem(worldObj,
									startPosX, startPosY, startPosZ, aabb.item);
							temp.motionX = motionX;
							temp.motionY = motionY;
							temp.motionZ = motionZ;
							worldObj.spawnEntityInWorld(temp);
							aabb.setDead();
						} else {
							TileEntity te = worldObj.getBlockTileEntity(
									targetBlockX, targetBlockY, targetBlockZ);
							if (te instanceof IInventory) {
								IInventory inventory = (IInventory) te;
								for (int i = 0; i < inventory
										.getSizeInventory(); i++) {
									ItemStack itemStack = inventory
											.getStackInSlot(i);
									if (itemStack == null) {
										inventory.setInventorySlotContents(i,
												aabb.item);
										aabb.setDead();
									} else {
										if (itemStack.itemID == aabb.item.itemID) {
											itemStack.stackSize += aabb.item.stackSize;
											if (itemStack.stackSize > itemStack
													.getMaxStackSize()) {
												aabb.item.stackSize = itemStack.stackSize
														- itemStack
																.getMaxStackSize();
												itemStack.stackSize = itemStack
														.getMaxStackSize();
											} else {
												aabb.setDead();
											}
										}
									}
									if (aabb.isDead) {
										break;
									}
								}
							}
						}

					}
				}
			}
		}
	}

	private void updateAABB() {
		aabb.minX = xCoord;
		aabb.maxX = xCoord + 1;
		aabb.minY = yCoord + 1;
		aabb.maxY = yCoord + 1.3;
		aabb.minZ = zCoord;
		aabb.maxZ = zCoord + 1;
	}
}
