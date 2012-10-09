package jp.wanda.minecraft.steam;

import cpw.mods.fml.common.IFuelHandler;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;

public class WandaSteamFuel extends Item implements IFuelHandler {

	protected WandaSteamFuel(int par1) {
		super(par1);
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
		return CreativeTabs.tabMaterials;
	}
}
