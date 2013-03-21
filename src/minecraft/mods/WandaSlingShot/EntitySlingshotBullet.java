package mods.WandaSlingShot;

import java.util.Iterator;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet61DoorChange;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntitySlingshotBullet extends Entity {

	public enum Type {
		STONE, DIAMOND,
	}

	private int xTile = -1;
	private int yTile = -1;
	private int zTile = -1;
	private int inTile = 0;
	private int inData = 0;
	private boolean inGround = false;
	private Type type;
	/** 1 if the player can pick up the arrow */
	public int canBePickedUp = 0;

	/** The owner of this arrow. */
	public Entity shootingEntity;
	private int ticksInGround;
	private int ticksInAir = 0;
	private double damage = 1.0D;

	/** The amount of knockback an arrow applies when it hits a mob. */
	private int knockbackStrength;

	public EntitySlingshotBullet(World par1World) {
		super(par1World);
		this.setSize(0.25F, 0.25F);
		renderDistanceWeight = 3;
	}

	public EntitySlingshotBullet(World par1World,
			EntityLiving par2EntityLiving, float par3) {
		super(par1World);
		this.shootingEntity = par2EntityLiving;
		renderDistanceWeight = 10;

		if (par2EntityLiving instanceof EntityPlayer) {
			this.canBePickedUp = 1;
		}

		this.setSize(0.25F, 0.25F);
		this.setLocationAndAngles(par2EntityLiving.posX, par2EntityLiving.posY
				+ (double) par2EntityLiving.getEyeHeight(),
				par2EntityLiving.posZ, par2EntityLiving.rotationYaw,
				par2EntityLiving.rotationPitch);
		this.posX -= (double) (MathHelper.cos(this.rotationYaw / 180.0F
				* (float) Math.PI) * 0.16F);
		this.posY -= 0.10000000149011612D;
		this.posZ -= (double) (MathHelper.sin(this.rotationYaw / 180.0F
				* (float) Math.PI) * 0.16F);
		this.setPosition(this.posX, this.posY, this.posZ);
		this.yOffset = 0.0F;
		this.motionX = (double) (-MathHelper.sin(this.rotationYaw / 180.0F
				* (float) Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F
				* (float) Math.PI));
		this.motionZ = (double) (MathHelper.cos(this.rotationYaw / 180.0F
				* (float) Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F
				* (float) Math.PI));
		this.motionY = (double) (-MathHelper.sin(this.rotationPitch / 180.0F
				* (float) Math.PI));
		this.setArrowHeading(this.motionX, this.motionY, this.motionZ,
				par3 * 1.5F, 1.0F);
	}

	protected void entityInit() {
		this.dataWatcher.addObject(16, Byte.valueOf((byte) 0));
	}

	/**
	 * Uses the provided coordinates as a heading and determines the velocity
	 * from it with the set force and random variance. Args: x, y, z, force,
	 * forceVariation
	 */
	public void setArrowHeading(double par1, double par3, double par5,
			float par7, float par8) {
		float var9 = MathHelper.sqrt_double(par1 * par1 + par3 * par3 + par5
				* par5);
		par1 /= (double) var9;
		par3 /= (double) var9;
		par5 /= (double) var9;
		par1 += this.rand.nextGaussian() * 0.007499999832361937D
				* (double) par8;
		par3 += this.rand.nextGaussian() * 0.007499999832361937D
				* (double) par8;
		par5 += this.rand.nextGaussian() * 0.007499999832361937D
				* (double) par8;
		par1 *= (double) par7;
		par3 *= (double) par7;
		par5 *= (double) par7;
		this.motionX = par1;
		this.motionY = par3;
		this.motionZ = par5;
		float var10 = MathHelper.sqrt_double(par1 * par1 + par5 * par5);
		this.prevRotationYaw = this.rotationYaw = (float) (Math.atan2(par1,
				par5) * 180.0D / Math.PI);
		this.prevRotationPitch = this.rotationPitch = (float) (Math.atan2(par3,
				(double) var10) * 180.0D / Math.PI);
		this.ticksInGround = 0;
	}

	@SideOnly(Side.CLIENT)
	/**
	 * Sets the position and rotation. Only difference from the other one is no bounding on the rotation. Args: posX,
	 * posY, posZ, yaw, pitch
	 */
	public void setPositionAndRotation2(double par1, double par3, double par5,
			float par7, float par8, int par9) {
		this.setPosition(par1, par3, par5);
		this.setRotation(par7, par8);
	}

	@SideOnly(Side.CLIENT)
	/**
	 * Sets the velocity to the args. Args: x, y, z
	 */
	public void setVelocity(double par1, double par3, double par5) {
		this.motionX = par1;
		this.motionY = par3;
		this.motionZ = par5;

		if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F) {
			float var7 = MathHelper.sqrt_double(par1 * par1 + par5 * par5);
			this.prevRotationYaw = this.rotationYaw = (float) (Math.atan2(par1,
					par5) * 180.0D / Math.PI);
			this.prevRotationPitch = this.rotationPitch = (float) (Math.atan2(
					par3, (double) var7) * 180.0D / Math.PI);
			this.prevRotationPitch = this.rotationPitch;
			this.prevRotationYaw = this.rotationYaw;
			this.setLocationAndAngles(this.posX, this.posY, this.posZ,
					this.rotationYaw, this.rotationPitch);
			this.ticksInGround = 0;
		}
	}

	/**
	 * Called to update the entity's position/logic.
	 */
	public void onUpdate() {
		super.onUpdate();

		if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F) {
			float var1 = MathHelper.sqrt_double(this.motionX * this.motionX
					+ this.motionZ * this.motionZ);
			this.prevRotationYaw = this.rotationYaw = (float) (Math.atan2(
					this.motionX, this.motionZ) * 180.0D / Math.PI);
			this.prevRotationPitch = this.rotationPitch = (float) (Math.atan2(
					this.motionY, (double) var1) * 180.0D / Math.PI);
		}

		int var16 = this.worldObj
				.getBlockId(this.xTile, this.yTile, this.zTile);

		if (var16 > 0) {
			Block.blocksList[var16].setBlockBoundsBasedOnState(this.worldObj,
					this.xTile, this.yTile, this.zTile);
			AxisAlignedBB var2 = Block.blocksList[var16]
					.getCollisionBoundingBoxFromPool(this.worldObj, this.xTile,
							this.yTile, this.zTile);

			if (var2 != null
					&& var2.isVecInside(this.worldObj.getWorldVec3Pool()
							.getVecFromPool(this.posX, this.posY, this.posZ))) {
				this.inGround = true;
			}
		}

		if (this.inGround) {
			int var18 = this.worldObj.getBlockId(this.xTile, this.yTile,
					this.zTile);
			int var19 = this.worldObj.getBlockMetadata(this.xTile, this.yTile,
					this.zTile);

			if (var18 == this.inTile && var19 == this.inData) {
				++this.ticksInGround;

				if (this.ticksInGround == 1200) {
					this.setDead();
				}
			} else {
				this.inGround = false;
				this.motionX *= (double) (this.rand.nextFloat() * 0.2F);
				this.motionY *= (double) (this.rand.nextFloat() * 0.2F);
				this.motionZ *= (double) (this.rand.nextFloat() * 0.2F);
				this.ticksInGround = 0;
				this.ticksInAir = 0;
			}
		} else {
			++this.ticksInAir;
			Vec3 var17 = this.worldObj.getWorldVec3Pool().getVecFromPool(
					this.posX, this.posY, this.posZ);
			Vec3 var3 = this.worldObj.getWorldVec3Pool().getVecFromPool(
					this.posX + this.motionX, this.posY + this.motionY,
					this.posZ + this.motionZ);
			MovingObjectPosition var4 = this.worldObj.rayTraceBlocks_do_do(
					var17, var3, false, true);
			var17 = this.worldObj.getWorldVec3Pool().getVecFromPool(this.posX,
					this.posY, this.posZ);
			var3 = this.worldObj.getWorldVec3Pool().getVecFromPool(
					this.posX + this.motionX, this.posY + this.motionY,
					this.posZ + this.motionZ);

			if (var4 != null) {
				var3 = this.worldObj.getWorldVec3Pool().getVecFromPool(
						var4.hitVec.xCoord, var4.hitVec.yCoord,
						var4.hitVec.zCoord);
			}

			Entity var5 = null;
			List var6 = this.worldObj.getEntitiesWithinAABBExcludingEntity(
					this,
					this.boundingBox.addCoord(this.motionX, this.motionY,
							this.motionZ).expand(1.0D, 1.0D, 1.0D));
			double var7 = 0.0D;
			Iterator var9 = var6.iterator();
			float var11;

			while (var9.hasNext()) {
				Entity var10 = (Entity) var9.next();
				if (var10.canBeCollidedWith()
						&& (var10 != this.shootingEntity || this.ticksInAir >= 5)) {
					var11 = 0.3F;
					AxisAlignedBB var12 = var10.boundingBox.expand(
							(double) var11, (double) var11, (double) var11);
					MovingObjectPosition var13 = var12.calculateIntercept(
							var17, var3);

					if (var13 != null) {
						double var14 = var17.distanceTo(var13.hitVec);
						if (var14 < var7 || var7 == 0.0D) {
							var5 = var10;
							var7 = var14;
						}
					}
				}
			}

			if (var5 != null) {
				var4 = new MovingObjectPosition(var5);
			}

			float var20;

			if (var4 != null) {
				if (var4.entityHit != null) {
					var20 = MathHelper.sqrt_double(this.motionX * this.motionX
							+ this.motionY * this.motionY + this.motionZ
							* this.motionZ);
					int var24 = MathHelper.ceiling_double_int((double) var20
							* this.damage);

					if (this.getIsCritical()) {
						var24 += this.rand.nextInt(var24 / 2 + 2);
					}

					DamageSource var22 = null;

					if (this.shootingEntity == null) {
						var22 = DamageSource.causeThrownDamage(this, this);
					} else {
						var22 = DamageSource.causeThrownDamage(this,
								this.shootingEntity);
					}

					if (this.isBurning()) {
						var4.entityHit.setFire(5);
					}

					if (var4.entityHit.attackEntityFrom(var22, var24)) {
						if (var4.entityHit instanceof EntityLiving) {
							EntityLiving el = (EntityLiving) var4.entityHit;
							el.setArrowCountInEntity(el.getArrowCountInEntity() + 1);
							if (this.knockbackStrength > 0) {
								float var25 = MathHelper
										.sqrt_double(this.motionX
												* this.motionX + this.motionZ
												* this.motionZ);

								if (var25 > 0.0F) {
									var4.entityHit
											.addVelocity(
													this.motionX
															* (double) this.knockbackStrength
															* 0.6000000238418579D
															/ (double) var25,
													0.1D,
													this.motionZ
															* (double) this.knockbackStrength
															* 0.6000000238418579D
															/ (double) var25);
								}
							}
						}

						this.worldObj.playSoundAtEntity(this,
								"damage.fallsmall", 1.0F,
								1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
						if (!worldObj.isRemote) {
							addEffect(this.worldObj, null,
									(this.posX + this.motionX),
									(this.posY + this.motionY),
									(this.posZ + this.motionZ));
						}
						this.setDead();
					} else {
						this.motionX *= -0.10000000149011612D;
						this.motionY *= -0.10000000149011612D;
						this.motionZ *= -0.10000000149011612D;
						this.rotationYaw += 180.0F;
						this.prevRotationYaw += 180.0F;
						this.ticksInAir = 0;
					}
				} else {
					this.xTile = var4.blockX;
					this.yTile = var4.blockY;
					this.zTile = var4.blockZ;
					this.inTile = this.worldObj.getBlockId(this.xTile,
							this.yTile, this.zTile);
					this.inData = this.worldObj.getBlockMetadata(this.xTile,
							this.yTile, this.zTile);
					this.motionX = (double) ((float) (var4.hitVec.xCoord - this.posX));
					this.motionY = (double) ((float) (var4.hitVec.yCoord - this.posY));
					this.motionZ = (double) ((float) (var4.hitVec.zCoord - this.posZ));
					var20 = MathHelper.sqrt_double(this.motionX * this.motionX
							+ this.motionY * this.motionY + this.motionZ
							* this.motionZ);
					this.posX -= this.motionX / (double) var20
							* 0.05000000074505806D;
					this.posY -= this.motionY / (double) var20
							* 0.05000000074505806D;
					this.posZ -= this.motionZ / (double) var20
							* 0.05000000074505806D;
					this.worldObj.playSoundAtEntity(this, "damage.fallsmall",
							1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));

					if (!worldObj.isRemote) {
						addEffect(worldObj, null, this.xTile, this.yTile,
								this.zTile);
					}
					this.inGround = true;
					this.setIsCritical(false);
					this.setDead();
				}
			}

			if (this.getIsCritical()) {
				for (int var21 = 0; var21 < 4; ++var21) {
					this.worldObj.spawnParticle("crit", this.posX
							+ this.motionX * (double) var21 / 4.0D, this.posY
							+ this.motionY * (double) var21 / 4.0D, this.posZ
							+ this.motionZ * (double) var21 / 4.0D,
							-this.motionX, -this.motionY + 0.2D, -this.motionZ);
				}
			}

			this.posX += this.motionX;
			this.posY += this.motionY;
			this.posZ += this.motionZ;
			var20 = MathHelper.sqrt_double(this.motionX * this.motionX
					+ this.motionZ * this.motionZ);
			this.rotationYaw = (float) (Math.atan2(this.motionX, this.motionZ) * 180.0D / Math.PI);

			for (this.rotationPitch = (float) (Math.atan2(this.motionY,
					(double) var20) * 180.0D / Math.PI); this.rotationPitch
					- this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F) {
				;
			}

			while (this.rotationPitch - this.prevRotationPitch >= 180.0F) {
				this.prevRotationPitch += 360.0F;
			}

			while (this.rotationYaw - this.prevRotationYaw < -180.0F) {
				this.prevRotationYaw -= 360.0F;
			}

			while (this.rotationYaw - this.prevRotationYaw >= 180.0F) {
				this.prevRotationYaw += 360.0F;
			}

			this.rotationPitch = this.prevRotationPitch
					+ (this.rotationPitch - this.prevRotationPitch) * 0.2F;
			this.rotationYaw = this.prevRotationYaw
					+ (this.rotationYaw - this.prevRotationYaw) * 0.2F;
			float var23 = 0.99F;
			var11 = 0.05F;

			if (this.isInWater()) {
				for (int var26 = 0; var26 < 4; ++var26) {
					float var27 = 0.25F;
					this.worldObj.spawnParticle("bubble", this.posX
							- this.motionX * (double) var27, this.posY
							- this.motionY * (double) var27, this.posZ
							- this.motionZ * (double) var27, this.motionX,
							this.motionY, this.motionZ);
				}

				var23 = 0.8F;
			}

			this.motionX *= (double) var23;
			this.motionY *= (double) var23;
			this.motionZ *= (double) var23;
			this.motionY -= (double) var11;
			this.setPosition(this.posX, this.posY, this.posZ);
			this.doBlockCollisions();
		}
	}

	private void addEffect(World world, EntityPlayer par1EntityPlayer,
			double d, double e, double f) {
		// if (Minecraft.getMinecraft() != null) {
		// Minecraft.getMinecraft().effectRenderer.addBlockDestroyEffects(x,
		// y, z, Block.cobblestone.blockID & 4095,
		// Block.cobblestone.blockID >> 12 & 255);
		// }
		FMLCommonHandler
				.instance()
				.getMinecraftServerInstance()
				.getConfigurationManager()
				.sendToAllNearExcept(
						par1EntityPlayer,
						(double) d,
						(double) e,
						(double) f,
						64.0D,
						world.provider.dimensionId,
						new Packet61DoorChange(2001, (int) d, (int) e, (int) f,
								Block.cobblestone.blockID, false));
	}

	/**
	 * (abstract) Protected helper method to write subclass entity data to NBT.
	 */
	public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
		par1NBTTagCompound.setShort("xTile", (short) this.xTile);
		par1NBTTagCompound.setShort("yTile", (short) this.yTile);
		par1NBTTagCompound.setShort("zTile", (short) this.zTile);
		par1NBTTagCompound.setByte("inTile", (byte) this.inTile);
		par1NBTTagCompound.setByte("inData", (byte) this.inData);
		par1NBTTagCompound.setByte("inGround", (byte) (this.inGround ? 1 : 0));
		par1NBTTagCompound.setByte("pickup", (byte) this.canBePickedUp);
		par1NBTTagCompound.setDouble("damage", this.damage);
	}

	/**
	 * (abstract) Protected helper method to read subclass entity data from NBT.
	 */
	public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
		this.xTile = par1NBTTagCompound.getShort("xTile");
		this.yTile = par1NBTTagCompound.getShort("yTile");
		this.zTile = par1NBTTagCompound.getShort("zTile");
		this.inTile = par1NBTTagCompound.getByte("inTile") & 255;
		this.inData = par1NBTTagCompound.getByte("inData") & 255;
		this.inGround = par1NBTTagCompound.getByte("inGround") == 1;

		if (par1NBTTagCompound.hasKey("damage")) {
			this.damage = par1NBTTagCompound.getDouble("damage");
		}

		if (par1NBTTagCompound.hasKey("pickup")) {
			this.canBePickedUp = par1NBTTagCompound.getByte("pickup");
		} else if (par1NBTTagCompound.hasKey("player")) {
			this.canBePickedUp = par1NBTTagCompound.getBoolean("player") ? 1
					: 0;
		}
	}

	/**
	 * Called by a player entity when they collide with an entity
	 */
	public void onCollideWithPlayer(EntityPlayer par1EntityPlayer) {
		if (!this.worldObj.isRemote && this.inGround) {
			boolean var2 = this.canBePickedUp == 1 || this.canBePickedUp == 2
					&& par1EntityPlayer.capabilities.isCreativeMode;

			if (this.canBePickedUp == 1
					&& !par1EntityPlayer.inventory
							.addItemStackToInventory(new ItemStack(Item.arrow,
									1))) {
				var2 = false;
			}

			if (var2) {
				this.worldObj
						.playSoundAtEntity(
								this,
								"random.pop",
								0.2F,
								((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
				par1EntityPlayer.onItemPickup(this, 1);
				this.setDead();
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public float getShadowSize() {
		return 0.0F;
	}

	public void setDamage(double par1) {
		this.damage = par1;
	}

	public double getDamage() {
		return this.damage;
	}

	/**
	 * Sets the amount of knockback the arrow applies when it hits a mob.
	 */
	public void setKnockbackStrength(int par1) {
		this.knockbackStrength = par1;
	}

	/**
	 * If returns false, the item will not inflict any damage against entities.
	 */
	public boolean canAttackWithItem() {
		return false;
	}

	/**
	 * Whether the arrow has a stream of critical hit particles flying behind it.
	 */
	public void setIsCritical(boolean par1) {
		byte var2 = this.dataWatcher.getWatchableObjectByte(16);

		if (par1) {
			this.dataWatcher.updateObject(16, Byte.valueOf((byte) (var2 | 1)));
		} else {
			this.dataWatcher.updateObject(16, Byte.valueOf((byte) (var2 & -2)));
		}
	}

	/**
	 * Whether the arrow has a stream of critical hit particles flying behind it.
	 */
	public boolean getIsCritical() {
		byte var1 = this.dataWatcher.getWatchableObjectByte(16);
		return (var1 & 1) != 0;
	}

	public Type getType() {
		return type;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean isInRangeToRenderDist(double par1) {
		return super.isInRangeToRenderDist(par1);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean isInRangeToRenderVec3D(Vec3 par1Vec3) {
		return super.isInRangeToRenderVec3D(par1Vec3);
	}
}