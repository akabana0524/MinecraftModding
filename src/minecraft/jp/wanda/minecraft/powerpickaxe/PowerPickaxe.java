package jp.wanda.minecraft.powerpickaxe;

import java.util.ArrayList;
import java.util.List;

import jp.wanda.minecraft.res.WandaResource;

import net.minecraft.src.Block;
import net.minecraft.src.BlockGlowStone;
import net.minecraft.src.BlockOre;
import net.minecraft.src.BlockRedstoneOre;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.EnumToolMaterial;
import net.minecraft.src.ItemPickaxe;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;

public class PowerPickaxe extends ItemPickaxe {
	private static final int[][] RELAY_POS = { { -1, 0, 0 }, { 1, 0, 0 },
			{ 0, -1, 0 }, { 0, 1, 0 }, { 0, 0, -1 }, { 0, 0, 1 } };

	private boolean enableOre;
	private boolean enableRedstoneOre;
	private boolean enableGrowstone;

	private List<Integer> targetBlockList;

	private double maxRange;

	private boolean enableEffect;

	private mod_WandaPowerPickaxe mod;

	public PowerPickaxe(int par1, int iconIndex,
			EnumToolMaterial par2EnumToolMaterial, mod_WandaPowerPickaxe mod) {
		super(par1, par2EnumToolMaterial);
		this.iconIndex = iconIndex;
		enableOre = enableRedstoneOre = enableGrowstone = true;
		this.mod = mod;
	}

	@Override
	public boolean onBlockDestroyed(ItemStack itemStack, World world, int blockID,
			int x, int y, int z, EntityLiving entityLiving) {
		if (!world.isRemote) {
			EntityPlayerMP player = (EntityPlayerMP) entityLiving;
			if (mod.isEnable(player.username)) {
				Block block = Block.blocksList[blockID];
				if (check(block, blockID)) {
					int count = relay(block, blockID, world, player, itemStack,
							x, y, z, x, y, z);
					itemStack.damageItem((int) (count * mod.getDamageRatio()),
							player);
				}
			}
		}

		return super.onBlockDestroyed(itemStack, world, blockID, x, y, z,
				entityLiving);
	}

	private int relay(Block target, int targetID, World world,
			EntityPlayerMP player, ItemStack itemStack, int x, int y, int z,
			int centerX, int centerY, int centerZ) {
		int ret = 0;
		for (int i = 0; i < PowerPickaxe.RELAY_POS.length; i++) {
			int currentX = x + RELAY_POS[i][0];
			int currentY = y + RELAY_POS[i][1];
			int currentZ = z + RELAY_POS[i][2];
			int blockID = world.getBlockId(currentX, currentY, currentZ);
			Block block = Block.blocksList[blockID];

			if (targetID == blockID
					|| (enableRedstoneOre && target instanceof BlockRedstoneOre && block instanceof BlockRedstoneOre)) {
				player.addExhaustion((float) mod.getExhaustionRatio());
				breakBlock(world, block, blockID, player, currentX, currentY,
						currentZ);

				if (itemStack.stackSize == 0) {
					break;
				}
				double normX = currentX - centerX;
				double normY = currentY - centerY;
				double normZ = currentZ - centerZ;
				double length = Math.sqrt((normX * normX) + (normY * normY)
						+ (normZ * normZ));
				if (maxRange <= 0 || length < maxRange) {
					ret += relay(target, targetID, world, player, itemStack,
							currentX, currentY, currentZ, centerX, centerY,
							centerZ);
				}
			}
		}
		return ret;
	}

	private void breakBlock(World world, Block block, int blockID,
			EntityPlayerMP player, int currentX, int currentY, int currentZ) {
		int metaData = world.getBlockMetadata(currentX, currentY, currentZ);
		if (enableEffect) {
			world.playAuxSFX(
					2001,
					currentX,
					currentY,
					currentZ,
					block.blockID
							+ (world.getBlockMetadata(currentX, currentY,
									currentZ) << 12));
		}
		block.harvestBlock(world, player, currentX, currentY, currentZ,
				metaData);
		world.setBlockWithNotify(currentX, currentY, currentZ, 0);
	}

	@Override
	public String getTextureFile() {
		return WandaResource.TEXTURE_TOOLS;
	}

	public void setEnableGrowstone(boolean enableGrowstone) {
		this.enableGrowstone = enableGrowstone;
	}

	public void setEnableOre(boolean enableOre) {
		this.enableOre = enableOre;
	}

	public void setEnableRedstoneOre(boolean enableRedstoneOre) {
		this.enableRedstoneOre = enableRedstoneOre;
	}

	public void setMaxRange(String range) {
		this.maxRange = Double.parseDouble(range);
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

	private boolean check(Block block, int blockID) {
		if (enableGrowstone && block instanceof BlockGlowStone) {
			return true;
		}

		if (enableRedstoneOre && block instanceof BlockRedstoneOre) {
			return true;
		}

		if (enableOre && block instanceof BlockOre) {
			boolean canHarvest = canHarvestBlock(block);
			return canHarvest;
		}

		if (targetBlockList != null && targetBlockList.contains(blockID)) {
			return true;
		}

		return false;
	}

	public void setEnableEffect(boolean enable) {
		this.enableEffect = enable;
	}
}
