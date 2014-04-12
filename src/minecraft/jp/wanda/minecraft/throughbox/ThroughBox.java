package jp.wanda.minecraft.throughbox;

import java.util.List;
import java.util.Random;

import jp.wanda.minecraft.core.BlockSide;
import jp.wanda.minecraft.core.WandaBlockContainerBase;
import jp.wanda.minecraft.res.WandaResource;
import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.Block;
import net.minecraft.src.BlockContainer;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.Material;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import net.minecraftforge.common.ForgeDirection;
import cpw.mods.fml.common.FMLLog;

public class ThroughBox extends WandaBlockContainerBase {

	private AxisAlignedBB aabb;

	public ThroughBox(int par1, Material par3Material) {
		super(par1, par3Material, ThroughBoxTileEntity.class);
		this.setTickRandomly(true);
		aabb = AxisAlignedBB.getBoundingBox(0, 0, 0, 0, 0, 0);
	}

	@Override
	public int getBlockTexture(IBlockAccess par1iBlockAccess, int x, int y,
			int z, int sideIndex) {
		int metaData = par1iBlockAccess.getBlockMetadata(x, y, z);
		if (sideIndex == 1) {
			return 2;
		}
		if (metaData == sideIndex) {
			return 4;
		} else {
			return 3;
		}
	}

	@Override
	public String getTextureFile() {
		return WandaResource.TEXTURE_BLOCK;
	}

	@Override
	public boolean onBlockActivated(World par1World, int par2, int par3,
			int par4, EntityPlayer par5EntityPlayer, int par6, float par7,
			float par8, float par9) {
		if (par6 != 1) {
			par1World.setBlockMetadataWithNotify(par2, par3, par4, par6);
		}
		return true;
	}

	@Override
	public int getTexture(BlockSide side, int metaData) {
		if (side.ordinal() == 1) {
			return 2;
		}
		if (metaData == side.ordinal()) {
			return 4;
		} else {
			return 3;
		}
	}

	@Override
	public boolean isBlockSolidOnSide(World world, int x, int y, int z,
			ForgeDirection side) {
		return false;
	}
}
