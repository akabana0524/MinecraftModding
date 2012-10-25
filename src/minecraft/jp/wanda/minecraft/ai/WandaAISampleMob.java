package jp.wanda.minecraft.ai;

import net.minecraft.src.EntityCreature;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EnumCreatureAttribute;
import net.minecraft.src.INpc;
import net.minecraft.src.Item;
import net.minecraft.src.World;

public class WandaAISampleMob extends EntityCreature implements INpc {

	private mod_WandaAI mod;
	private int modGuiId;

	public WandaAISampleMob(mod_WandaAI mod, int modGuiID, World par1World) {
		super(par1World);
		this.mod = mod;
		this.modGuiId = modGuiID;
	}

	public int getMaxHealth() {
		return 10;
	}

	public int getTotalArmorValue() {
		return 0;
	}

	protected boolean isAIEnabled() {
		return false;
	}

	public void onLivingUpdate() {
		super.onLivingUpdate();
	}

	protected String getLivingSound() {
		return "mob.zombie";
	}

	protected String getHurtSound() {
		return "mob.zombiehurt";
	}

	protected String getDeathSound() {
		return "mob.zombiedeath";
	}

	protected int getDropItemId() {
		return Item.diamond.shiftedIndex;
	}

	public EnumCreatureAttribute getCreatureAttribute() {
		return EnumCreatureAttribute.UNDEAD;
	}

	protected void dropRareDrop(int par1) {
		switch (this.rand.nextInt(4)) {
		case 0:
			this.dropItem(Item.helmetDiamond.shiftedIndex, 1);
			break;
		case 1:
			this.dropItem(Item.plateDiamond.shiftedIndex, 1);
			break;
		case 2:
			this.dropItem(Item.legsDiamond.shiftedIndex, 1);
			break;
		case 3:
			this.dropItem(Item.bootsDiamond.shiftedIndex, 1);
		}
	}

	/**
	 * Called when a player interacts with a mob. e.g. gets milk from a cow,
	 * gets into the saddle on a pig.
	 */
	public boolean interact(EntityPlayer par1EntityPlayer) {
		if (this.isEntityAlive()) {
			if (!this.worldObj.isRemote) {
				par1EntityPlayer.openGui(mod, modGuiId, worldObj, (int) posX,
						(int) posY, (int) posZ);
			}

			return true;
		} else {
			return super.interact(par1EntityPlayer);
		}
	}

}
