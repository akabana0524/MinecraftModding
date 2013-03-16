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
	public void func_94581_a(IconRegister par1IconRegister) {
		iconIndex = par1IconRegister
				.func_94245_a("WandaSteamCore:SteamEngineSmall");
	}
}
