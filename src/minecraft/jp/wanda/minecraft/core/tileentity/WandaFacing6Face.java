package jp.wanda.minecraft.core.tileentity;

import jp.wanda.minecraft.core.BlockSide;
import net.minecraft.src.Block;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.MathHelper;
import net.minecraft.src.World;

public class WandaFacing6Face extends WandaFacingData {

	@Override
	public void setDefaultDirection(World world, int x, int y, int z) {
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

			setFace(var9);
		}
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z,
			EntityLiving entityliving) {
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
		setFace(facing);

	}

	@Override
	public BlockSide getSide(BlockSide dir) {
		BlockSide face = BlockSide.values()[getFace()];
		return BlockSide.convert6Side(dir, face);
	}

}
