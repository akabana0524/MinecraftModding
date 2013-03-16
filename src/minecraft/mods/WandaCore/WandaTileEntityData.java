package mods.WandaCore;

public interface WandaTileEntityData {

	public String getName();

	public byte[] getBinary();

	public void readBinary(byte[] binary);
}
