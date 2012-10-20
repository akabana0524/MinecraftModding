package jp.wanda.minecraft.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class WandaReflectionHelper {

	public static Class<?> getClass(String className) {
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public static Method getMethod(Class c, String name, Class... args) {
		try {
			return c.getMethod(name, args);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		}
	}

	public static Constructor<?> getConstructor(Class c, Class... args) {
		try {
			return c.getConstructor(args);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		}
	}
}
