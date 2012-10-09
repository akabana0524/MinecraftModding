package WandaResource;

import net.minecraftforge.client.MinecraftForgeClient;

public class Client extends Proxy {
	@Override
	public void registerRenderInformation() {
		super.registerRenderInformation();
		MinecraftForgeClient.preloadTexture("/WandaResource/WandaTools.png");
		MinecraftForgeClient.preloadTexture("/WandaResource/WandaBlocks.png");
		MinecraftForgeClient.preloadTexture("/WandaResource/WandaItems.png");
	}
}