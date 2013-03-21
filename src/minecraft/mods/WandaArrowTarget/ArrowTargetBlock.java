package mods.WandaArrowTarget;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ArrowTargetBlock extends Block {

	private Icon iconTop;
	private Icon defaultIcon;

	public ArrowTargetBlock(int blockID, Material blockMaterial) {
		super(blockID, blockMaterial);
	}

	@Override
	public Icon getBlockTextureFromSideAndMetadata(int par1, int par2) {
		if (par1 == 1) {
			return iconTop;
		} else {
			return super.getBlockTextureFromSideAndMetadata(par1, par2);
		}
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z,
			Entity entity) {
		if (world.isRemote) {
			return;
		}
		if (entity instanceof EntityArrow) {
			EntityArrow arrow = (EntityArrow) entity;
			world.playAuxSFX(2001, x, y, z,
					blockID + (world.getBlockMetadata(x, y, z) << 12));
			world.setBlock(x, y, z, 0, 0, 3);
			entity.setDead();
		}
		super.onEntityCollidedWithBlock(world, x, y, z, entity);
	}

	@SideOnly(Side.CLIENT)

	/**
	 * When this method is called, your block should register all the icons it needs with the given IconRegister. This is
	 * the only chance you get to register icons.
	 */
	public void registerIcons(IconRegister par1IconRegister) {
		this.blockIcon = par1IconRegister
				.registerIcon("WandaArrowTarget:ArrowTarget00");
		this.iconTop = par1IconRegister
				.registerIcon("WandaArrowTarget:ArrowTarget01");
	}

}
