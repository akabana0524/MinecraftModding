package jp.wanda.minecraft.steam.drill;

import jp.wanda.minecraft.steam.mod_WandaSteamCore;
import net.minecraft.src.Block;
import net.minecraft.src.EnumToolMaterial;
import net.minecraft.src.ItemPickaxe;

public class WandaSteamDrill extends ItemPickaxe {

	protected WandaSteamDrill(int itemID, int coordRow, int coordColumn,
			EnumToolMaterial toolMaterial) {
		super(itemID, toolMaterial);
		setIconCoord(coordRow, coordColumn);
	}

	@Override
	public String getTextureFile() {
		return mod_WandaSteamCore.TEXTURE;
	}

}
