package jp.wanda.minecraft.core;

import org.lwjgl.opengl.GL11;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.GuiContainer;
import net.minecraft.src.World;

public abstract class WandaGuiContainerBase extends GuiContainer {
	protected EntityPlayer player;
	protected World world;
	protected int x;
	protected int y;
	protected int z;
	private String texture;

	public WandaGuiContainerBase(WandaContainerBase container,
			EntityPlayer player, World world, int x, int y, int z, String texture) {
		super(container);
		this.player = player;
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
		this.texture = texture;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2,
			int var3) {
		int k = mc.renderEngine.getTexture(texture);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.renderEngine.bindTexture(k);
		int l = width - xSize >> 1;
		int i1 = height - ySize >> 1;
		drawTexturedModalRect(l, i1, 0, 0, xSize, ySize);
	}

}
