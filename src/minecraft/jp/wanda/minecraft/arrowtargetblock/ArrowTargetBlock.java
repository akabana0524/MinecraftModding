package jp.wanda.minecraft.arrowtargetblock;

import net.minecraft.src.Block;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityArrow;
import net.minecraft.src.Material;
import net.minecraft.src.World;

public class ArrowTargetBlock extends Block {

	public ArrowTargetBlock(int blockID, Material blockMaterial) {
		super(blockID, blockMaterial);
	}

	@Override
	public int getBlockTextureFromSide(int par1) {
		if (par1 == 1) {
			return 1;
		} else {
			return 0;
		}
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z,
			Entity entity) {
		if (world.isRemote) {
			return;
		}
		if (entity instanceof EntityArrow) {
			world.playAuxSFX(2001, x, y, z,
					blockID + (world.getBlockMetadata(x, y, z) << 12));
			world.setBlockWithNotify(x, y, z, 0);
			entity.setDead();
		}
		super.onEntityCollidedWithBlock(world, x, y, z, entity);
	}

	@Override
	public String getTextureFile() {
		return "/WandaResource/WandaBlocks.png";
	}
}
