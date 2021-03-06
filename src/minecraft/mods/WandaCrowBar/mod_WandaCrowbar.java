package mods.WandaCrowBar;

import java.util.HashMap;

import mods.WandaCore.MaterialTable;
import mods.WandaCore.WandaModBase;
import net.minecraft.block.Block;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Property;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

@Mod(modid = "WandaCrowbar", name = "Wanda Crowbar", version = "0.5.1", dependencies = "required-after:WandaCore")
public class mod_WandaCrowbar extends WandaModBase {

	private static final Block[] blocksEffectiveAgainst;

	private static HashMap<EnumToolMaterial, Crowbar> list;

	static {
		blocksEffectiveAgainst = new Block[] { Block.planks,// 木材
				Block.rail,// レール
				Block.railDetector,// ディテクターレール
				Block.railPowered,// パワードレール
				Block.fence,// 柵
				Block.fenceGate,// フェンスゲート
				Block.fenceIron, // 鉄柵
				Block.bed,// ベッド
				Block.brewingStand,// 醸造台
				Block.cauldron,// 大釜
				Block.bookShelf,// 本棚
				Block.brick,// レンガ
				Block.stoneButton,// ボタン
				Block.lever,// レバー
				Block.chest,// チェスト
				Block.glass,// ガラス
				Block.thinGlass,// ガラス板
				Block.dispenser,// ディスペンサー
				Block.doorSteel,// 鉄製ドア
				Block.doorWood,// 木製ドア
				Block.enchantmentTable,// エンチャント台
				Block.glowStone,// グロウストーン
				Block.jukebox,// ジュークボックス
				Block.ladder,// ハシゴ
				Block.pistonBase, // ピストン
				Block.pistonExtension,// 伸びたピストン
				Block.pistonMoving,// 動いてる最中のピストン
				Block.pistonStickyBase,// スティッキーピストン
				Block.pressurePlatePlanks,// 木製感圧板
				Block.pressurePlateStone,// 石製感圧板
				Block.pumpkinLantern,// ジャック・オー・ランタン
				Block.redstoneLampActive,// レッドストーンランプ(ON)
				Block.redstoneLampIdle,// レッドストーンランプ(OFF)
				Block.redstoneRepeaterActive,// レッドストーンリピーター(ON)
				Block.redstoneRepeaterIdle,// レッドストーンリピーター(OFF)
				Block.redstoneWire,// レッドストーンワイヤ
				Block.signPost,// 看板(棒付き)
				Block.signWall,// 看板(壁付き)
				Block.stoneBrick,// 石レンガ
				Block.furnaceBurning,// かまど(ON)
				Block.furnaceIdle,// かまど(OFF)
				Block.tnt,// TNT
				Block.torchRedstoneActive,// レッドストーントーチ(ON)
				Block.torchRedstoneIdle,// レッドストーントーチ(OFF)
				Block.torchWood,// 松明
				Block.trapdoor,// トラップドア
				Block.workbench,// 作業台
				Block.woodSingleSlab,// 木製半ブロック
				Block.woodDoubleSlab,// 木製半ブロック2個
				Block.stoneSingleSlab,// 石製半ブロック
				Block.stoneDoubleSlab,// 石製半ブロック
				Block.tripWire,// トリップワイヤ(本体)
				Block.tripWireSource,// トリップワイヤ(紐)
				Block.stairsSandStone,// 砂岩階段
				Block.stairsWoodBirch,// 松製階段
				Block.stairsWoodSpruce,// 白樺製階段
				Block.stairsWoodJungle,// ジャングルの木製階段
				Block.stairsWoodOak,// オーク製階段
				Block.stairsCobblestone,// 丸石製階段
				Block.stairsBrick,// レンガ製階段
				Block.stairsStoneBrick,// 石製階段
				Block.blockDiamond,// ダイヤブロック
				Block.blockEmerald,// エメラルドブロック
				Block.blockGold,// 金ブロック
				Block.blockSteel,// 鉄ブロック
				Block.blockLapis,// ラピスラズリブロック
				Block.blockSnow,// 雪ブロック
				Block.music, // ノートブロック
				Block.woodenButton, // 木製ブロック
				Block.anvil,// 鉄床
				Block.beacon,// ビーコン
				Block.blockClay, // 粘土ブロック
				Block.sandStone, // 砂岩
				Block.enderChest, // エンダーチェスト
				Block.chestTrapped, // トラップチェスト
				Block.hopperBlock,//ホッパー
				Block.pressurePlateGold,//金製感圧板
				Block.pressurePlateIron,//鉄製感圧板
				Block.blockNetherQuartz, // ネザー水晶ブロック
				Block.stairCompactNetherQuartz, //ネザー水晶階段
				Block.railActivator, // アクティベータレール
				Block.cobblestoneWall, // 丸石壁
				Block.blockRedstone, // 赤石ブロック
				Block.daylightSensor, // 日光センサー
				Block.redstoneComparatorIdle, // コンパレータ(ON)
				Block.redstoneComparatorActive, // コンパレータ(OFF)
		};
	}

	@Init
	public void init(FMLInitializationEvent event) {
		super.init(event);
		FMLLog.info("Init WandaCrowbar");
		Property propertyItemID = config.get("general", "ItemID", 5020);
		config.save();
		list = new HashMap<EnumToolMaterial, Crowbar>();
		int itemID = propertyItemID.getInt() - 256;

		for (EnumToolMaterial mat : EnumToolMaterial.values()) {
			if (mat.equals(EnumToolMaterial.WOOD)
					|| mat.equals(EnumToolMaterial.STONE)) {
				continue;
			}
			Object materialObject = null;
			try {
				materialObject = MaterialTable.getMaterial(mat);
			} catch (Exception e) {
				System.out.println("WandaCrowbar not support " + mat);
				continue;
			}

			Crowbar temp = new Crowbar(itemID, 4, mat, blocksEffectiveAgainst);
			temp.setUnlocalizedName(MaterialTable.getMaterialName(mat)
					+ "Crowbar");
			LanguageRegistry.addName(temp, MaterialTable.getMaterialName(mat)
					+ " Crowbar");
			GameRegistry.addRecipe(new ItemStack(temp), new Object[] { "XX",
					" X", " X", 'X', materialObject, });
			list.put(mat, temp);
			itemID++;
		}
	}

	@Override
	protected String getModID() {
		return "WandaCrowbar";
	}
}
