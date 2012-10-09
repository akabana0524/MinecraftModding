package jp.wanda.minecraft.core;

import jp.wanda.minecraft.steam.WandaSteamFuelGenerator.GeneratorTileEntity;
import net.minecraft.src.Block;
import net.minecraft.src.BlockContainer;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.Material;
import net.minecraft.src.MathHelper;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;

abstract public class WandaBlockContainerBase extends BlockContainer {

	private Class<? extends TileEntity> tileEntityClass;
	private boolean facing;
	private TileEntity tileEntity;

	protected WandaBlockContainerBase(int blockID, Material material,
			Class<? extends TileEntity> tileEntityClass) {
		super(blockID, material);
		this.tileEntityClass = tileEntityClass;
		facing = tileEntityClass.asSubclass(WandaFacingTileEntity.class) != null;
	}

	@Override
	public TileEntity createNewTileEntity(World var1) {
		tileEntity = null;
		try {
			tileEntity = tileEntityClass.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return tileEntity;
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		super.onBlockAdded(world, x, y, z);
		if (hasFacing()) {
			setDefaultDirection(world, x, y, z);
		}
	}

	private void setDefaultDirection(World world, int x, int y, int z) {
		WandaFacingTileEntity tileEntityNoop = (WandaFacingTileEntity) world
				.getBlockTileEntity(x, y, z);
		if (!world.isRemote) {
			int var5 = world.getBlockId(x, y, z - 1);
			int var6 = world.getBlockId(x, y, z + 1);
			int var7 = world.getBlockId(x - 1, y, z);
			int var8 = world.getBlockId(x + 1, y, z);
			byte var9 = 3;

			if (Block.opaqueCubeLookup[var5] && !Block.opaqueCubeLookup[var6]) {
				var9 = 3;
			}

			if (Block.opaqueCubeLookup[var6] && !Block.opaqueCubeLookup[var5]) {
				var9 = 2;
			}

			if (Block.opaqueCubeLookup[var7] && !Block.opaqueCubeLookup[var8]) {
				var9 = 5;
			}

			if (Block.opaqueCubeLookup[var8] && !Block.opaqueCubeLookup[var7]) {
				var9 = 4;
			}

			tileEntityNoop.setFacing(var9);
		}
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z,
			EntityLiving entityliving) {
		super.onBlockPlacedBy(world, x, y, z, entityliving);
		if (hasFacing()) {
			int playerFacing = MathHelper
					.floor_double((double) ((entityliving.rotationYaw * 4F) / 360F) + 0.5D) & 3;

			byte facing = 0;
			if (playerFacing == 0) {
				facing = 2;
			}
			if (playerFacing == 1) {
				facing = 5;
			}
			if (playerFacing == 2) {
				facing = 3;
			}
			if (playerFacing == 3) {
				facing = 4;
			}
			WandaFacingTileEntity facingTileEntity = (WandaFacingTileEntity) world
					.getBlockTileEntity(x, y, z);
			((WandaFacingTileEntity) facingTileEntity).setFacing(facing);
			world.markBlockNeedsUpdate(x, y, z);
		}
	}

	protected boolean hasFacing() {
		return facing;
	}

	@Override
	public int getBlockTexture(IBlockAccess par1iBlockAccess, int x, int y,
			int z, int sideIndex) {
		BlockSide dir = BlockSide.values()[sideIndex];
		BlockSide side = dir;
		if (hasFacing()) {
			WandaFacingTileEntity facing = (WandaFacingTileEntity) par1iBlockAccess
					.getBlockTileEntity(x, y, z);
			BlockSide face = BlockSide.values()[facing.getFacing()];
			side = BlockSide.convert6Side(dir, face);
		}
		return getTexture(side, x);
	}

	@Override
	public int getBlockTextureFromSideAndMetadata(int par1, int par2) {
		return getTexture(BlockSide.values()[par1], par2);
	}

	abstract public int getTexture(BlockSide side, int metaData);

}
