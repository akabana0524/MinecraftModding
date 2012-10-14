package WandaResource;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.src.KeyBinding;

import org.lwjgl.input.Keyboard;

import WandaResource.WandaKeyRegistry.WandaKeyListener;

import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.TickType;

public class WandaKeyRegistry {

	public static interface WandaKeyListener {
		void keyDown(WandaKey wandaKey, EnumSet<TickType> types, KeyBinding kb,
				boolean tickEnd, boolean isRepeat);

		void keyUp(WandaKey wandaKey, EnumSet<TickType> types, KeyBinding kb,
				boolean tickEnd);
	}

	private static class Wrap extends KeyHandler {

		private List<WandaKeyListener> listenerList;
		private WandaKey key;

		public Wrap(WandaKey key, List<WandaKeyListener> listenerList) {
			super(new KeyBinding[] { new KeyBinding(key.getName(),
					key.getDefaultKeyboard()) },
					new boolean[] { key.isRepeat() });
			this.listenerList = listenerList;
		}

		@Override
		public String getLabel() {
			return "WandaKeyHandler:" + key.getName();
		}

		@Override
		public void keyDown(EnumSet<TickType> types, KeyBinding kb,
				boolean tickEnd, boolean isRepeat) {
			for (WandaKeyListener listener : listenerList) {
				listener.keyDown(key, types, kb, tickEnd, isRepeat);
			}
		}

		@Override
		public void keyUp(EnumSet<TickType> types, KeyBinding kb,
				boolean tickEnd) {
			for (WandaKeyListener listener : listenerList) {
				listener.keyUp(key, types, kb, tickEnd);
			}
		}

		@Override
		public EnumSet<TickType> ticks() {
			return EnumSet.of(TickType.CLIENT);
		}

	}

	abstract public static class WandaKey {
		public static final WandaKey TOGGLE = new WandaKey() {
			@Override
			public int getDefaultKeyboard() {
				return Keyboard.KEY_V;
			}

			@Override
			public String getName() {
				return "Wanda Switch";
			}

			@Override
			public boolean isRepeat() {
				return false;
			}
		};

		abstract public String getName();

		abstract public int getDefaultKeyboard();

		abstract public boolean isRepeat();

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof WandaKey) {
				WandaKey new_name = (WandaKey) obj;
				if (getName().equals(new_name.getName())) {
					if (getDefaultKeyboard() == new_name.getDefaultKeyboard()
							&& isRepeat() == new_name.isRepeat()) {
						return true;
					}
				} else {
					return false;
				}
				throw new RuntimeException("WandaKey confrict different param:"
						+ new_name.getName());
			} else {
				return false;
			}
		}

		@Override
		public int hashCode() {
			return getName().hashCode();
		}
	}

	public static WandaKeyRegistry instance = new WandaKeyRegistry();

	private Set<WandaKey> registedKey;
	private Map<WandaKey, List<WandaKeyListener>> listenerMap;
	private Map<KeyBinding, WandaKey> keyMap;

	public WandaKeyRegistry() {
		registedKey = new HashSet<WandaKeyRegistry.WandaKey>();
		listenerMap = new HashMap<WandaKeyRegistry.WandaKey, List<WandaKeyListener>>();
		keyMap = new HashMap<KeyBinding, WandaKeyRegistry.WandaKey>();
	}

	public void registerWandaKeyListener(WandaKey key, WandaKeyListener listener) {
		KeyBinding keyBinding = new KeyBinding(key.getName(),
				key.getDefaultKeyboard());
		List<WandaKeyListener> listenerList = null;
		if (!registedKey.contains(key)) {
			listenerList = new ArrayList<WandaKeyRegistry.WandaKeyListener>();
			listenerMap.put(key, listenerList);
			keyMap.put(keyBinding, key);
			KeyBindingRegistry.registerKeyBinding(new Wrap(key, listenerList));
		} else {
			listenerList = listenerMap.get(key);
		}
		listenerList.add(listener);
		registedKey.add(key);
	}

}
