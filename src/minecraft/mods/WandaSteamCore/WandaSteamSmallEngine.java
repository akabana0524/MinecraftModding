package mods.WandaSteamCore;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class WandaSteamSmallEngine extends Item {
	public static int grobalItemID = 0;

	protected WandaSteamSmallEngine(int par1) {
		super(par1);
		grobalItemID = itemID;
	}

	@Override
	public CreativeTabs getCreativeTab() {
		return CreativeTabs.tabMisc;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateIcons(IconRegister par1IconRegister) {
		iconIndex = par1IconRegister
				.registerIcon("WandaSteamCore:SteamEngineSmall");
	}
}
