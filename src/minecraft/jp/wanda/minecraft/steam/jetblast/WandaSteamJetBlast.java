package jp.wanda.minecraft.steam.jetblast;

import jp.wanda.minecraft.res.WandaResource;
import jp.wanda.minecraft.slingshot.EntitySlingshotBullet;
import net.minecraft.src.Block;
import net.minecraft.src.Enchantment;
import net.minecraft.src.EnchantmentHelper;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EnumToolMaterial;
import net.minecraft.src.ItemBow;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ItemTool;
import net.minecraft.src.World;

public class WandaSteamJetBlast extends ItemBow {

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
						world, entityPlayer, posX + motionX * (double) var21 / 4.0D,
						posY + motionY * (double) var21 / 4.0D, posZ + motionZ
								* (double) var21 / 4.0D, -motionX,
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

	@Override
	public String getTextureFile() {
		return WandaResource.TEXTURE_ITEMS;
	}

	@Override
	public int getIconIndex(ItemStack stack, int renderPass,
			EntityPlayer player, ItemStack usingItem, int useRemaining) {
		int var4 = stack.getMaxItemUseDuration() - player.getItemInUseCount();

		if (var4 >= 18) {
			return 3;
		}

		if (var4 > 13) {
			return 2;
		}

		if (var4 > 0) {
			return 1;
		}
		return super.getIconIndex(stack, renderPass, player, usingItem,
				useRemaining);
	}

}
