package jp.wanda.minecraft.core;

public interface WandaTileEntityData {

	public String getName();

	public byte[] getBinary();

	public void readBinary(byte[] binary);
}
