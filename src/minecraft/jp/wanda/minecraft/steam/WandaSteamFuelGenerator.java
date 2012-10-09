package jp.wanda.minecraft.steam;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import jp.wanda.minecraft.core.BlockSide;
import jp.wanda.minecraft.core.WandaBlockContainerBase;
import jp.wanda.minecraft.core.WandaContainerBase;
import jp.wanda.minecraft.core.WandaFacingTileEntity;
import jp.wanda.minecraft.core.WandaGuiContainerBase;
import jp.wanda.minecraft.core.WandaInventoryBlock;
import jp.wanda.minecraft.core.WandaTileEntityBase;

import org.lwjgl.opengl.GL11;

import samples.containersamplemod.CommonProxy;
import samples.tileentitysamplemod.TileEntityNoop;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.src.Block;
import net.minecraft.src.BlockContainer;
import net.minecraft.src.Container;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Gui;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.ICrafting;
import net.minecraft.src.IInventory;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Material;
import net.minecraft.src.MathHelper;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.Packet;
import net.minecraft.src.Slot;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;

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
			addExtraInventory(new WandaInventoryBlock("Fuel", 1, 1, 26, 35));
			addExtraInventory(new WandaInventoryBlock("Material", 2, 2, 71, 26));
			addExtraInventory(new WandaInventoryBlock("Output", 1, 1, 134, 35));
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

	public static class GeneratorTileEntity extends WandaTileEntityBase
			implements WandaFacingTileEntity {

		private byte face;

		@Override
		public int getTileEntityVersion() {
			return 0;
		}

		@Override
		public void updateEntity() {
			super.updateEntity();
		}

		public void setFacing(byte face) {
			this.face = face;
		}

		public int getFacing() {
			return face;
		}

		@Override
		public void writeToNBT(NBTTagCompound nbt) {
			super.writeToNBT(nbt);
			nbt.setByte("Face", face);
		}

		@Override
		public void readFromNBT(NBTTagCompound nbt) {
			super.readFromNBT(nbt);
			face = nbt.getByte("Face");
		}

		@Override
		public byte[] getAuxillaryInfoPacketData() {
			return new byte[] { face };
		}

		@Override
		public void setAuxillaryInfoPacketData(DataInputStream din)
				throws IOException {
			face = din.readByte();
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
