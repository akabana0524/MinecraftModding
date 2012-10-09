package WandaResource;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.src.KeyBinding;
import net.minecraftforge.common.Configuration;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.TickType;

public class WandaKeyHandler extends KeyHandler {

	public static interface WandaKeyListener {
		void keyDown(WandaKey wandaKey, EnumSet<TickType> types, KeyBinding kb, boolean tickEnd,
				boolean isRepeat);

		void keyUp(WandaKey wandaKey, EnumSet<TickType> types, KeyBinding kb, boolean tickEnd);
	}

	public enum WandaKey {
		TOGGLE("Wanda Switch", Keyboard.KEY_V, false);
		private String label;
		private int key;
		private boolean repeat;

		private WandaKey(String label, int key, boolean repeat) {
			this.label = label;
			this.key = key;
			this.repeat = repeat;
		}
	}

	private static Map<WandaKey, List<WandaKeyListener>> listeners;
	private static Map<KeyBinding, WandaKey> keyMap;

	private static KeyBinding[] bindingList;
	private static boolean[] repeatList;
	private static Configuration config;

	static {
		listeners = new HashMap<WandaKey, List<WandaKeyListener>>();
		keyMap = new HashMap<KeyBinding, WandaKey>() {
			{
				for (WandaKey temp : WandaKey.values()) {
					put(new KeyBinding(temp.label, temp.key), temp);
				}
			}
		};
		bindingList = new KeyBinding[keyMap.size()];
		repeatList = new boolean[keyMap.size()];
		int count = 0;
		for (KeyBinding key : keyMap.keySet()) {
			bindingList[count] = key;
			repeatList[count] = keyMap.get(key).repeat;
			count++;
			listeners.put(keyMap.get(key),
					new ArrayList<WandaKeyHandler.WandaKeyListener>());
		}

	}

	public static void registerWandaKeyListener(WandaKey key,
			WandaKeyListener listener) {
		listeners.get(key).add(listener);
	}

	public WandaKeyHandler() {
		super(bindingList, repeatList);
	}

	@Override
	public String getLabel() {
		return "WandaKeyHandler";
	}

	@Override
	public void keyDown(EnumSet<TickType> types, KeyBinding kb,
			boolean tickEnd, boolean isRepeat) {
		for (WandaKeyListener listener : listeners.get(keyMap.get(kb))) {
			listener.keyDown(keyMap.get(kb), types, kb, tickEnd, isRepeat);
		}
	}

	@Override
	public void keyUp(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd) {
		for (WandaKeyListener listener : listeners.get(keyMap.get(kb))) {
			listener.keyUp(keyMap.get(kb), types, kb, tickEnd);
		}
	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.CLIENT);
	}

}
