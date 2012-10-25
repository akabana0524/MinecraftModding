package jp.wanda.minecraft.ai;

import java.util.Random;

import jp.wanda.minecraft.core.WandaContainerBase;
import jp.wanda.minecraft.core.WandaGuiContainerBase;
import jp.wanda.minecraft.core.WandaInventoryGroup;
import jp.wanda.minecraft.steam.mod_WandaSteamCore;
import net.minecraft.src.Block;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerSP;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Material;
import net.minecraft.src.Slot;
import net.minecraft.src.World;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.IGuiHandler;

public class WandaAIWorkbench extends Block {

	public static final String GUI_TEXTURE = "/jp/wanda/minecraft/ai/GuiWandaAIWorkbench.png";

	public static IGuiHandler GUI = new GuiAIWorkbench();

	public static class GuiAIWorkbench implements IGuiHandler {
		@Override
		public Object getServerGuiElement(int ID, EntityPlayer player,
				World world, int x, int y, int z) {
			if (ID == mod_WandaAI.GUI_WandaAIWorkbench) {
				return new ContainerAIWorkbench(player, world, x, y, z);
			}
			return null;
		}

		@Override
		public Object getClientGuiElement(int ID, EntityPlayer player,
				World world, int x, int y, int z) {
			if (ID == mod_WandaAI.GUI_WandaAIWorkbench) {
				return new GuiContainerAIWorkbench(new ContainerAIWorkbench(
						player, world, x, y, z), player, world, x, y, z,
						GUI_TEXTURE);
			}
			return null;
		}
	}

	private static class ContainerAIWorkbench extends WandaContainerBase {
		public ContainerAIWorkbench(EntityPlayer player, World world, int x,
				int y, int z) {
			super(player, world, x, y, z, 8, 118);
		}

		@Override
		protected void setupExtraInventory() {
			super.setupExtraInventory();
			addExtraInventory(new WandaInventoryGroup("Craft", 5, 5, 26, 18,
					true, true));
			addExtraInventory(new WandaInventoryGroup("Output", 1, 1, 134, 54,
					false, false));
		}

		@Override
		protected boolean hasTileEntity() {
			return false;
		}

	}

	private static class GuiContainerAIWorkbench extends WandaGuiContainerBase {

		public GuiContainerAIWorkbench(WandaContainerBase container,
				EntityPlayer player, World world, int x, int y, int z,
				String texture) {
			super(container, player, world, x, y, z, texture);
			xSize = 176;
			ySize = 200;
		}
	}

	private mod_WandaAI mod;
	private int guiID;

	protected WandaAIWorkbench(mod_WandaAI mod, int guiID, int blockID,
			Material material) {
		super(blockID, material);
		this.mod = mod;
		this.guiID = guiID;
		setCreativeTab(CreativeTabs.tabMisc);
		setTextureFile(mod_WandaSteamCore.TEXTURE);
		setHardness(3.5F);
		setStepSound(soundMetalFootstep);
		setRequiresSelfNotify();
	}

	@Override
	public boolean onBlockActivated(World par1World, int par2, int par3,
			int par4, EntityPlayer par5EntityPlayer, int par6, float par7,
			float par8, float par9) {
		if (!par1World.isRemote) {
			par5EntityPlayer.openGui(mod, guiID, par1World, par2, par3, par4);
		}
		return true;
	}

	/**
	 * Returns the ID of the items to drop on destruction.
	 */
	public int idDropped(int par1, Random par2Random, int par3) {
		return blockID;
	}

}
