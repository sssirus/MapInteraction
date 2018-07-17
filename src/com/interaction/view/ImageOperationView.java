package com.interaction.view;

import com.interaction.activity.MainActivity;
import com.interaction.data.DataStore;

import android.content.Context;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

public class ImageOperationView extends ImageView {

	private MainActivity mainActivity;

	public void setMainActivity(MainActivity mainActivity) {
		this.mainActivity = mainActivity;
	}

	public ImageOperationView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public ImageOperationView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public ImageOperationView(Context context) {// 构造方法
		super(context);
		// TODO Auto-generated constructor stub
	}

	// 拖拽or缩放
	private enum MODE {
		NONE, DRAG, ZOOM
	};

	private MODE mode = MODE.NONE;// 默认模式
	private boolean isScaleAnim = false;// 缩放动画
	boolean mIsWaitUpEvent = false;
	boolean mIsWaitDoubleClick = false;// 是否双击
	float first, second;
	private boolean anyabingtuozhuai = false;// 是否按压并缩放
	private boolean mIsLongPressed = false;// 是否长按
	private float longx, longy, duodianx1, duodiany1, duodianx2, duodiany2,
			rawx, rawy;// 需要的参数（就是各种点的坐标）
	long time1, time2;// 需要的参数（时间）

	/***
	 * touch 事件
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		/** 处理单点、多点触摸 **/
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		/**
		 * 可以看到，and运算的结果总是小于等于0x000000ff， 那就是说and之后，无论你多少根手指加进来，
		 * 都是会ACTION_POINTER_DOWN或者ACTION_POINTER_UP
		 **/
		case MotionEvent.ACTION_DOWN: {
			mode = MODE.DRAG;
			try {
				time1 = event.getEventTime();
				longx = event.getX();
				longy = event.getY();
				rawx = event.getRawX();
				rawy = event.getRawY();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
//				e.printStackTrace();
			}
			break;
		}
		// 多点触摸
		case MotionEvent.ACTION_POINTER_DOWN: {
			Log.v("April", "多点触摸");
			if (event.getPointerCount() == 2) {

				/** 两个手指 只能放大缩小 **/

				mode = MODE.ZOOM;
				first = getDistance(event);
				try {
					duodianx1 = event.getX(0);
					duodiany1 = event.getY(0);
					duodianx2 = event.getX(1);
					duodiany2 = event.getY(1);
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
//					e.printStackTrace();
				}

			}
			break;
		}
		case MotionEvent.ACTION_MOVE: {
			if (event.getPointerCount() == 2) {
				float moveX1 = 0;
				float moveY1 = 0;
				try {
					moveX1 = Math.abs(longx - event.getX());
					moveY1 = Math.abs(longy - event.getY());
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
//					e.printStackTrace();
				}
				long timenow = event.getEventTime();
				long time = timenow - time1;

				if (moveX1 < 10 && moveY1 < 10 && time >= 500) {
					anyabingtuozhuai = true;
					Log.e("April", "进入了——按压并拖拽");

					break;
				}

			}
			// 检测是否长按,在非长按时检测
			mIsLongPressed = isLongPressed(longx, longy, event.getX(),
					event.getY(), event.getEventTime(), event.getDownTime(),
					500);

			if (mIsLongPressed && event.getPointerCount() != 2) {
				// 长按模式所做的事
				Log.e("April", "长按");
				changan();
			} else { // 移动模式所做的事
				//Log.e("April", "移动");
				onTouchMove(event);

			}
			break;
		}
		case MotionEvent.ACTION_UP: {
			float x = 0, y = 0;
			try {
				x = Math.abs(event.getRawX() - rawx);
				y =  Math.abs(event.getRawY() - rawy);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
//				e.printStackTrace();
			}
			time2 = event.getEventTime();
			Log.e("time", "" + (time2 - time1));
			if (time2 - time1 > 200||x>20||y>20) {
				mIsWaitUpEvent = false;

				Log.e("April",
						"取消单击");
				break;
			} else {
				mIsWaitUpEvent = false;

				removeCallbacks(mTimerForUpEvent);

				onSingleClick();

			}

			break;
		}
		// 多点松开
		case MotionEvent.ACTION_POINTER_UP: {
			/** 执行缩放还原 **/
			if (isScaleAnim) {
				/***
				 * 缩放动画处理
				 */
			}

			if (anyabingtuozhuai) {
				float x = 0, y = 0;
				try {
					x = event.getX(1) - duodianx2;

					y = event.getY(1) - duodiany2;
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
//					e.printStackTrace();
				}
				if (x > 50)// 向右
				{
					Log.e("April", "向右");
					DataStore.setPlayerX(DataStore.getPlayerX() - (int) x);

				} else if (x < -50)// 向左
				{
					DataStore.setPlayerX(DataStore.getPlayerX() - (int) x);

					Log.e("April", "向左");
				} else if (y < -50)// 向上
				{

					Log.e("April", "向上");

					DataStore.setPlayerY(DataStore.getPlayerY() - (int) y);
				} else {// 向下

					Log.e("April", "向下");

					DataStore.setPlayerY(DataStore.getPlayerY() - (int) y);
				}
				anyabingtuozhuai = false;
			}

			break;
		}

		}
		mainActivity.drawBitmap();
		return true;
	}

	/**
	 * * 判断是否有长按动作发生 * @param lastX 按下时X坐标 * @param lastY 按下时Y坐标 *
	 * 
	 * @param thisX
	 *            移动时X坐标 *
	 * @param thisY
	 *            移动时Y坐标 *
	 * @param lastDownTime
	 *            按下时间 *
	 * @param thisEventTime
	 *            移动时间 *
	 * @param longPressTime
	 *            判断长按时间的阀值
	 */
	static boolean isLongPressed(float lastX, float lastY, float thisX,
			float thisY, long lastDownTime, long thisEventTime,
			long longPressTime) {
		float offsetX = 0, offsetY = 0;
		try {
			offsetX = Math.abs(thisX - lastX);
			offsetY = Math.abs(thisY - lastY);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
		long intervalTime = Math.abs(thisEventTime - lastDownTime);
		if (offsetX <= 10 && offsetY <= 10 && intervalTime >= longPressTime) {
			return true;
		}

		else
			return false;
	}

	Runnable mTimerForSecondClick = new Runnable() {
		@Override
		public void run() {
			if (mIsWaitDoubleClick) {

				mIsWaitDoubleClick = false;
				// at here can do something for singleClick!!
				Log.e("april", "单击");

				DataStore.setWidth(DataStore.getWidth() - 100);
				DataStore.setHeight(DataStore.getHeight() - 100);

			} else {
				Log.e("april",
						"The mTimerForSecondClick has executed, the doubleclick has executed ,so do thing");
			}
		}
	};

	public void onSingleClick() {
		if (mIsWaitDoubleClick) {
			onDoubleClick();
			mIsWaitDoubleClick = false;
			removeCallbacks(mTimerForSecondClick);
		} else {
			mIsWaitDoubleClick = true;
			postDelayed(mTimerForSecondClick, 500);
		}
	}

	public void onDoubleClick() {
		Log.e("April", "双击");
		DataStore.setWidth(DataStore.getWidth() + 100);
		DataStore.setHeight(DataStore.getHeight() + 100);
	}

	Runnable mTimerForUpEvent = new Runnable() {
		public void run() {
			if (mIsWaitUpEvent) {
				Log.e("April",
						"The mTimerForUpEvent has executed, so set the mIsWaitUpEvent as false");
				mIsWaitUpEvent = false;
			} else {
				Log.e("April",
						"The mTimerForUpEvent has executed, mIsWaitUpEvent is false,so do nothing");
			}
		}
	};

	public void changan() {
		
		DataStore.setPlayerX(DataStore.getMapWidth() / 2);// 初始化x and y 在地图中央
		DataStore.setPlayerY(DataStore.getMapHeight() / 2);
		
		DataStore.setWidth(DataStore.getScreen_width());
		DataStore.setHeight(DataStore.getScreen_height() / 2);
	}

	/** 移动的处理 **/
	void onTouchMove(MotionEvent event) {
		/** 处理拖动 **/
		if (mode == MODE.DRAG) {
			Log.e("April", "拖动");
			// 拖动动作
			float x = 0, y = 0;
			try {
				x = event.getRawX() - rawx;
				y = event.getRawY() - rawy;
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
//				e.printStackTrace();
			}
			DataStore.setPlayerX(DataStore.getPlayerX() - (int) x);
			DataStore.setPlayerY(DataStore.getPlayerY() - (int) y);
		}
		/** 处理缩放 **/
		else if (mode == MODE.ZOOM) {
			Log.e("April", "缩放");
			// 缩放动作
			second = getDistance(event);
			if(second==0)
				return;
			float distance = first - second ;
			Log.e("distance","firstdistance:"+ first);
			Log.e("distance","seconddistance:"+ second);
			Log.e("distance","firstdistance:"+ distance);
float nowdistance=DataStore.getWidth();
float newdistance=(int) (DataStore.getWidth()+distance);
			DataStore.setWidth((int) (newdistance));
			
			DataStore.setHeight((int)((DataStore.getHeight() *newdistance)/nowdistance));

		}
	}

	float getDistance(MotionEvent event) {
		float x = 0, y = 0;
		try {
			x = event.getX(0) - event.getX(1);
			y = event.getY(0) - event.getY(1);

		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
		return FloatMath.sqrt(x * x + y * y);
	}
}
