package jp.wanda.minecraft.core.tileentity;

import jp.wanda.minecraft.core.BlockSide;
import jp.wanda.minecraft.core.WandaTileEntityData;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.World;

abstract public class WandaFacingData implements WandaTileEntityData {

	private byte face;

	@Override
	public String getName() {
		return "face";
	}

	@Override
	public byte[] getBinary() {
		return new byte[] { face };
	}

	@Override
	public void readBinary(byte[] binary) {
		face = binary[0];
	}

	public void setFace(byte face) {
		this.face = face;
	}

	public byte getFace() {
		return face;
	}

	abstract public void setDefaultDirection(World world, int x, int y, int z);

	abstract public void onBlockPlacedBy(World world, int x, int y, int z,
			EntityLiving entityliving);

	abstract public BlockSide getSide(BlockSide dir);

}
