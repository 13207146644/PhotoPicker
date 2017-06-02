package com.zhaiugo.photopicker.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.zhaiugo.photopicker.variable.Variable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ScreenUtil {

	/**
	 * 初始化屏幕的大小密度等参数
	 * 
	 * @param c
	 */
	public static void initScreenProperties(Context c) {
		DisplayMetrics dm = new DisplayMetrics();
		((Activity) c).getWindowManager().getDefaultDisplay().getMetrics(dm);
		Variable.DESITY = dm.density;
		Variable.WIDTH = dm.widthPixels;
		Variable.HEIGHT = dm.heightPixels;
	}

	/**
	 * 获取状态栏的高度
	 * 
	 * @param c
	 * @return
	 */
	public static int getStatusBarHeight(Context c) {
		int statusBarHeight = 0;
		try {
			Class<?> clazz = Class.forName("com.android.internal.R$dimen");
			Object obj = clazz.newInstance();
			Field field = clazz.getField("status_bar_height");
			int temp = Integer.parseInt(field.get(obj).toString());
			statusBarHeight = c.getResources().getDimensionPixelSize(temp);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return statusBarHeight;
	}

	/**
	 * 获取NavigationBar高度
	 * @param context
	 * @return
	 */
	public static int getNavigationBarHeight(Context context) {
		Resources resources = context.getResources();
		int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
		int height = resources.getDimensionPixelSize(resourceId);
		return height;
	}

	/**
	 * 判断是否有NavigationBar
	 * @param context
	 * @return
	 */
	public static boolean checkDeviceHasNavigationBar(Context context) {
		boolean hasNavigationBar = false;
		Resources rs = context.getResources();
		int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
		if (id > 0) {
			hasNavigationBar = rs.getBoolean(id);
		}
		try {
			Class<?> systemPropertiesClass = Class.forName("android.os.SystemProperties");
			Method m = systemPropertiesClass.getMethod("get", String.class);
			String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
			if ("1".equals(navBarOverride)) {
				hasNavigationBar = false;
			} else if ("0".equals(navBarOverride)) {
				hasNavigationBar = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return hasNavigationBar;
	}

}
