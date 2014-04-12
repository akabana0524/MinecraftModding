package jp.wanda.minecraft.steam.jetblast;

import java.util.Iterator;
import java.util.List;

import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.DamageSource;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityExplodeFX;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.MathHelper;
import net.minecraft.src.MovingObjectPosition;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.Vec3;
import net.minecraft.src.World;

public class WandaSteamJetBlastBullet extends EntityExplodeFX {

	private EntityLiving shootingEntity;
	private int ticksInAir;
	private int damage;
	private float knockbackStrength;

	public WandaSteamJetBlastBullet(World par1World,
			EntityLiving par2EntityLiving, double par2, double par4,
			double par6, double par8, double par10, double par12) {
		super(par1World, par2, par4, par6, par8, par10, par12);
		shootingEntity = par2EntityLiving;
		damage = 1;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		Vec3 var17 = Vec3.createVectorHelper(this.posX, this.posY, this.posZ);
		Vec3 var3 = Vec3.createVectorHelper(this.posX + this.motionX, this.posY
				+ this.motionY, this.posZ + this.motionZ);
		MovingObjectPosition var4 = this.worldObj.rayTraceBlocks_do_do(var17,
				var3, false, true);
		var17 = Vec3.createVectorHelper(this.posX, this.posY, this.posZ);
		var3 = Vec3.createVectorHelper(this.posX + this.motionX, this.posY
				+ this.motionY, this.posZ + this.motionZ);

		if (var4 != null) {
			var3 = Vec3.createVectorHelper(var4.hitVec.xCoord,
					var4.hitVec.yCoord, var4.hitVec.zCoord);
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
				AxisAlignedBB var12 = var10.boundingBox.expand((double) var11,
						(double) var11, (double) var11);
				MovingObjectPosition var13 = var12.calculateIntercept(var17,
						var3);

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
						++((EntityLiving) var4.entityHit).arrowHitTempCounter;

						if (this.knockbackStrength > 0) {
							float var25 = MathHelper.sqrt_double(this.motionX
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

					this.worldObj.playSoundAtEntity(this, "damage.fallbig",
							1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
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
				this.setDead();
			}
		}

	}

}
