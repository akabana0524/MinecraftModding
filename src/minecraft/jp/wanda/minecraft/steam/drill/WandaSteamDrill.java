package jp.wanda.minecraft.steam.drill;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;

import jp.wanda.minecraft.core.WandaReflectionHelper;
import jp.wanda.minecraft.core.packet.WandaPacketHandlerRegistry;
import jp.wanda.minecraft.steam.WandaSteamFuel;
import jp.wanda.minecraft.steam.WandaSteamFuelMachine;
import jp.wanda.minecraft.steam.mod_WandaSteamCore;
import net.minecraft.client.Minecraft;
import net.minecraft.src.Block;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EnumAction;
import net.minecraft.src.EnumToolMaterial;
import net.minecraft.src.ItemPickaxe;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ModelBiped;
import net.minecraft.src.ModelRenderer;
import net.minecraft.src.MovingObjectPosition;
import net.minecraft.src.Packet;
import net.minecraft.src.Packet14BlockDig;
import net.minecraft.src.Packet55BlockDestroy;
import net.minecraft.src.RenderManager;
import net.minecraft.src.RenderPlayer;
import net.minecraft.src.Vec3;
import net.minecraft.src.World;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLLog;

public class WandaSteamDrill extends ItemPickaxe implements
		WandaSteamFuelMachine {

	private int maxFuel;

	protected WandaSteamDrill(int itemID, int coordRow, int coordColumn,
			EnumToolMaterial toolMaterial, int maxFuel) {
		super(itemID, toolMaterial);
		setIconCoord(coordRow, coordColumn);
		setMaxDamage(0);
		this.maxFuel = maxFuel;
		canRepair = false;
	}

	@Override
	public void onCreated(ItemStack par1ItemStack, World par2World,
			EntityPlayer par3EntityPlayer) {
		super.onCreated(par1ItemStack, par2World, par3EntityPlayer);
	}

	@Override
	public boolean getShareTag() {
		return true;
	}

	@Override
	public String getTextureFile() {
		return mod_WandaSteamCore.TEXTURE;
	}

	@Override
	public int getDamageVsEntity(Entity par1Entity) {
		return super.getDamageVsEntity(par1Entity) * 2;
	}

	public float getStrVsBlock(ItemStack par1ItemStack, Block par2Block) {
		if (isEnoughFuel(par1ItemStack)) {
			return super.getStrVsBlock(par1ItemStack, par2Block);
		} else {
			return 0;
		}
	}

	@Override
	public boolean onBlockStartBreak(ItemStack itemStack, int x, int y, int z,
			EntityPlayer player) {
		World par2World = player.worldObj;
		if (isEnoughFuel(itemStack)) {
			if (par2World.isRemote) {
				MovingObjectPosition objectMouseOver = Minecraft.getMinecraft().objectMouseOver;
				if (objectMouseOver != null) {
					int orientation = objectMouseOver.sideHit;
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					DataOutputStream dos = new DataOutputStream(baos);
					try {
						dos.writeInt(x);
						dos.writeInt(y);
						dos.writeInt(z);
						dos.writeByte(orientation);
						FMLClientHandler
								.instance()
								.sendPacket(
										WandaPacketHandlerRegistry
												.createWandaPacket(
														mod_WandaSteamDrill.instance
																.getModID(),
														baos.toByteArray(),
														false));
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						if (baos != null) {
							try {
								baos.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				}
			} else {
				WandaSteamFuel.subFuelMachine(itemStack, 200);
			}
			return super.onBlockStartBreak(itemStack, x, y, z, player);
		}
		return false;
	}

	private boolean isEnoughFuel(ItemStack par1ItemStack) {
		return WandaSteamFuel.getFuel(par1ItemStack) > 0;
	}

	@Override
	public void onUpdate(ItemStack itemStack, World world, Entity entity,
			int par4, boolean par5) {
		if (!world.isRemote) {
			EntityPlayer player = (EntityPlayer) entity;
			if (!player.capabilities.isCreativeMode) {
				WandaSteamFuel.idlingFuelMachine(itemStack);
			}
			if (player.field_82175_bq) {
				WandaSteamFuel.subFuelMachine(itemStack, 2);
			}
		} else {
			// if (WandaSteamFuel.getFuel(itemStack) > 0) {
			// if (entity instanceof EntityPlayer) {
			// EntityPlayer new_name = (EntityPlayer) entity;
			// if (new_name.getCurrentEquippedItem() == itemStack) {
			// if (world.rand.nextInt(3) == 0) {
			// hoge((EntityPlayer) entity, itemStack, world);
			// }
			// }
			// }
			// }
		}
		super.onUpdate(itemStack, world, entity, par4, par5);
	}

	@Override
	public int getMaxFuel() {
		return maxFuel;
	}

	private void hoge(EntityPlayer par1EntityPlayer, ItemStack var21,
			World world) {
		float var6;
		EnumAction var20 = null;
		RenderPlayer p = (RenderPlayer) RenderManager.instance.entityRenderMap
				.get(EntityPlayer.class);
		Field modelBipedMain = WandaReflectionHelper.getDeclaredField(
				RenderPlayer.class, "modelBipedMain");
		modelBipedMain.setAccessible(true);
		try {
			Vec3 temp = Vec3.createVectorHelper(par1EntityPlayer.posX,
					par1EntityPlayer.posY, par1EntityPlayer.posZ);
			// FMLLog.info("p:%s", temp.toString());
			float par1 = 0.0625F;
			// par1 = 1;
			ModelBiped biped = (ModelBiped) modelBipedMain.get(p);
			ModelRenderer arm = biped.bipedRightArm;
			Vec3 rightArm = Vec3.createVectorHelper(-0.0625F, 0.4375F, 0.0625F);
			// FMLLog.info("r:%s", rightArm.toString());
			if (arm.rotateAngleX == 0.0F && arm.rotateAngleY == 0.0F
					&& arm.rotateAngleZ == 0.0F) {
				if (arm.rotationPointX != 0.0F || arm.rotationPointY != 0.0F
						|| arm.rotationPointZ != 0.0F) {
					// temp.addVector(arm.rotationPointX, arm.rotationPointY,
					// arm.rotationPointZ);
				}
			} else {

				if (arm.rotateAngleZ != 0.0F) {
					rightArm.rotateAroundZ(arm.rotateAngleZ
							* (180F / (float) Math.PI));
				}

				if (arm.rotateAngleY != 0.0F) {
					rightArm.rotateAroundY(arm.rotateAngleY
							* (180F / (float) Math.PI));
				}

				if (arm.rotateAngleX != 0.0F) {
					rightArm.rotateAroundX(arm.rotateAngleX
							* (180F / (float) Math.PI));
				}
				rightArm = rightArm.addVector(arm.rotationPointX * par1,
						arm.rotationPointY * par1, arm.rotationPointZ * par1);
				temp = temp.addVector(rightArm.xCoord, rightArm.yCoord,
						rightArm.zCoord);
			}
			FMLLog.info("r:%s", rightArm.toString());
			world.spawnParticle("note", temp.xCoord, temp.yCoord, temp.zCoord,
					0, 0, 0);

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

	}
}
