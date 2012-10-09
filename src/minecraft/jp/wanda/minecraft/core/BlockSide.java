package jp.wanda.minecraft.core;

public enum BlockSide {
	BOTTOM, TOP, FRONT, BACK, LEFT, RIGHT, ;
	public static BlockSide convert6Side(BlockSide dir, BlockSide face) {
		BlockSide side = dir;
		switch (dir) {
		case BOTTOM:
		case TOP:
			break;
		default:
			switch (face) {
			case BACK:
				switch (dir) {
				case FRONT:
					side = BlockSide.BACK;
					break;
				case BACK:
					side = BlockSide.FRONT;
					break;
				case LEFT:
					side = BlockSide.RIGHT;
					break;
				case RIGHT:
					side = BlockSide.LEFT;
					break;
				}
				break;
			case LEFT:
				switch (dir) {
				case FRONT:
					side = BlockSide.RIGHT;
					break;
				case BACK:
					side = BlockSide.LEFT;
					break;
				case LEFT:
					side = BlockSide.FRONT;
					break;
				case RIGHT:
					side = BlockSide.BACK;
					break;
				}
				break;
			case RIGHT:
				switch (dir) {
				case FRONT:
					side = BlockSide.LEFT;
					break;
				case BACK:
					side = BlockSide.RIGHT;
					break;
				case LEFT:
					side = BlockSide.BACK;
					break;
				case RIGHT:
					side = BlockSide.FRONT;
					break;
				}
				break;
			}
			break;

		}
		return side;

	}
}
