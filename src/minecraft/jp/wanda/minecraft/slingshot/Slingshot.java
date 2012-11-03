package jp.wanda.minecraft.slingshot;

import jp.wanda.minecraft.res.WandaResource;
import net.minecraft.src.Block;
import net.minecraft.src.Enchantment;
import net.minecraft.src.EnchantmentHelper;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemBow;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;

public class Slingshot extends ItemBow {

	private static final int[] animation = { 1, 2, 3 };

	protected Slingshot(int par1) {
		super(par1);
		iconIndex = 0;
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

			EntitySlingshotBullet var8 = new EntitySlingshotBullet(world,
					entityPlayer, var7 * 2.0F);

			if (EnchantmentHelper.getEnchantmentLevel(
					Enchantment.flame.effectId, itemStack) > 0) {
				var8.setFire(100);
			}
			if (var7 == 1.0F) {
				var8.func_70243_d(true);
			}

			int var9 = EnchantmentHelper.getEnchantmentLevel(
					Enchantment.power.effectId, itemStack);

			if (var9 > 0) {
				var8.setDamage(var8.getDamage() + (double) var9 * 0.5D + 0.5D);
			}

			int var10 = EnchantmentHelper.getEnchantmentLevel(
					Enchantment.punch.effectId, itemStack);

			if (var10 > 0) {
				var8.setKnockbackStrength(var10);
			}

			if (EnchantmentHelper.getEnchantmentLevel(
					Enchantment.flame.effectId, itemStack) > 0) {
				var8.setFire(100);
			}

			itemStack.damageItem(1, entityPlayer);
			world.playSoundAtEntity(entityPlayer, "random.bow", 1.0F, 1.0F
					/ (itemRand.nextFloat() * 0.4F + 1.2F) + var7 * 0.5F);

			if (infinity) {
			} else {
				entityPlayer.inventory
						.consumeInventoryItem(Block.cobblestone.blockID);
			}

			if (!world.isRemote) {
				world.spawnEntityInWorld(var8);
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
		if (usingItem != null
				&& usingItem.getItem().shiftedIndex == this.shiftedIndex) {
			int k = usingItem.getMaxItemUseDuration() - useRemaining;
			if (k >= 18)
				return animation[2];
			if (k > 13)
				return animation[1];
			if (k > 0)
				return animation[0];
		}
		return getIconIndex(stack);
	}
}
