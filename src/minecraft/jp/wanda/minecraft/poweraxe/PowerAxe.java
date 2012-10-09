package jp.wanda.minecraft.poweraxe;

import java.util.ArrayList;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import net.minecraft.src.Block;
import net.minecraft.src.BlockGlowStone;
import net.minecraft.src.BlockOre;
import net.minecraft.src.BlockRedstoneOre;
import net.minecraft.src.EntityItem;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.EnumToolMaterial;
import net.minecraft.src.ItemAxe;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Packet61DoorChange;
import net.minecraft.src.World;

public class PowerAxe extends ItemAxe {
	private boolean enableEffect;
	private ArrayList<Integer> targetBlockList;
	private int horizontalRange;
	private int verticalRange;
	private mod_WandaPowerAxe mod;

	public PowerAxe(int par1, int iconIndex,
			EnumToolMaterial par2EnumToolMaterial, mod_WandaPowerAxe mod) {
		super(par1, par2EnumToolMaterial);
		this.mod = mod;
		this.iconIndex = iconIndex;
	}

	@Override
	public boolean func_77660_a(ItemStack itemStack, World world, int blockID,
			int x, int y, int z, EntityLiving entityLiving) {
		int damage = itemStack.getItemDamage();
		if (!world.isRemote) {
			EntityPlayerMP player = (EntityPlayerMP) entityLiving;
			if (mod.isEnable(player.username)) {
				Block block = Block.blocksList[blockID];
				if (check(block, blockID)) {
					int breakCount = relay(world, player, x, y, z, x, y, z);
					itemStack.damageItem(
							(int) (breakCount * mod.getDamageRatio()),
							entityLiving);
				}
			}
		}

		boolean ret = super.func_77660_a(itemStack, world, blockID, x, y, z,
				entityLiving);
		return ret;
	}

	private int relay(World world, EntityPlayerMP player, int x, int y, int z,
			int startX, int startY, int startZ) {
		int ret = 0;

		for (int offY = 0; offY < 2; offY++) {
			int currentY = y + offY;

			for (int offX = 0; offX < 3; offX++) {
				int currentX = x - 1 + offX;

				for (int offZ = 0; offZ < 3; offZ++) {
					int currentZ = z - 1 + offZ;
					int blockID = world
							.getBlockId(currentX, currentY, currentZ);
					Block block = Block.blocksList[blockID];

					if (check(block, blockID)) {
						player.addExhaustion((float) mod.getExhaustionRatio());
						int metaData = world.getBlockMetadata(currentX,
								currentY, currentZ);
						if (enableEffect) {
							world.playAuxSFX(2001, currentX, currentY,
									currentZ, block.blockID + (metaData << 12));
						}
						block.harvestBlock(world, player, x, y, z, metaData);
						world.setBlockWithNotify(currentX, currentY, currentZ,
								0);
						ret++;

						double tempX = currentX - startX;
						double tempZ = currentZ - startZ;
						double horizontalDistance = Math.sqrt(tempX * tempX
								+ tempZ * tempZ);
						if (horizontalDistance < this.horizontalRange
								&& currentY - startY < this.verticalRange) {
							ret += relay(world, player, currentX, currentY,
									currentZ, startX, startY, startZ);
						}
					}
				}
			}
		}

		return ret;
	}

	@Override
	public String getTextureFile() {
		return "/WandaResource/WandaTools.png";
	}

	public void setEnableEffect(boolean enable) {
		this.enableEffect = enable;
	}

	private boolean check(Block block, int blockID) {

		if (targetBlockList != null && targetBlockList.contains(blockID)) {
			return true;
		}

		return block == Block.wood;
	}

	public void setTargetBlockList(String listStr) {
		if (listStr == null || listStr.trim().equals("")) {
			return;
		}
		String[] array = listStr.split(",");
		ArrayList<Integer> list = new ArrayList<Integer>();
		for (String value : array) {
			try {
				list.add(Integer.parseInt(value));
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		this.targetBlockList = list;
	}

	public void setHorizontalRange(int range) {
		this.horizontalRange = range;
	}

	public void setVerticalRange(int range) {
		this.verticalRange = range;
	}

}
