package jp.wanda.minecraft.crowbar;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.src.Block;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.EnumToolMaterial;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ItemTool;
import net.minecraft.src.World;

public class Crowbar extends ItemTool {
	private List<Block> targetBlock;

	public Crowbar(int itemID, int iconIndex, int damageVsEntity,
			EnumToolMaterial material, Block[] blocksEffectiveAgainst) {
		super(itemID, 4, material, blocksEffectiveAgainst);
		this.iconIndex = iconIndex;
		efficiencyOnProperMaterial = 100;
		this.targetBlock = new ArrayList<Block>();
		for (Block b : blocksEffectiveAgainst) {
			targetBlock.add(b);
		}
	}

	@Override
	public boolean onItemUse(ItemStack itemStack, EntityPlayer entityPlayer,
			World world, int x, int y, int z, int side, float clickPosX,
			float clickPosY, float clickPosZ) {
		Block block = Block.blocksList[world.getBlockId(x, y, z)];
		float check = getStrVsBlock(itemStack, block);

		if (check > 1.0f) {
			entityPlayer.swingItem();

			if (!world.isRemote) {
				int metaData = world.getBlockMetadata(x, y, z);
				EntityPlayerMP player = (EntityPlayerMP) entityPlayer;
				itemStack.damageItem(1, player);
				world.playAuxSFX(2001, x, y, z,
						block.blockID + (world.getBlockMetadata(x, y, z) << 12));
				block.harvestBlock(world, player, x, y, z, metaData);
				world.setBlockWithNotify(x, y, z, 0);
			}
		}

		return super.onItemUse(itemStack, entityPlayer, world, x, y, z,
				side, clickPosX, clickPosY, clickPosZ);
	}

	@Override
	public String getTextureFile() {
		return "/WandaResource/WandaTools.png";
	}

	@Override
	public boolean canHarvestBlock(Block par1Block) {
		return targetBlock.contains(par1Block);
	}
}
