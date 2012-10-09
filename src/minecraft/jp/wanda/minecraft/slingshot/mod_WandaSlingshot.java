package jp.wanda.minecraft.slingshot;

import static cpw.mods.fml.common.Side.CLIENT;
import jp.wanda.minecraft.WandaModBase;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.MLProp;
import net.minecraftforge.common.Property;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

@Mod(modid = "WandaSlingshot", name = "Wanda Slingshot", version = "0.3.0", dependencies = "required-after:WandaResource")
@NetworkMod(clientSideRequired = true, serverSideRequired = false)
public class mod_WandaSlingshot extends WandaModBase {
	public static ItemSlingshot item;

	@Init
	public void init(FMLInitializationEvent event) {
		super.init(event);
		FMLLog.info("Init WandaSlingshot");

		Property propertyItemID = config.getOrCreateIntProperty("ItemID",
				"general", 5040);
		Property propertyEntityID = config.getOrCreateIntProperty(
				"BulletEntityID", "general", 220);
		config.save();

		 EntityRegistry.registerModEntity(EntitySlingshotBullet.class,
		 "entSlingshotBullet", 1, this, 250, 5, true);
		// EntityRegistry.registerGlobalEntityID(EntitySlingshotBullet.class,
		// "entSlingshotBullet", propertyEntityID.getInt());
		if (event.getSide() == Side.CLIENT) {
			registRender();
		}
		item = new ItemSlingshot(propertyItemID.getInt());
		item.setItemName("Slingshot");
		LanguageRegistry.addName(item, "Slingshot");
		GameRegistry.addRecipe(new ItemStack(item), new Object[] { "XYX",
				" X ", " X ", 'X', Item.stick, 'Y', Item.silk, });
	}

	@SideOnly(CLIENT)
	private void registRender() {
		RenderingRegistry.registerEntityRenderingHandler(
				EntitySlingshotBullet.class, new RenderSlingshotBullet());
	}

	@Override
	protected String getModID() {
		return "WandaSlingshot";
	}
}
