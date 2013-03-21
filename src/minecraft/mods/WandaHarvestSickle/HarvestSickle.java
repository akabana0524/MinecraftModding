package mods.WandaHarvestSickle;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.world.World;

public class HarvestSickle extends ItemTool {

	private List<int[]> positionList;
	private boolean enableEffect;

	public HarvestSickle(int itemID, int damageVsEntity, EnumToolMaterial mat) {
		super(itemID, damageVsEntity, mat, new Block[] {});
	}

	@Override
	public boolean onItemUse(ItemStack itemStack, EntityPlayer entityPlayer,
			World world, int x, int y, int z, int side, float clickPosX,
			float clickPosY, float clickPosZ) {
		int blockID = world.getBlockId(x, y, z);
		Block block = Block.blocksList[blockID];
		int metaData = world.getBlockMetadata(x, y, z);

		if (isTarget(block, metaData, false)) {
			entityPlayer.swingItem();

			if (!world.isRemote) {
				EntityPlayerMP player = (EntityPlayerMP) entityPlayer;

				positionList = new ArrayList<int[]>();
				if (harvest(itemStack, world, player, x, y, z)) {
					itemStack.damageItem(1, player);
				}
			}
		}

		return super.onItemUse(itemStack, entityPlayer, world, x, y, z, side,
				clickPosX, clickPosY, clickPosZ);
	}

	@Override
	public boolean onItemUseFirst(ItemStack stack, EntityPlayer player,
			World world, int x, int y, int z, int side, float hitX, float hitY,
			float hitZ) {
		if (!world.isRemote) {
			int blockID = world.getBlockId(x, y, z);
			EntityPlayerMP mp = (EntityPlayerMP) player;
			Block block = Block.blocksList[blockID];
			int metaData = world.getBlockMetadata(x, y, z);

			if (isTarget(block, metaData, true)) {
				positionList = new ArrayList<int[]>();
				if (harvest(stack, world, mp, x, y, z)) {
					stack.damageItem(1, player);
				}
			}
		}

		return super.onItemUseFirst(stack, player, world, x, y, z, side, hitX,
				hitY, hitZ);
	}

	// @Override
	// public boolean onBlockDestroyed(ItemStack itemStack, World world, int
	// blockID,
	// int x, int y, int z, EntityLiving entityLiving) {
	// if (!world.isRemote) {
	// EntityPlayerMP player = (EntityPlayerMP) entityLiving;
	// Block block = Block.blocksList[blockID];
	// int metaData = world.getBlockMetadata(x, y, z);
	//
	// if (isTarget(block, metaData, true)) {
	// positionList = new ArrayList<int[]>();
	// if (harvest(itemStack, world, player, x, y, z)) {
	// itemStack.damageItem(1, player);
	// }
	// }
	// }
	//
	// return super.onBlockDestroyed(itemStack, world, blockID, x, y, z,
	// entityLiving);
	// }

	private boolean harvest(ItemStack item, World world, EntityPlayerMP player,
			int x, int y, int z) {
		boolean ret = false;

		for (int offX = 0; offX < 5; offX++) {
			int currentX = x - 2 + offX;

			for (int offZ = 0; offZ < 5; offZ++) {
				int currentZ = z - 2 + offZ;
				int blockID = world.getBlockId(currentX, y, currentZ);
				Block block = Block.blocksList[blockID];
				int metaData = world.getBlockMetadata(currentX, y, currentZ);

				if (isTarget(block, metaData, false)) {
					positionList.add(new int[] { block.blockID, currentX, y,
							currentZ });
					ret = true;
					if (enableEffect) {
						world.playAuxSFX(
								2001,
								currentX,
								y,
								currentZ,
								block.blockID
										+ (world.getBlockMetadata(currentX, y,
												currentZ) << 12));
					}

					block.harvestBlock(world, player, currentX, y, currentZ,
							metaData);
					world.setBlock(currentX, y, currentZ, 0, 0, 3);
				}
			}
		}

		return ret;
	}

	private boolean isTarget(Block block, int metaData, boolean breaked) {
		boolean isGrass = block == Block.tallGrass;

		if (isGrass) {
			return true;
		}

		if (block == Block.crops) {
			if (breaked || metaData == 7) {
				return true;
			}
		}

		return false;
	}

	public void setEnableEffect(boolean enable) {
		this.enableEffect = enable;

	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateIcons(IconRegister par1IconRegister) {
		String path = null;
		switch (toolMaterial) {
		case IRON:
			path = "WandaHarvestSickle:HarvestSickle00";
			break;
		case EMERALD:
			path = "WandaHarvestSickle:HarvestSickle01";
			break;
		case GOLD:
			path = "WandaHarvestSickle:HarvestSickle02";
			break;
		default:
			path = "WandaHarvestSickle:HarvestSickle00";
			break;
		}
		iconIndex = par1IconRegister.registerIcon(path);
	}
}
