package mods.WandaCrowBar;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.util.Icon;
import net.minecraft.world.World;

public class Crowbar extends ItemTool {
	private List<Block> targetBlock;

	public Crowbar(int itemID, int damageVsEntity, EnumToolMaterial material,
			Block[] blocksEffectiveAgainst) {
		super(itemID, 4, material, blocksEffectiveAgainst);
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
				world.setBlock(x, y, z, 0, 0, 3);
			}
		}

		return super.onItemUse(itemStack, entityPlayer, world, x, y, z, side,
				clickPosX, clickPosY, clickPosZ);
	}

	@Override
	public boolean canHarvestBlock(Block par1Block) {
		return targetBlock.contains(par1Block);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateIcons(IconRegister par1IconRegister) {
		String path = null;
		switch (toolMaterial) {
		case IRON:
			path = "WandaCrowBar:CrowBar00";
			break;
		case EMERALD:
			path = "WandaCrowBar:CrowBar01";
			break;
		case GOLD:
			path = "WandaCrowBar:CrowBar02";
			break;
		default:
			path = "WandaCrowBar:CrowBar00";
			break;
		}
		iconIndex = par1IconRegister.registerIcon(path);
	}
}
