package jp.wanda.minecraft.harvestsickle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cpw.mods.fml.common.asm.SideOnly;

import jp.wanda.minecraft.MaterialTable;
import net.minecraft.client.Minecraft;
import net.minecraft.src.Block;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.EnumToolMaterial;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ItemTool;
import net.minecraft.src.World;

public class HarvestSickle extends ItemTool {

	private List<int[]> positionList;
	private boolean enableEffect;

	public HarvestSickle(int itemID, int iconIndex, int damageVsEntity,
			EnumToolMaterial mat) {
		super(itemID, damageVsEntity, mat, new Block[] {});
		this.iconIndex = iconIndex;
	}

	@Override
	public boolean tryPlaceIntoWorld(ItemStack itemStack,
			EntityPlayer entityPlayer, World world, int x, int y, int z,
			int side, float clickPosX, float clickPosY, float clickPosZ) {
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

		return super.tryPlaceIntoWorld(itemStack, entityPlayer, world, x, y, z,
				side, clickPosX, clickPosY, clickPosZ);
	}

	@Override
	public boolean func_77660_a(ItemStack itemStack, World world, int blockID,
			int x, int y, int z, EntityLiving entityLiving) {
		if (!world.isRemote) {
			EntityPlayerMP player = (EntityPlayerMP) entityLiving;
			Block block = Block.blocksList[blockID];
			int metaData = world.getBlockMetadata(x, y, z);

			if (isTarget(block, metaData, true)) {
				positionList = new ArrayList<int[]>();
				if (harvest(itemStack, world, player, x, y, z)) {
					itemStack.damageItem(1, player);
				}
			}
		}

		return super.func_77660_a(itemStack, world, blockID, x, y, z,
				entityLiving);
	}

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
					world.setBlockWithNotify(currentX, y, currentZ, 0);
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

	@Override
	public String getTextureFile() {
		return "/WandaResource/WandaTools.png";
	}

	public void setEnableEffect(boolean enable) {
		this.enableEffect = enable;

	}
}
