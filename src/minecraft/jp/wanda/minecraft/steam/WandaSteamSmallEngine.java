package jp.wanda.minecraft.steam;

import net.minecraft.src.CreativeTabs;
import net.minecraft.src.Item;

public class WandaSteamSmallEngine extends Item {
	public static int grobalShiftedIndex;
	protected WandaSteamSmallEngine(int par1) {
		super(par1);
		grobalShiftedIndex = shiftedIndex;
		setIconCoord(0, 2);
	}

	@Override
	public String getTextureFile() {
		return mod_WandaSteamCore.TEXTURE;
	}

	@Override
	public CreativeTabs getCreativeTab() {
		return CreativeTabs.tabMisc;
	}

}
