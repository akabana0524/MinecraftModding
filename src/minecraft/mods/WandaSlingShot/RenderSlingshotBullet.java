package mods.WandaSlingShot;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class RenderSlingshotBullet extends Render {
	private RenderBlocks renderBlocks = new RenderBlocks();

	public RenderSlingshotBullet() {
		shadowSize = 0;
	}

	@Override
	public void doRender(Entity par1Entity, double par2, double par4,
			double par6, float par8, float par9) {
		EntitySlingshotBullet slingshot = (EntitySlingshotBullet) par1Entity;
		GL11.glPushMatrix();
        this.loadTexture("/terrain.png");
		GL11.glEnable(GL11.GL_TEXTURE_2D);

		GL11.glTranslatef((float) par2, (float) par4, (float) par6);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);

		GL11.glScaled(slingshot.boundingBox.maxX - slingshot.boundingBox.minX,
				slingshot.boundingBox.maxY - slingshot.boundingBox.minY,
				slingshot.boundingBox.maxZ - slingshot.boundingBox.minZ);

		this.renderBlocks.renderBlockAsItem(Block.cobblestone, 0, 1);
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glPopMatrix();
	}

	/**
	 * Renders the given texture to the bottom face of the block. Args: block,
	 * x, y, z, texture
	 */
	public void renderBottomFace(AxisAlignedBB par1Block, double par2,
			double par4, double par6, int par8) {
		Tessellator var9 = Tessellator.instance;

		int var10 = (par8 & 15) << 4;
		int var11 = par8 & 240;
		double var12 = ((double) var10 + par1Block.minX * 16.0D) / 256.0D;
		double var14 = ((double) var10 + par1Block.maxX * 16.0D - 0.01D) / 256.0D;
		double var16 = ((double) var11 + par1Block.minZ * 16.0D) / 256.0D;
		double var18 = ((double) var11 + par1Block.maxZ * 16.0D - 0.01D) / 256.0D;

		if (par1Block.minX < 0.0D || par1Block.maxX > 1.0D) {
			var12 = (double) (((float) var10 + 0.0F) / 256.0F);
			var14 = (double) (((float) var10 + 15.99F) / 256.0F);
		}

		if (par1Block.minZ < 0.0D || par1Block.maxZ > 1.0D) {
			var16 = (double) (((float) var11 + 0.0F) / 256.0F);
			var18 = (double) (((float) var11 + 15.99F) / 256.0F);
		}

		double var20 = var14;
		double var22 = var12;
		double var24 = var16;
		double var26 = var18;

		double var28 = par2 + par1Block.minX;
		double var30 = par2 + par1Block.maxX;
		double var32 = par4 + par1Block.minY;
		double var34 = par6 + par1Block.minZ;
		double var36 = par6 + par1Block.maxZ;

		var9.addVertexWithUV(var28, var32, var36, var22, var26);
		var9.addVertexWithUV(var28, var32, var34, var12, var16);
		var9.addVertexWithUV(var30, var32, var34, var20, var24);
		var9.addVertexWithUV(var30, var32, var36, var14, var18);
	}
}
