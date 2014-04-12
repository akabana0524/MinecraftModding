package jp.wanda.minecraft.core;

import java.util.Random;

import jp.wanda.minecraft.core.tileentity.WandaFacingData;
import jp.wanda.minecraft.steam.WandaSteamFuelGenerator.GeneratorTileEntity;
import net.minecraft.src.Block;
import net.minecraft.src.BlockContainer;
import net.minecraft.src.EntityItem;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Material;
import net.minecraft.src.MathHelper;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.TileEntity;
import net.minecraft.src.TileEntityChest;
import net.minecraft.src.World;

abstract public class WandaBlockContainerBase extends BlockContainer {

	private Class<? extends WandaTileEntityBase> tileEntityClass;
	private WandaTileEntityBase tileEntity;
	private boolean enableFacing;
	private Random random = new Random();

	protected WandaBlockContainerBase(int blockID, Material material,
			Class<? extends WandaTileEntityBase> tileEntityClass) {
		super(blockID, material);
		this.tileEntityClass = tileEntityClass;
	}

	@Override
	public TileEntity createNewTileEntity(World var1) {
		tileEntity = null;
		if (tileEntityClass != null) {
			try {
				tileEntity = tileEntityClass.newInstance();
				enableFacing = tileEntity.getFacing() != null;
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return tileEntity;
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		super.onBlockAdded(world, x, y, z);
		if (enableFacing) {
			getFacing(world, x, y, z).setDefaultDirection(world, x, y, z);
		}
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z,
			EntityLiving entityliving) {
		super.onBlockPlacedBy(world, x, y, z, entityliving);
		if (enableFacing) {
			getFacing(world, x, y, z).onBlockPlacedBy(world, x, y, z,
					entityliving);
		}
		world.markBlockNeedsUpdate(x, y, z);
	}

	@Override
	public int getBlockTexture(IBlockAccess par1iBlockAccess, int x, int y,
			int z, int sideIndex) {
		BlockSide dir = BlockSide.values()[sideIndex];
		BlockSide side = dir;
		if (enableFacing) {
			side = getFacing(par1iBlockAccess, x, y, z).getSide(dir);
		}
		return getTexture(side, x);
	}

	@Override
	public int getBlockTextureFromSideAndMetadata(int par1, int par2) {
		return getTexture(BlockSide.values()[par1], par2);
	}

	abstract public int getTexture(BlockSide side, int metaData);

	protected WandaFacingData getFacing(IBlockAccess world, int x, int y, int z) {
		WandaTileEntityBase tileEntity = (WandaTileEntityBase) world
				.getBlockTileEntity(x, y, z);
		return tileEntity.getFacing();
	}

	@Override
	public void onBlockEventReceived(World par1World, int par2, int par3,
			int par4, int par5, int par6) {
		super.onBlockEventReceived(par1World, par2, par3, par4, par5, par6);
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, int blockID,
			int blockMetaData) {
		onBlockDestroyed(world, x, y, z);
		super.breakBlock(world, x, y, z, blockID, blockMetaData);
	}

	protected void onBlockDestroyed(World world, int x, int y, int z) {
		WandaTileEntityBase tileEntity = (WandaTileEntityBase) world
				.getBlockTileEntity(x, y, z);
		for (WandaTileEntityData data : tileEntity.tileEntityDataList) {
			if (data instanceof WandaInventoryGroup) {
				WandaInventoryGroup var7 = (WandaInventoryGroup) data;
				for (int var8 = 0; var8 < var7.getSizeInventory(); ++var8) {
					ItemStack var9 = var7.getStackInSlot(var8);
					if (var9 != null) {
						float var10 = this.random.nextFloat() * 0.8F + 0.1F;
						float var11 = this.random.nextFloat() * 0.8F + 0.1F;
						EntityItem var14;
						for (float var12 = this.random.nextFloat() * 0.8F + 0.1F; var9.stackSize > 0; world
								.spawnEntityInWorld(var14)) {
							int var13 = this.random.nextInt(21) + 10;
							if (var13 > var9.stackSize) {
								var13 = var9.stackSize;
							}
							var9.stackSize -= var13;
							var14 = new EntityItem(world,
									(double) ((float) x + var10),
									(double) ((float) y + var11),
									(double) ((float) z + var12),
									new ItemStack(var9.itemID, var13,
											var9.getItemDamage()));
							float var15 = 0.05F;
							var14.motionX = (double) ((float) this.random
									.nextGaussian() * var15);
							var14.motionY = (double) ((float) this.random
									.nextGaussian() * var15 + 0.2F);
							var14.motionZ = (double) ((float) this.random
									.nextGaussian() * var15);

							if (var9.hasTagCompound()) {
								var14.item.setTagCompound((NBTTagCompound) var9
										.getTagCompound().copy());
							}
						}
					}
				}

			}
		}
	}

}
