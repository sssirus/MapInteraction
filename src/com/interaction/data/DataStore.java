package com.interaction.data;

import android.app.Activity;
import android.util.Log;

public class DataStore {
	// 全局变量

	private static int playerX;// 当前屏幕中心横坐标
	private static int playerY;// 当前屏幕中心纵坐标

	private static int screen_width;// 屏幕宽度
	private static int screen_height;// 屏幕高度

	private static int width;// 显示宽度
	private static int height;// 显示高度

	private static int map_width = 26625;// 地图的尺寸
	private static int map_height = 822;

	private static int sMap_width;
	private static int sMap_height;
	
	private static Activity mainActivity;

	private static int select;// 选择的模式，0为手势，1为语音，2为感应器

	
	
	
	public static Activity getMainActivity() {
		return mainActivity;
	}

	public static void setMainActivity(Activity mainActivity) {
		DataStore.mainActivity = mainActivity;
	}

	/**
	 * 对设置不合理的参数进行调整
	 */
	public static void adjustment() {
		// 先对切割的高进行处理
		if (DataStore.getHeight() < ((double) DataStore.getMapHeight() / 4)) {
			DataStore.setHeight(DataStore.getMapHeight() / 4);
		} else {
			if (DataStore.getHeight() > DataStore.getMapHeight()) {
				DataStore.setHeight(DataStore.getMapHeight());
			}
		}

		// 然后对切割的宽进行处理
		
//		DataStore
//		.setWidth((int) (2*(double) DataStore.height * (double) DataStore.screen_width / (double) DataStore.screen_height));
		DataStore.setWidth((int)((double)DataStore.getHeight()*((double)DataStore.getScreen_width()/((double)DataStore.getScreen_height()-338))));

		// 对playerX调整
		if (DataStore.playerX - (DataStore.getWidth() / 2) < 0) {
			DataStore.playerX = DataStore.getWidth() / 2;
		} else {
			if (DataStore.playerX + (DataStore.getWidth() / 2) > DataStore
					.getMapWidth()) {
				DataStore.playerX = DataStore.getMapWidth()
						- (DataStore.getWidth() / 2);
			}
		}

		// 对playerY调整
		if (DataStore.playerY - (DataStore.getHeight() / 2) < 0) {
			DataStore.playerY = DataStore.getHeight() / 2;
		} else {
			if (DataStore.playerY + (DataStore.getHeight() / 2) > DataStore
					.getMapHeight()) {
				DataStore.playerY = DataStore.getMapHeight()
						- (DataStore.getHeight() / 2);
			}
		}
	}

	// 显示的尺寸
	public static int getWidth() {
		return width;
	}

	public static void setWidth(int wide) {
		DataStore.width = wide;

	}

	public static int getHeight() {
		return height;
	}

	public static void setHeight(int high) {
		//new Exception("height changed").printStackTrace();
		DataStore.height = high;
	}

	// get,set 当前屏幕中心横坐标
	public static int getPlayerX() {
		return playerX;
	}

	public static void setPlayerX(int playerX) {
		DataStore.playerX = playerX;
	}

	// get,set 当前屏幕中心纵坐标
	public static int getPlayerY() {
		return playerY;
	}

	public static void setPlayerY(int playerY) {
		DataStore.playerY = playerY;

	}

	// get,set 屏幕宽度
	public static int getScreen_width() {
		return screen_width;
	}

	public static void setScreen_width(int screen_wide) {
		DataStore.screen_width = screen_wide;
	}

	// get,set 当前屏幕高度
	public static int getScreen_height() {
		return screen_height;
	}

	public static void setScreen_height(int screen_high) {
		DataStore.screen_height = screen_high;
	}

	// 得到屏幕的横纵坐标
	public static int getX1() {
		return playerX - width / 2;
	}

	public static int getY1() {
		return playerY - height / 2;
	}

	public static int getX2() {
		return playerX + width / 2;
	}

	public static int getY2() {
		return playerY + height / 2;
	}

	// 地图尺寸的得取
	public static int getMapWidth() {
		return map_width;
	}

	public static int getMapHeight() {
		return map_height;
	}

	public static void setMapWidth(int map_width) {
		DataStore.map_width = map_width;
	}

	public static void setMapHeight(int map_high) {
		DataStore.map_height = map_high;
	}

	// 得到、设定模式参数
	public static int getSelect() {
		return select;
	}

	public static void setSelect(int select) {
		DataStore.select = select;
	}

	// 得到设定单张地图碎片的尺寸
	public static int getsMap_width() {
		return sMap_width;
	}

	public static void setsMap_width(int sMap_width) {
		DataStore.sMap_width = sMap_width;
	}

	public static int getsMap_height() {
		return sMap_height;
	}

	public static void setsMap_height(int sMap_high) {
		DataStore.sMap_height = sMap_high;
	}

}
