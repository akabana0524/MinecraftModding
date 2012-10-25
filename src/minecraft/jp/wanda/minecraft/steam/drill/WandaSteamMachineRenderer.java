package jp.wanda.minecraft.steam.drill;

import jp.wanda.minecraft.steam.WandaSteamFuel;
import jp.wanda.minecraft.steam.WandaSteamFuelMachine;
import net.minecraft.client.Minecraft;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.RenderBlocks;
import net.minecraft.src.RenderItem;
import net.minecraft.src.Tessellator;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.FMLLog;

public class WandaSteamMachineRenderer implements IItemRenderer {
	private Minecraft mc;
	private RenderItem render;

	public WandaSteamMachineRenderer(Minecraft minecraft) {
		this.mc = minecraft;
		render = new RenderItem();
	}

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return type == ItemRenderType.INVENTORY;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item,
			ItemRendererHelper helper) {
		return false;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack par2ItemStack,
			Object... data) {
		switch (type) {
		case EQUIPPED: {
			FMLLog.info("equipped");
		}
			break;
		case INVENTORY: {
			RenderBlocks blocks = (RenderBlocks) data[0];
			Item item = par2ItemStack.getItem();
			render.renderItemIntoGUI(mc.fontRenderer, mc.renderEngine,
					par2ItemStack, 0, 0);
			// render.drawItemIntoGui(mc.fontRenderer, mc.renderEngine,
			// par2ItemStack.itemID, par2ItemStack.getItemDamage(),
			// par2ItemStack.getIconIndex(), 0, 0);
			if (item instanceof WandaSteamFuelMachine) {
				int fuel = WandaSteamFuel.getFuel(par2ItemStack);
				WandaSteamFuelMachine fuelMachine = (WandaSteamFuelMachine) item;
				int var11 = (int) Math.round((double) fuel * 13.0D
						/ (double) fuelMachine.getMaxFuel());
				int var7 = (int) Math.round((double) fuel * 255.0D
						/ (double) fuelMachine.getMaxFuel());
				GL11.glDisable(GL11.GL_LIGHTING);
				GL11.glDisable(GL11.GL_DEPTH_TEST);
				GL11.glDisable(GL11.GL_TEXTURE_2D);
				Tessellator var8 = Tessellator.instance;
				int var9 = 255 - var7 << 16 | var7 << 8;
				int var10 = (255 - var7) / 4 << 16 | 16128;
				this.renderQuad(var8, 2, 13, 13, 2, 0);
				this.renderQuad(var8, 2, 13, 12, 1, var10);
				this.renderQuad(var8, 2, 13, var11, 1, var9);
				GL11.glEnable(GL11.GL_TEXTURE_2D);
				GL11.glEnable(GL11.GL_LIGHTING);
				GL11.glEnable(GL11.GL_DEPTH_TEST);
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			}
		}
			break;
		}
	}

	/**
	 * Adds a quad to the tesselator at the specified position with the set
	 * width and height and color. Args: tessellator, x, y, width, height, color
	 */
	private void renderQuad(Tessellator par1Tessellator, int par2, int par3,
			int par4, int par5, int par6) {
		par1Tessellator.startDrawingQuads();
		par1Tessellator.setColorOpaque_I(par6);
		par1Tessellator.addVertex((double) (par2 + 0), (double) (par3 + 0),
				0.0D);
		par1Tessellator.addVertex((double) (par2 + 0), (double) (par3 + par5),
				0.0D);
		par1Tessellator.addVertex((double) (par2 + par4),
				(double) (par3 + par5), 0.0D);
		par1Tessellator.addVertex((double) (par2 + par4), (double) (par3 + 0),
				0.0D);
		par1Tessellator.draw();
	}

}
