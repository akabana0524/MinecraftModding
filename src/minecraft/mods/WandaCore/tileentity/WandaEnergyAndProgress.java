package mods.WandaCore.tileentity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import mods.WandaCore.WandaTileEntityData;

public class WandaEnergyAndProgress implements WandaTileEntityData {

	private int energy;
	private int progress;

	@Override
	public String getName() {
		return "WnadaN&P";
	}

	@Override
	public byte[] getBinary() {
		byte[] ret = null;
		ByteArrayOutputStream bout = null;
		DataOutputStream dos = null;
		try {
			bout = new ByteArrayOutputStream();
			dos = new DataOutputStream(bout);
			dos.writeInt(energy);
			dos.writeInt(progress);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bout != null) {
				try {
					bout.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				ret = bout.toByteArray();
			}
		}
		return ret;
	}

	@Override
	public void readBinary(byte[] binary) {
		ByteArrayInputStream bin = null;
		DataInputStream dis = null;
		try {
			bin = new ByteArrayInputStream(binary);
			dis = new DataInputStream(bin);
			energy = dis.readInt();
			progress = dis.readInt();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bin != null) {
				try {
					bin.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public int getEnergy() {
		return energy;
	}

	public int getProcCount() {
		return progress;
	}

	public void setEnergy(int energy) {
		this.energy = energy;
	}

	public void setProcCount(int progress) {
		this.progress = progress;
	}

}
