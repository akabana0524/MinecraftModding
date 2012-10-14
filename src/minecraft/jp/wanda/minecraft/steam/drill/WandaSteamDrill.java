package jp.wanda.minecraft.steam.drill;

import jp.wanda.minecraft.steam.WandaSteamFuel;
import jp.wanda.minecraft.steam.WandaSteamFuelMachine;
import jp.wanda.minecraft.steam.mod_WandaSteamCore;
import net.minecraft.src.Block;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EnumToolMaterial;
import net.minecraft.src.ItemPickaxe;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.World;

public class WandaSteamDrill extends ItemPickaxe implements
		WandaSteamFuelMachine {

	private int maxFuel;

	protected WandaSteamDrill(int itemID, int coordRow, int coordColumn,
			EnumToolMaterial toolMaterial, int maxFuel) {
		super(itemID, toolMaterial);
		setIconCoord(coordRow, coordColumn);
		setMaxDamage(0);
		this.maxFuel = maxFuel;
		canRepair = false;
	}

	@Override
	public void onCreated(ItemStack par1ItemStack, World par2World,
			EntityPlayer par3EntityPlayer) {
		super.onCreated(par1ItemStack, par2World, par3EntityPlayer);
	}

	@Override
	public boolean getShareTag() {
		return true;
	}

	@Override
	public String getTextureFile() {
		return mod_WandaSteamCore.TEXTURE;
	}

	@Override
	public int getDamageVsEntity(Entity par1Entity) {
		return super.getDamageVsEntity(par1Entity) * 2;
	}

	public float getStrVsBlock(ItemStack par1ItemStack, Block par2Block) {
		if (isEnoughFuel(par1ItemStack)) {
			return super.getStrVsBlock(par1ItemStack, par2Block);
		} else {
			return 0;
		}
	}

	@Override
	public boolean func_77660_a(ItemStack itemStack, World world, int par3,
			int par4, int par5, int par6, EntityLiving par7EntityLiving) {
		if (isEnoughFuel(itemStack)) {
			return super.func_77660_a(itemStack, world, par3, par4, par5, par6,
					par7EntityLiving);
		}
		return false;
	}

	private boolean isEnoughFuel(ItemStack par1ItemStack) {
		return WandaSteamFuel.getFuel(par1ItemStack) > 0;
	}

	@Override
	public void onUpdate(ItemStack itemStack, World world, Entity entity,
			int par4, boolean par5) {
		EntityPlayer player = (EntityPlayer) entity;
		if (!player.capabilities.isCreativeMode) {
			 WandaSteamFuel.idlingFuelMachine(itemStack);
		}
		super.onUpdate(itemStack, world, entity, par4, par5);
	}

	@Override
	public int getMaxFuel() {
		return maxFuel;
	}
}
