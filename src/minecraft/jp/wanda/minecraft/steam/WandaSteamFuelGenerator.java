package jp.wanda.minecraft.steam;

import java.util.Random;

import jp.wanda.minecraft.core.BlockSide;
import jp.wanda.minecraft.core.WandaBlockContainerBase;
import jp.wanda.minecraft.core.WandaContainerBase;
import jp.wanda.minecraft.core.WandaGuiContainerBase;
import jp.wanda.minecraft.core.WandaInventoryGroup;
import jp.wanda.minecraft.core.WandaTileEntityBase;
import jp.wanda.minecraft.core.tileentity.WandaFacing6Face;
import jp.wanda.minecraft.core.tileentity.WandaFacingData;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICrafting;
import net.minecraft.src.Material;
import net.minecraft.src.World;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.network.IGuiHandler;

public class WandaSteamFuelGenerator extends WandaBlockContainerBase {
	public static final String GUI_TEXTURE = "/jp/wanda/minecraft/steam/GuiWandaSteamFuelGenerator.png";

	public static GuiFuelGenerator GUI = new GuiFuelGenerator();

	public static class GuiFuelGenerator implements IGuiHandler {
		@Override
		public Object getServerGuiElement(int ID, EntityPlayer player,
				World world, int x, int y, int z) {
			if (ID == mod_WandaSteamCore.GUI_GENERATOR) {
				return new GeneratorContainer(player, world, x, y, z);
			}
			return null;
		}

		@Override
		public Object getClientGuiElement(int ID, EntityPlayer player,
				World world, int x, int y, int z) {
			if (ID == mod_WandaSteamCore.GUI_GENERATOR) {
				return new GeneratorGUI(new GeneratorContainer(player, world,
						x, y, z), player, world, x, y, z);
			}
			return null;
		}
	}

	private static class GeneratorGUI extends WandaGuiContainerBase {

		public GeneratorGUI(WandaContainerBase container, EntityPlayer player,
				World world, int x, int y, int z) {
			super(container, player, world, x, y, z, GUI_TEXTURE);
		}
	}

	private static class GeneratorContainer extends WandaContainerBase {
		public GeneratorContainer(EntityPlayer player, World world, int x,
				int y, int z) {
			super(player, world, x, y, z);
		}

		@Override
		protected void setupExtraInventory() {
		}

		@Override
		public void updateCraftingResults() {
			super.updateCraftingResults();
		}

		@Override
		public void updateProgressBar(int par1, int par2) {
			super.updateProgressBar(par1, par2);
			FMLLog.info("updateProgressBar(" + par1 + "," + par2 + ")");
		}

		@Override
		protected boolean hasTileEntity() {
			return true;
		}

		@Override
		protected int getInventoryBlockX(int index) {
			return 0;
		}

		@Override
		protected int getInventoryBlockY(int index) {
			return 0;
		}

		@Override
		public void addCraftingToCrafters(ICrafting par1iCrafting) {
			super.addCraftingToCrafters(par1iCrafting);
		}

	}

	public static class GeneratorTileEntity extends WandaTileEntityBase {

		private WandaFacingData face;

		public GeneratorTileEntity() {
			face = new WandaFacing6Face();
			registTileEntityData(new WandaInventoryGroup("Fuel", 1, 1, 26, 35,
					false));
			registTileEntityData(new WandaInventoryGroup("Material", 2, 2, 71,
					26, false));
			registTileEntityData(new WandaInventoryGroup("Output", 1, 1, 134,
					35, false));
			registTileEntityData(face);
		}

		@Override
		public int getTileEntityVersion() {
			return 0;
		}

		@Override
		public void updateEntity() {
			super.updateEntity();
		}

		@Override
		public String getChannel() {
			return "WandaSteamFG";
		}

	}

	private mod_WandaSteamCore core;
	private int guiid;

	public WandaSteamFuelGenerator(int par1, Material par3Material,
			mod_WandaSteamCore core, int guiid) {
		super(par1, par3Material, GeneratorTileEntity.class);
		this.core = core;
		this.guiid = guiid;
		setCreativeTab(CreativeTabs.tabBrewing);
		setTextureFile(mod_WandaSteamCore.TEXTURE);
		setHardness(3.5F);
		setStepSound(soundMetalFootstep);
		setRequiresSelfNotify();
	}

	/**
	 * Returns the ID of the items to drop on destruction.
	 */
	public int idDropped(int par1, Random par2Random, int par3) {
		return blockID;
	}

	@Override
	public boolean onBlockActivated(World par1World, int par2, int par3,
			int par4, EntityPlayer par5EntityPlayer, int par6, float par7,
			float par8, float par9) {
		if (!par1World.isRemote) {
			par5EntityPlayer.openGui(core, guiid, par1World, par2, par3, par4);
		}
		return true;
	}

	@Override
	public int getTexture(BlockSide side, int metaData) {
		return 16 + side.ordinal();
	}

}
