package jp.wanda.minecraft.steam;

import cpw.mods.fml.common.IFuelHandler;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;

public class WandaSteamFuel extends Item implements IFuelHandler {

	private enum FuelType {
		DAMAGE, STACK_SIZE, NBT,
	}

	public static int globalShiftedIndex;
	public static int fuelGainEnergy;
	private static final FuelType FUEL_TYPE = FuelType.NBT;

	public static void addFuel(ItemStack itemStack) {
		switch (FUEL_TYPE) {
		case DAMAGE: {
			WandaSteamFuelMachine machine = (WandaSteamFuelMachine) itemStack
					.getItem();
			int current = itemStack.getItemDamage();
			int fuel = Math.min(current + fuelGainEnergy, machine.getMaxFuel());
			itemStack.setItemDamage(fuel);
		}
			break;
		case NBT: {
			NBTTagCompound tag = itemStack.getTagCompound();
			if (tag == null) {
				tag = new NBTTagCompound();
				itemStack.setTagCompound(tag);
			}
			WandaSteamFuelMachine machine = (WandaSteamFuelMachine) itemStack
					.getItem();
			int current = tag.getInteger("Fuel");
			int fuel = Math.min(current + fuelGainEnergy, machine.getMaxFuel());
			tag.setInteger("Fuel", fuel);
		}
			break;
		case STACK_SIZE: {
			WandaSteamFuelMachine machine = (WandaSteamFuelMachine) itemStack
					.getItem();
			int current = itemStack.stackSize;
			int fuel = Math.min(current + fuelGainEnergy, machine.getMaxFuel());
			itemStack.stackSize = fuel;
		}
			break;
		}
	}

	public static void idlingFuelMachine(ItemStack itemStack) {
		switch (FUEL_TYPE) {
		case DAMAGE: {
			WandaSteamFuelMachine machine = (WandaSteamFuelMachine) itemStack
					.getItem();
			int current = itemStack.getItemDamage();
			int fuel = Math.max(current - 1, 0);
			itemStack.setItemDamage(fuel);
		}
			break;
		case NBT: {
			NBTTagCompound tag = itemStack.getTagCompound();
			if (tag == null) {
				return;
			}
			WandaSteamFuelMachine machine = (WandaSteamFuelMachine) itemStack
					.getItem();
			int current = tag.getInteger("Fuel");
			int fuel = Math.max(current - 1, 0);
			tag.setInteger("Fuel", fuel);
		}
			break;
		case STACK_SIZE: {
			WandaSteamFuelMachine machine = (WandaSteamFuelMachine) itemStack
					.getItem();
			int current = itemStack.stackSize;
			int fuel = Math.max(current - 1, 1);
			itemStack.stackSize = fuel;
		}
			break;
		}
	}

	public static int getFuel(ItemStack itemStack) {
		switch (FUEL_TYPE) {
		case DAMAGE: {
			return itemStack.getItemDamage();
		}
		case NBT: {
			NBTTagCompound tag = itemStack.getTagCompound();
			if (tag == null) {
				return 0;
			}
			return tag.getInteger("Fuel");
		}
		case STACK_SIZE: {
			return itemStack.stackSize;
		}
		}
		return 0;
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
