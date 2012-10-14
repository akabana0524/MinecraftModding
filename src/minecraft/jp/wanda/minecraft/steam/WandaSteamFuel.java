package jp.wanda.minecraft.steam;

import cpw.mods.fml.common.IFuelHandler;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;

public class WandaSteamFuel extends Item implements IFuelHandler {

	public static int globalShiftedIndex;
	public static int fuelGainEnergy;

	public static void addFuel(ItemStack itemStack) {
		NBTTagCompound tag = itemStack.getTagCompound();
		if (tag == null) {
			tag = new NBTTagCompound();
			itemStack.setTagCompound(tag);
		}
		WandaSteamFuelMachine machine = (WandaSteamFuelMachine) itemStack
				.getItem();
		tag.setInteger(
				"Fuel",
				Math.min(tag.getInteger("Fuel") + fuelGainEnergy,
						machine.getMaxFuel()));
	}

	public static void idlingFuelMachine(ItemStack itemStack) {
		NBTTagCompound tag = itemStack.getTagCompound();
		if (tag == null) {
			return;
		}
		int fuel = tag.getInteger("Fuel");
		tag.setInteger("Fuel", Math.max(fuel - 1, 0));
	}

	public static int getFuel(ItemStack itemStack) {
		NBTTagCompound tag = itemStack.getTagCompound();
		if (tag == null) {
			return 0;
		}
		return tag.getInteger("Fuel");
	}

	protected WandaSteamFuel(int par1) {
		super(par1);
		WandaSteamFuel.globalShiftedIndex = this.shiftedIndex;
		setIconIndex(0);
	}

	@Override
	public String getTextureFile() {
		return mod_WandaSteamCore.TEXTURE;
	}

	@Override
	public int getBurnTime(ItemStack fuel) {
		return 200 * 16;
	}

	@Override
	public CreativeTabs getCreativeTab() {
		return CreativeTabs.tabMisc;
	}
}
