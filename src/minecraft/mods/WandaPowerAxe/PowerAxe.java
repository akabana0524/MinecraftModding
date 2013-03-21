package mods.WandaPowerAxe;

import java.util.ArrayList;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

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
	}

	@Override
	public boolean onBlockDestroyed(ItemStack itemStack, World world,
			int blockID, int x, int y, int z, EntityLiving entityLiving) {
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

		boolean ret = super.onBlockDestroyed(itemStack, world, blockID, x, y,
				z, entityLiving);
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
						block.harvestBlock(world, player, currentX, currentY,
								currentZ, metaData);
						world.setBlock(currentX, currentY,
								currentZ, 0, 0, 3);
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

	@Override
	@SideOnly(Side.CLIENT)
	public void updateIcons(IconRegister par1IconRegister) {
		String path = null;
		switch (toolMaterial) {
		case WOOD:
			path = "WandaPowerAxe:PowerAxe00";
			break;
		case STONE:
			path = "WandaPowerAxe:PowerAxe01";
			break;
		case IRON:
			path = "WandaPowerAxe:PowerAxe02";
			break;
		case EMERALD:
			path = "WandaPowerAxe:PowerAxe03";
			break;
		case GOLD:
			path = "WandaPowerAxe:PowerAxe04";
			break;
		default:
			path = "WandaPowerAxe:PowerAxe00";
			break;
		}
		iconIndex = par1IconRegister.registerIcon(path);
	}
}
