package jp.wanda.minecraft.res;

import net.minecraftforge.client.MinecraftForgeClient;

public class Client extends Proxy {
	@Override
	public void registerRenderInformation() {
		super.registerRenderInformation();
		MinecraftForgeClient.preloadTexture(WandaResource.TEXTURE_BLOCK);
		MinecraftForgeClient.preloadTexture(WandaResource.TEXTURE_ITEMS);
		MinecraftForgeClient.preloadTexture(WandaResource.TEXTURE_TOOLS);
	}
}