package mods.WandaSteamJetBlast;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.World;

public class WandaSteamJetBlast extends ItemBow {

	private static final String[] ICON_PATH = { "WandaSlingShot:SlingShot00",
			"WandaSlingShot:SlingShot01", "WandaSlingShot:SlingShot02",
			"WandaSlingShot:SlingShot03" };
	private Icon[] icons;

	public WandaSteamJetBlast(int par1) {
		super(par1);
	}

	public void onPlayerStoppedUsing(ItemStack itemStack, World world,
			EntityPlayer entityPlayer, int duration) {
		int power = this.getMaxItemUseDuration(itemStack) - duration;

		boolean infinity = entityPlayer.capabilities.isCreativeMode
				|| EnchantmentHelper.getEnchantmentLevel(
						Enchantment.infinity.effectId, itemStack) > 0;

		if (infinity
				|| entityPlayer.inventory.hasItem(Block.cobblestone.blockID)) {
			float var7 = (float) power / 20.0F;
			var7 = (var7 * var7 + var7 * 2.0F) / 3.0F;

			if ((double) var7 < 0.2D) {
				return;
			}

			if (var7 > 1.0F) {
				var7 = 1.0F;
			}

			double posX = 0;
			double posY = 0;
			double posZ = 0;
			double motionX = 0;
			double motionY = 0;
			double motionZ = 0;

			for (int var21 = 0; var21 < 50; var21++) {
				WandaSteamJetBlastBullet var8 = new WandaSteamJetBlastBullet(
						world, entityPlayer, posX + motionX * (double) var21
								/ 4.0D, posY + motionY * (double) var21 / 4.0D,
						posZ + motionZ * (double) var21 / 4.0D, -motionX,
						-motionY + 0.2D, -motionZ);

				itemStack.damageItem(1, entityPlayer);
				world.playSoundAtEntity(entityPlayer, "random.bow", 1.0F, 1.0F
						/ (itemRand.nextFloat() * 0.4F + 1.2F) + var7 * 0.5F);
				if (infinity) {
				} else {
				}
				if (!world.isRemote) {
					world.spawnEntityInWorld(var8);
				}
			}
		}
	}

	@Override
	public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World,
			EntityPlayer par3EntityPlayer) {
		if (par3EntityPlayer.capabilities.isCreativeMode
				|| par3EntityPlayer.inventory
						.hasItem(Block.cobblestone.blockID)) {
			par3EntityPlayer.setItemInUse(par1ItemStack,
					this.getMaxItemUseDuration(par1ItemStack));
		}

		return par1ItemStack;
	}

	@SideOnly(Side.CLIENT)
	public void updateIcons(IconRegister par1IconRegister) {
		iconIndex = par1IconRegister.registerIcon(ICON_PATH[0]);
		icons = new Icon[ICON_PATH.length];
		for (int i = 0; i < this.icons.length; ++i) {
			icons[i] = par1IconRegister.registerIcon(ICON_PATH[i]);
		}
	}

	@Override
	public Icon getIcon(ItemStack stack, int renderPass, EntityPlayer player,
			ItemStack usingItem, int useRemaining) {
		int j = stack.getMaxItemUseDuration() - useRemaining;
		if (j >= 18) {
			return icons[3];
		}
		if (j > 13) {
			return icons[2];
		}
		if (j > 8) {
			return icons[1];
		}
		if (j > 0) {
			return icons[0];
		}
		return super
				.getIcon(stack, renderPass, player, usingItem, useRemaining);
	}
}
