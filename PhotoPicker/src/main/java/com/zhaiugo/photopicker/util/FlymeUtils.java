package com.zhaiugo.photopicker.util;

import android.os.Build;

import java.lang.reflect.Method;

public class FlymeUtils {

	/**
	 * 著作权归作者所有。
	 */

	public static boolean isFlyme() {
		try {
			// Invoke Build.hasSmartBar()
			final Method method = Build.class.getMethod("hasSmartBar");
			return method != null;
		} catch (final Exception e) {
			return false;
		}
	}

}