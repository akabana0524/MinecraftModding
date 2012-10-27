package jp.wanda.minecraft.core;

import net.minecraft.src.Block;
import net.minecraft.src.EnumToolMaterial;
import net.minecraft.src.Item;
import net.minecraft.src.Material;

public class MaterialTable {
	public static Object getMaterial(EnumToolMaterial mat) {
		switch (mat) {
		case WOOD:
			return Block.planks;

		case STONE:
			return Block.cobblestone;

		case IRON:
			return Item.ingotIron;

		case GOLD:
			return Item.ingotGold;

		case EMERALD:
			return Item.diamond;
		}

		throw new RuntimeException("Unknown material:" + mat);
	}

	public static String getMaterialName(EnumToolMaterial mat) {
		switch (mat) {
		case WOOD:
			return "Wooden";

		case STONE:
			return "Stone";

		case IRON:
			return "Iron";

		case GOLD:
			return "Golden";

		case EMERALD:
			return "Diamond";
		}

		throw new RuntimeException("Unknown material:" + mat);
	}

	public static String getMaterialName(Material material) {
		if (material == Material.air) {
			return "air";
		}

		if (material == Material.grass) {
			return "grass";
		}

		if (material == Material.ground) {
			return "ground";
		}

		if (material == Material.wood) {
			return "wood";
		}

		if (material == Material.rock) {
			return "rock";
		}

		if (material == Material.iron) {
			return "iron";
		}

		if (material == Material.water) {
			return "water";
		}

		if (material == Material.lava) {
			return "lava";
		}

		if (material == Material.leaves) {
			return "leaves";
		}

		if (material == Material.plants) {
			return "plants";
		}

		if (material == Material.vine) {
			return "vine";
		}

		if (material == Material.sponge) {
			return "sponge";
		}

		if (material == Material.cloth) {
			return "cloth";
		}

		if (material == Material.fire) {
			return "fire";
		}

		if (material == Material.sand) {
			return "sand";
		}

		if (material == Material.circuits) {
			return "circuits";
		}

		if (material == Material.glass) {
			return "glass";
		}

		if (material == Material.redstoneLight) {
			return "redstoneLight";
		}

		if (material == Material.tnt) {
			return "tnt";
		}

		if (material == Material.ice) {
			return "ice";
		}

		if (material == Material.snow) {
			return "snow";
		}

		if (material == Material.craftedSnow) {
			return "craftedSnow";
		}

		if (material == Material.cactus) {
			return "cactus";
		}

		if (material == Material.clay) {
			return "clay";
		}

		if (material == Material.pumpkin) {
			return "pumpkin";
		}

		if (material == Material.dragonEgg) {
			return "dragonEgg";
		}

		if (material == Material.portal) {
			return "portal";
		}

		if (material == Material.cake) {
			return "cake";
		}

		if (material == Material.web) {
			return "web";
		}

		if (material == Material.piston) {
			return "piston";
		}

		return "unknown";
	}

	public static int getTextureU(EnumToolMaterial mat) {
		switch (mat) {
		case WOOD:
			return 0;
		case STONE:
			return 1;
		case IRON:
			return 2;
		case EMERALD:
			return 3;
		case GOLD:
			return 4;
		}
		return 0;
	}
}
