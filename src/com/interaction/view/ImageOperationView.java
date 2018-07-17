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

	public ImageOperationView(Context context) {// ���췽��
		super(context);
		// TODO Auto-generated constructor stub
	}

	// ��קor����
	private enum MODE {
		NONE, DRAG, ZOOM
	};

	private MODE mode = MODE.NONE;// Ĭ��ģʽ
	private boolean isScaleAnim = false;// ���Ŷ���
	boolean mIsWaitUpEvent = false;
	boolean mIsWaitDoubleClick = false;// �Ƿ�˫��
	float first, second;
	private boolean anyabingtuozhuai = false;// �Ƿ�ѹ������
	private boolean mIsLongPressed = false;// �Ƿ񳤰�
	private float longx, longy, duodianx1, duodiany1, duodianx2, duodiany2,
			rawx, rawy;// ��Ҫ�Ĳ��������Ǹ��ֵ�����꣩
	long time1, time2;// ��Ҫ�Ĳ�����ʱ�䣩

	/***
	 * touch �¼�
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		/** �����㡢��㴥�� **/
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		/**
		 * ���Կ�����and����Ľ������С�ڵ���0x000000ff�� �Ǿ���˵and֮����������ٸ���ָ�ӽ�����
		 * ���ǻ�ACTION_POINTER_DOWN����ACTION_POINTER_UP
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
		// ��㴥��
		case MotionEvent.ACTION_POINTER_DOWN: {
			Log.v("April", "��㴥��");
			if (event.getPointerCount() == 2) {

				/** ������ָ ֻ�ܷŴ���С **/

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
					Log.e("April", "�����ˡ�����ѹ����ק");

					break;
				}

			}
			// ����Ƿ񳤰�,�ڷǳ���ʱ���
			mIsLongPressed = isLongPressed(longx, longy, event.getX(),
					event.getY(), event.getEventTime(), event.getDownTime(),
					500);

			if (mIsLongPressed && event.getPointerCount() != 2) {
				// ����ģʽ��������
				Log.e("April", "����");
				changan();
			} else { // �ƶ�ģʽ��������
				//Log.e("April", "�ƶ�");
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
						"ȡ������");
				break;
			} else {
				mIsWaitUpEvent = false;

				removeCallbacks(mTimerForUpEvent);

				onSingleClick();

			}

			break;
		}
		// ����ɿ�
		case MotionEvent.ACTION_POINTER_UP: {
			/** ִ�����Ż�ԭ **/
			if (isScaleAnim) {
				/***
				 * ���Ŷ�������
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
				if (x > 50)// ����
				{
					Log.e("April", "����");
					DataStore.setPlayerX(DataStore.getPlayerX() - (int) x);

				} else if (x < -50)// ����
				{
					DataStore.setPlayerX(DataStore.getPlayerX() - (int) x);

					Log.e("April", "����");
				} else if (y < -50)// ����
				{

					Log.e("April", "����");

					DataStore.setPlayerY(DataStore.getPlayerY() - (int) y);
				} else {// ����

					Log.e("April", "����");

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
	 * * �ж��Ƿ��г����������� * @param lastX ����ʱX���� * @param lastY ����ʱY���� *
	 * 
	 * @param thisX
	 *            �ƶ�ʱX���� *
	 * @param thisY
	 *            �ƶ�ʱY���� *
	 * @param lastDownTime
	 *            ����ʱ�� *
	 * @param thisEventTime
	 *            �ƶ�ʱ�� *
	 * @param longPressTime
	 *            �жϳ���ʱ��ķ�ֵ
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
				Log.e("april", "����");

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
		Log.e("April", "˫��");
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
		
		DataStore.setPlayerX(DataStore.getMapWidth() / 2);// ��ʼ��x and y �ڵ�ͼ����
		DataStore.setPlayerY(DataStore.getMapHeight() / 2);
		
		DataStore.setWidth(DataStore.getScreen_width());
		DataStore.setHeight(DataStore.getScreen_height() / 2);
	}

	/** �ƶ��Ĵ��� **/
	void onTouchMove(MotionEvent event) {
		/** �����϶� **/
		if (mode == MODE.DRAG) {
			Log.e("April", "�϶�");
			// �϶�����
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
		/** �������� **/
		else if (mode == MODE.ZOOM) {
			Log.e("April", "����");
			// ���Ŷ���
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
