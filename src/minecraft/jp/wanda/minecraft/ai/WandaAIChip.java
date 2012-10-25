package jp.wanda.minecraft.ai;

import net.minecraft.src.Entity;
import net.minecraft.src.World;

public interface WandaAIChip {
	int getRow();

	int getColumn();

	int[] getNeedOutputColor();

	String getOutputDescription(int color);

	int proc(World world, Entity self);
}
