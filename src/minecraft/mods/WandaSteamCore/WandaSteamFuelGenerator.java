package mods.WandaSteamCore;

import java.util.Random;

import mods.WandaCore.BlockSide;
import mods.WandaCore.WandaBlockContainerBase;
import mods.WandaCore.WandaContainerBase;
import mods.WandaCore.WandaGuiContainerBase;
import mods.WandaCore.WandaInventoryGroup;
import mods.WandaCore.WandaTileEntityBase;
import mods.WandaCore.tileentity.WandaEnergyAndProgress;
import mods.WandaCore.tileentity.WandaFacing6Face;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class WandaSteamFuelGenerator extends WandaBlockContainerBase {
	public static final String GUI_TEXTURE = "/mods/WandaSteamCore/GuiWandaSteamFuelGenerator.png";

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
		private GeneratorTileEntity generatorTileEntity;

		public GeneratorGUI(WandaContainerBase container, EntityPlayer player,
				World world, int x, int y, int z) {
			super(container, player, world, x, y, z, GUI_TEXTURE);
			generatorTileEntity = (GeneratorTileEntity) world
					.getBlockTileEntity(x, y, z);
		}

		@Override
		protected void drawGuiContainerForegroundLayer(int par1, int par2) {
			fontRenderer.drawString(
					"Energy:" + generatorTileEntity.ep.getEnergy(), 8, 6,
					0x404040);
			fontRenderer.drawString(
					"ProcCnt:" + generatorTileEntity.ep.getProcCount(), 8, 20,
					0x404040);
		}

		@Override
		public void drawScreen(int par1, int par2, float par3) {
			super.drawScreen(par1, par2, par3);
		}
	}

	private static class GeneratorContainer extends WandaContainerBase {
		public GeneratorContainer(EntityPlayer player, World world, int x,
				int y, int z) {
			super(player, world, x, y, z);
		}

		@Override
		protected boolean hasTileEntity() {
			return true;
		}

	}

	public static class GeneratorTileEntity extends WandaTileEntityBase {

		private WandaFacing6Face face;
		private WandaEnergyAndProgress ep;
		private WandaInventoryGroup fuel;
		private WandaInventoryGroup material;
		private WandaInventoryGroup output;
		private BlockSide waterSide;
		private boolean active;

		private static final BlockSide[] CHECK_SIDE = { BlockSide.BOTTOM,
				BlockSide.BACK, BlockSide.LEFT, BlockSide.RIGHT,
				BlockSide.FRONT, BlockSide.TOP, };
		private static final int NEED_PROC_COUNT = 20 * 10;

		public GeneratorTileEntity() {
			registTileEntityData(fuel = new WandaInventoryGroup("Fuel", 1, 1,
					26, 35, false, true));
			registTileEntityData(material = new WandaInventoryGroup("Material",
					2, 2, 71, 26, false, true));
			registTileEntityData(output = new WandaInventoryGroup("Output", 1,
					1, 134, 35, false, false));
			registTileEntityData(face = new WandaFacing6Face());
			registTileEntityData(ep = new WandaEnergyAndProgress());
		}

		@Override
		public int getTileEntityVersion() {
			return 0;
		}

		@Override
		public void updateEntity() {
			super.updateEntity();
			waterSide = checkWater();
			if (checkMaterial() && checkOutput()) {
				processGenerate();
			}
			if (ep.getEnergy() == 0) {
				active = false;
			} else {
				active = true;
				ep.setEnergy(ep.getEnergy() - 1);
			}
		}

		private boolean checkOutput() {
			ItemStack itemStack = output.getStackInSlot(0);
			if (itemStack == null) {
				return true;
			}
			if (itemStack.itemID != WandaSteamFuel.globalItemID) {
				return false;
			}
			return itemStack.stackSize < itemStack.getMaxStackSize();
		}

		private void processGenerate() {
			if (ep.getEnergy() == 0 && fuel.containsItem(Item.coal)) {
				ItemStack itemStack = new ItemStack(Item.coal.itemID, 1, 0);
				fuel.removeItem(itemStack);
				ep.setEnergy(ep.getEnergy()
						+ GameRegistry.getFuelValue(itemStack));
			}
			if (ep.getEnergy() == 0) {
				return;
			}
			active = true;
			ep.setProcCount(ep.getProcCount() + 1);
			if (ep.getProcCount() >= NEED_PROC_COUNT) {
				ep.setProcCount(ep.getProcCount() - NEED_PROC_COUNT);
				if (material.removeItem(new ItemStack(Item.bucketWater.itemID,
						1, 0))) {
					material.addItem(new ItemStack(Item.bucketEmpty.itemID, 1,
							0));
				}
				material.removeItem(new ItemStack(Item.gunpowder.itemID, 1, 0));
				material.removeItem(new ItemStack(Item.coal.itemID, 1, 0));
				material.removeItem(new ItemStack(Item.slimeBall.itemID, 1, 0));
				output.addItem(new ItemStack(WandaSteamFuel.globalItemID, 1, 0));

			}
		}

		private boolean checkMaterial() {
			if (waterSide == null) {
				if (!material.containsItem(Item.bucketWater)) {
					return false;
				}
			}
			if (!material.containsItem(Item.coal)) {
				return false;
			}
			if (!material.containsItem(Item.gunpowder)) {
				return false;
			}
			if (!material.containsItem(Item.slimeBall)) {
				return false;
			}
			return true;
		}

		private BlockSide checkWater() {
			for (BlockSide check : CHECK_SIDE) {
				if (Block.waterStill.blockID == face.getSideBlockId(check,
						worldObj, xCoord, yCoord, zCoord)) {
					return check;
				}
			}
			return null;
		}

	}

	private mod_WandaSteamCore core;
	private int guiid;

	private boolean isActive;

	private Icon[] icons;

	public WandaSteamFuelGenerator(int par1, Material par3Material,
			mod_WandaSteamCore core, int guiid) {
		super(par1, par3Material, GeneratorTileEntity.class);
		this.core = core;
		this.guiid = guiid;
		setCreativeTab(CreativeTabs.tabMisc);
		setHardness(3.5F);
		setStepSound(soundMetalFootstep);
		// setRequiresSelfNotify();
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
	public Icon getTexture(BlockSide side, int metaData) {
		return icons[side.ordinal()];
	}

	@Override
	public void randomDisplayTick(World par1World, int par2, int par3,
			int par4, Random par5Random) {
		super.randomDisplayTick(par1World, par2, par3, par4, par5Random);
		GeneratorTileEntity tileEntity = ((GeneratorTileEntity) par1World
				.getBlockTileEntity(par2, par3, par4));
		if (tileEntity.active) {
			int var6 = tileEntity.face.getFace();
			float var7 = (float) par2 + 0.5F;
			float var8 = (float) par3 + 0.0F + par5Random.nextFloat() * 6.0F
					/ 16.0F;
			float var9 = (float) par4 + 0.5F;
			float var10 = 0.52F;
			float var11 = par5Random.nextFloat() * 0.6F - 0.3F;

			if (var6 == 4) {
				par1World.spawnParticle("smoke", (double) (var7 - var10),
						(double) var8, (double) (var9 + var11), 0.0D, 0.0D,
						0.0D);
				par1World.spawnParticle("flame", (double) (var7 - var10),
						(double) var8, (double) (var9 + var11), 0.0D, 0.0D,
						0.0D);
			} else if (var6 == 5) {
				par1World.spawnParticle("smoke", (double) (var7 + var10),
						(double) var8, (double) (var9 + var11), 0.0D, 0.0D,
						0.0D);
				par1World.spawnParticle("flame", (double) (var7 + var10),
						(double) var8, (double) (var9 + var11), 0.0D, 0.0D,
						0.0D);
			} else if (var6 == 2) {
				par1World.spawnParticle("smoke", (double) (var7 + var11),
						(double) var8, (double) (var9 - var10), 0.0D, 0.0D,
						0.0D);
				par1World.spawnParticle("flame", (double) (var7 + var11),
						(double) var8, (double) (var9 - var10), 0.0D, 0.0D,
						0.0D);
			} else if (var6 == 3) {
				par1World.spawnParticle("smoke", (double) (var7 + var11),
						(double) var8, (double) (var9 + var10), 0.0D, 0.0D,
						0.0D);
				par1World.spawnParticle("flame", (double) (var7 + var11),
						(double) var8, (double) (var9 + var10), 0.0D, 0.0D,
						0.0D);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	/**
	 * When this method is called, your block should register all the icons it needs with the given IconRegister. This is
	 * the only chance you get to register icons.
	 */
	public void registerIcons(IconRegister par1IconRegister) {
		icons = new Icon[6];
		for (int i = 0; i < icons.length; i++) {
			icons[i] = par1IconRegister
					.registerIcon("WandaSteamCore:CoalRefined0" + i);
		}
		this.blockIcon = icons[0];
	}
}
