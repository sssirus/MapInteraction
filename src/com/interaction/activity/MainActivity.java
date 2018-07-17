package com.interaction.activity;

import com.iflytek.speech.ErrorCode;
import com.iflytek.speech.ISpeechModule;
import com.iflytek.speech.InitListener;
import com.iflytek.speech.RecognizerListener;
import com.iflytek.speech.RecognizerResult;
import com.iflytek.speech.SpeechConstant;
import com.iflytek.speech.SpeechRecognizer;
import com.iflytek.speech.setting.IatSettings;
import com.iflytek.speech.util.JsonParser;
import com.interaction.data.DataCalaulate;
import com.interaction.data.DataStore;
import com.interaction.view.ImageOperationView;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class MainActivity extends Activity {
	private boolean start=false;
	private SpeechRecognizer mIat;
	private SharedPreferences mSharedPreferences;
	
	String TAG = "MAIN_ACTIVITY";// 标示符

	private static Bitmap map_total;// map对象
	private static Bitmap map_now;// map对象

	private static ImageOperationView imageView_display;// 显示区域

	private RadioGroup radioGroup_ModeSelect;
	private RadioButton radioButton_touch;
	private RadioButton radioButton_speak;
	private RadioButton radioButton_sensor;
	Intent intent =null;

	// private RelativeLayout relativeLayout_title;//标题

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 隐藏标题栏
		setContentView(R.layout.activity_main);// 加载布局
		
		mIat = new SpeechRecognizer(this, mInitListener);
		mSharedPreferences = getSharedPreferences(IatSettings.PREFER_NAME, Activity.MODE_PRIVATE);
		DataStore.setMainActivity(this);
		getScreenSize();
		initialize();
	}

	// 初始化变量
	public void initialize() {
		imageView_display = (ImageOperationView) findViewById(R.id.imageview_display);// 显示初始化
		imageView_display.setMainActivity(this);

		// relativeLayout_title=(RelativeLayout)findViewById(R.id.area_title);//标题栏初始化
		// relativeLayout_title.getBackground().setAlpha(230);//透明度

		radioGroup_ModeSelect = (RadioGroup) findViewById(R.id.area_select);// 单选按钮的初始化
		radioButton_touch = (RadioButton) findViewById(R.id.select_touch);
		radioButton_speak = (RadioButton) findViewById(R.id.select_speak);
		radioButton_sensor = (RadioButton) findViewById(R.id.select_sensor);
		radioGroup_ModeSelect
				.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						int mod = 0;
						if (checkedId == radioButton_touch.getId()) {
							mod = 0;
						}
						if (checkedId == radioButton_speak.getId()) {
							mod = 1;
						}
						if (checkedId == radioButton_sensor.getId()) {
							mod = 2;
						}
						DataStore.setSelect(mod);
						Log.v(TAG, mod + "");
						if(mod==1){
							//这里写语音识别的部分。
//							intent=new Intent(getApplicationContext(), VoiceInteraction.class);
							if(start){
								start=false;
								setParam();
								mIat.startListening(mRecognizerListener);
							}else {
								start=true;
								mIat.stopListening(mRecognizerListener);
							}
						}
					}
				});

		// --------------------------------------------------------------------------------
		map_total = BitmapFactory.decodeResource(getResources(),
				R.drawable.qingming_01);// 载入整张地图的一块碎片
		DataStore.setsMap_width(map_total.getWidth());// 初始化地图碎片数据
		DataStore.setsMap_height(map_total.getHeight());

		Log.v("initial", "a slice map----->height:" + map_total.getHeight()
				+ " width:" + map_total.getWidth());

		DataStore.setMapWidth(map_total.getWidth() * 50);// 初始化地图数据
		DataStore.setMapHeight(map_total.getHeight() * 2);

		Log.v("initial",
				"the whole map------->height:" + DataStore.getMapHeight()
						+ " width:" + DataStore.getMapWidth());

		DataStore.setPlayerX(DataStore.getMapWidth() / 2);// 初始化x and y 在地图中央
		DataStore.setPlayerY(DataStore.getMapHeight() / 2);

		Log.v("initial", "center of map------->x:" + DataStore.getPlayerX()
				+ " y:" + DataStore.getPlayerY());

		DataStore.setWidth(DataStore.getScreen_width());
		DataStore.setHeight(DataStore.getScreen_height()-200);

		Log.v("initial", "displayed--------->height:" + DataStore.getHeight()
				+ " width:" + DataStore.getWidth());

		drawBitmap();
	}
	
	/**
	 * 参数设置
	 * @param param
	 * @return 
	 */
	public void setParam(){
		
		mIat.setParameter(SpeechConstant.LANGUAGE, mSharedPreferences.getString("iat_language_preference", "zh_cn"));
		mIat.setParameter(SpeechConstant.VAD_BOS, mSharedPreferences.getString("iat_vadbos_preference", "4000"));
//		mIat.setParameter(SpeechConstant.ACCENT, mSharedPreferences.getString("accent_preference", "mandarin"));
//		mIat.setParameter(SpeechConstant.DOMAIN, mSharedPreferences.getString("domain_perference", "iat"));
		mIat.setParameter(SpeechConstant.VAD_EOS, mSharedPreferences.getString("iat_vadeos_preference", "1000"));
		
		String param = null;
		param = "asr_ptt="+mSharedPreferences.getString("iat_punc_preference", "1");
		mIat.setParameter(SpeechConstant.PARAMS, param+",asr_audio_path=/sdcard/iflytek/wavaudio.pcm");

	}
	
    /**
     * 识别回调。
     */
    private RecognizerListener mRecognizerListener = new RecognizerListener.Stub() {
        
        @Override
        public void onVolumeChanged(int v) throws RemoteException {
//            showTip("onVolumeChanged："	+ v);
        }
        
        @Override
        public void onResult(final RecognizerResult result, boolean isLast)
                throws RemoteException {
        	
        	Log.v("zjh", "mRecongnizer--》onResult");
        	runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (null != result) {
		            	// 显示
						Log.d(TAG, "recognizer result：" + result.getResultString());
						String iattext = JsonParser.parseIatResult(result.getResultString());
						Log.v("zjh", iattext);
						voicemove(iattext);
//						EditText editor = ((EditText)findViewById(R.id.iat_text));
//						String text = editor.getText().toString()+iattext;
//						editor.setText(text);
		            } else {
		                Log.d("zjh", "recognizer result : null");
//		                showTip("无识别结果");
		            }	
				}
			});
            
        }
        
        
        
        @Override
        public void onError(int errorCode) throws RemoteException {
        	Log.v("zjh","onError Code："	+ errorCode);
//			showTip("onError Code："	+ errorCode);
        }
        
        @Override
        public void onEndOfSpeech() throws RemoteException {
        	Log.v("zjh","onEndOfSpeech");
//			showTip("onEndOfSpeech");
        }
        
        @Override
        public void onBeginOfSpeech() throws RemoteException {
        	Log.v("zjh","onBeginOfSpeech");
//			showTip("onBeginOfSpeech");
        }
    };
    
    
    //得到语音指令后进行相关的动作
    public void voicemove(String vc){
      
       if(vc.equals("市区街道")){
    	   DataStore.setPlayerX(21499);
    	   DataStore.setPlayerY(772);
    	   
       }else if(vc.equals("汴河码头")){
    	   DataStore.setPlayerX(11089);
    	   DataStore.setPlayerY(772);
    	   
       }else if (vc.equals("汴京郊野")) {
    	   DataStore.setPlayerX(38617);
    	   DataStore.setPlayerY(772);
		
	   }else if (vc.equals("上")) {
		   DataStore.setPlayerY(DataStore.getPlayerY()-100);
		
	   }else if (vc.equals("下")){
		   DataStore.setPlayerY(DataStore.getPlayerY()+100);
		   
	   }else if(vc.equals("左")||vc.equals("做")||vc.equals("卓")||vc.equals("作")||vc.equals("坐")||vc.equals("座")){
		   DataStore.setPlayerX(DataStore.getPlayerX()-100);//模糊识别
		   
	   }else if (vc.equals("又")||vc.equals("右")||vc.equals("幼")){
		   DataStore.setPlayerX(DataStore.getPlayerX()+100);
	   }else if (vc.equals("大")) {
		//控制音量变大
		   
	   }else if (vc.equals("小")) {
		//控制音量变小
		   
	   }
       drawBitmap();//刷新
    }
    
    
    
	@Override
	protected void onDestroy() {
	    super.onDestroy();
	    // 退出时释放连接
	    mIat.cancel(mRecognizerListener);
	    mIat.destory();
	}
	
	
    /**
     * 初期化监听器。
     */
    private InitListener mInitListener = new InitListener() {

		@Override
		public void onInit(ISpeechModule module, int code) {
			Log.d(TAG, "SpeechRecognizer init() code = " + code);
        	if (code == ErrorCode.SUCCESS) {
//        		findViewById(R.id.iat_recognize_bind).setEnabled(true);
//        		findViewById(R.id.iat_recognize_intent).setEnabled(true);
        	}
		}
    };

	// 得到屏幕尺寸
	public void getScreenSize() {
		// WindowManager windowManager=this.getWindowManager();
		DisplayMetrics displayMetrics = new DisplayMetrics();// 得到屏幕尺寸的工具
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

		DataStore.setScreen_height(displayMetrics.heightPixels);// 设置屏幕尺寸
		DataStore.setScreen_width(displayMetrics.widthPixels);

		Log.v("initial", "Screen---->height:" + displayMetrics.heightPixels
				+ " width:" + displayMetrics.widthPixels);

		DataStore.setHeight(displayMetrics.heightPixels);
		DataStore.setWidth(displayMetrics.widthPixels);// 初始化屏幕大小


	}

	// 画图方法
	public void drawBitmap() {
		// map_now=Bitmap.createBitmap(map_total,DataStore.getX1(),DataStore.getY1(),DataStore.getWidth(),DataStore.getHeight());
		// imageView_display.setImageBitmap(map_now);
		
		

		DataStore.adjustment();// 调整参数
		
		try {
			imageView_display.setImageBitmap(addition());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.err.println("产生了一个错误！");
			e.printStackTrace();
		}

	}

	// 分图的id
	private int mapId[] = { R.drawable.qingming_01, R.drawable.qingming_02,
			R.drawable.qingming_03, R.drawable.qingming_04,
			R.drawable.qingming_05, R.drawable.qingming_06,
			R.drawable.qingming_07, R.drawable.qingming_08,
			R.drawable.qingming_09, R.drawable.qingming_10,
			R.drawable.qingming_11, R.drawable.qingming_12,
			R.drawable.qingming_13, R.drawable.qingming_14,
			R.drawable.qingming_15, R.drawable.qingming_16,
			R.drawable.qingming_17, R.drawable.qingming_18,
			R.drawable.qingming_19, R.drawable.qingming_20,
			R.drawable.qingming_21, R.drawable.qingming_22,
			R.drawable.qingming_23, R.drawable.qingming_24,
			R.drawable.qingming_25, R.drawable.qingming_26,
			R.drawable.qingming_27, R.drawable.qingming_28,
			R.drawable.qingming_29, R.drawable.qingming_30,
			R.drawable.qingming_31, R.drawable.qingming_32,
			R.drawable.qingming_33, R.drawable.qingming_34,
			R.drawable.qingming_35, R.drawable.qingming_36,
			R.drawable.qingming_37, R.drawable.qingming_38,
			R.drawable.qingming_39, R.drawable.qingming_40,
			R.drawable.qingming_41, R.drawable.qingming_42,
			R.drawable.qingming_43, R.drawable.qingming_44,
			R.drawable.qingming_45, R.drawable.qingming_46,
			R.drawable.qingming_47, R.drawable.qingming_48,
			R.drawable.qingming_49, R.drawable.qingming_50,
			R.drawable.qingming_51, R.drawable.qingming_52,
			R.drawable.qingming_53, R.drawable.qingming_54,
			R.drawable.qingming_55, R.drawable.qingming_56,
			R.drawable.qingming_57, R.drawable.qingming_58,
			R.drawable.qingming_59, R.drawable.qingming_60,
			R.drawable.qingming_61, R.drawable.qingming_62,
			R.drawable.qingming_63, R.drawable.qingming_64,
			R.drawable.qingming_65, R.drawable.qingming_66,
			R.drawable.qingming_67, R.drawable.qingming_68,
			R.drawable.qingming_69, R.drawable.qingming_70,
			R.drawable.qingming_71, R.drawable.qingming_72,
			R.drawable.qingming_73, R.drawable.qingming_74,
			R.drawable.qingming_75, R.drawable.qingming_76,
			R.drawable.qingming_77, R.drawable.qingming_78,
			R.drawable.qingming_79, R.drawable.qingming_80,
			R.drawable.qingming_81, R.drawable.qingming_82,
			R.drawable.qingming_83, R.drawable.qingming_84,
			R.drawable.qingming_85, R.drawable.qingming_86,
			R.drawable.qingming_87, R.drawable.qingming_88,
			R.drawable.qingming_89, R.drawable.qingming_90,
			R.drawable.qingming_91, R.drawable.qingming_92,
			R.drawable.qingming_93, R.drawable.qingming_94,
			R.drawable.qingming_95, R.drawable.qingming_96,
			R.drawable.qingming_97, R.drawable.qingming_98,
			R.drawable.qingming_99, R.drawable.qingming_100 };

	// 拼接
	public Bitmap addition() throws Exception{
		Canvas canvas = null;
		Bitmap bitmap_addition = null;
		// 新建bitmap
		try {

			bitmap_addition = Bitmap.createBitmap(DataStore.getWidth(),
					DataStore.getHeight(), Config.ARGB_8888);
			canvas = new Canvas(bitmap_addition);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("catch exception:" + DataStore.getWidth() + " "
					+ DataStore.getHeight());
			// TODO: handle exception
		}
		// 得到图片的数量
		int start_c = DataStore.getX1() / DataStore.getsMap_width();
		int start_l = DataStore.getY1() / DataStore.getsMap_height();
		int end_c = DataStore.getX2() / DataStore.getsMap_width();
		int end_l = DataStore.getY2() / DataStore.getsMap_height();
		
		// 对不对？
		if ((DataStore.getX1() + DataStore.getWidth())
				% DataStore.getsMap_width() == 0)
			end_c = end_c - 1;
		if ((DataStore.getY1() + DataStore.getHeight())
				% DataStore.getsMap_height() == 0)
			end_l--;

		Log.v("cutmap", "x1:" + DataStore.getX1() + " x2:" + DataStore.getX2()
				+ " y1:" + DataStore.getY1() + " y2:" + DataStore.getY2());
		Log.v("cutmap", "start_c:" + start_c + " end_c:" + end_c + " start_l:"
				+ start_l + " end_l:" + end_l);

		// 双循环拼图
		for (int j = 0; j <= end_l - start_l; j++) {
			for (int i = 0; i <= end_c - start_c; i++) {
				// 碎片编号，从0开始，到99
				int photosId = DataStore.getX1() / DataStore.getsMap_width()/* 单张图片宽度 */
						+ i + 50 * j;

				// 第一行
				if (j == 0) {
					if (i == 0)// 第一列
					{
						Log.v(TAG, "asd " + DataStore.getsMap_width() + " "
								+ DataStore.getsMap_height());
						Log.v(TAG, "asd " + DataCalaulate.getInsideX() + " "
								+ DataCalaulate.getInsideX());
						Log.v(TAG, "asd " + DataCalaulate.getHead_width() + " "
								+ DataCalaulate.getHead_height());
						Log.v(TAG, "x:"+DataStore.getPlayerX()+" ,"+"y:"+DataStore.getPlayerY());

						Bitmap now = BitmapFactory.decodeResource(
								getResources(), mapId[photosId]);
						Bitmap now_exact = Bitmap.createBitmap(now,
								DataCalaulate.getInsideX(),
								DataCalaulate.getInsideY(),
								DataCalaulate.getHead_width(),
								DataCalaulate.getHead_height());
						canvas.drawBitmap(now_exact, 0, 0, null);

					}
					if (i == end_c - start_c)// 最后一列
					{
						Bitmap now = BitmapFactory.decodeResource(
								getResources(), mapId[photosId]);
						Bitmap now_exact = Bitmap.createBitmap(now, 0,
								DataCalaulate.getInsideY(),
								DataCalaulate.getEnd_width(),
								DataCalaulate.getHead_height());
						canvas.drawBitmap(now_exact,
								DataCalaulate.getOutsideX(i), 0, null);
					} else// 在中间
					{
						Bitmap now = BitmapFactory.decodeResource(
								getResources(), mapId[photosId]);
						Bitmap now_exact = Bitmap.createBitmap(now, 0,
								DataCalaulate.getInsideY(),
								DataStore.getsMap_width(),
								DataCalaulate.getHead_height());
						canvas.drawBitmap(now_exact,
								DataCalaulate.getOutsideX(i), 0, null);
					}

				} else {
					// 最后一行
					if (j == end_l - start_l) {
						if (i == 0)// 第一列
						{
							Bitmap now = BitmapFactory.decodeResource(
									getResources(), mapId[photosId]);
							Log.e("height", ""+DataCalaulate.getEnd_height());
							Bitmap now_exact = Bitmap.createBitmap(now,
									DataCalaulate.getInsideX(), 0,
									DataCalaulate.getHead_width(),
									DataCalaulate.getEnd_height());
							
							canvas.drawBitmap(now_exact, 0,
									DataCalaulate.getOutsideY(j), null);
						}

						else {
							if (i == end_c - start_c)// 最后一列
							{
								Bitmap now = BitmapFactory.decodeResource(
										getResources(), mapId[photosId]);
								Bitmap now_exact = Bitmap.createBitmap(now, 0,
										0, DataCalaulate.getEnd_width(),
										DataCalaulate.getEnd_height());
								canvas.drawBitmap(now_exact,
										DataCalaulate.getOutsideX(i),
										DataCalaulate.getOutsideY(j), null);
							} else// 在中间
							{
								Bitmap now = BitmapFactory.decodeResource(
										getResources(), mapId[photosId]);
								Bitmap now_exact = Bitmap.createBitmap(now, 0,
										0, DataStore.getsMap_width(),
										DataCalaulate.getEnd_height());
								canvas.drawBitmap(now_exact,
										DataCalaulate.getOutsideX(i),
										DataCalaulate.getOutsideY(j), null);
							}
						}

					}
					// 中间
					else {
						if (i == 0)// 第一列
						{
							Bitmap now = BitmapFactory.decodeResource(
									getResources(), mapId[photosId]);
							Bitmap now_exact = Bitmap.createBitmap(now,
									DataCalaulate.getInsideX(), 0,
									DataCalaulate.getHead_width(),
									DataStore.getsMap_height());
							canvas.drawBitmap(now_exact, 0,
									DataCalaulate.getOutsideY(j), null);
						}

						else {
							if (i == end_c - start_c)// 最后一列
							{
								Bitmap now = BitmapFactory.decodeResource(
										getResources(), mapId[photosId]);
								Bitmap now_exact = Bitmap.createBitmap(now, 0,
										0, DataCalaulate.getEnd_width(),
										DataStore.getsMap_height());
								canvas.drawBitmap(now_exact,
										DataCalaulate.getOutsideX(i),
										DataCalaulate.getOutsideY(j), null);
							} else// 在中间
							{
								Bitmap now = BitmapFactory.decodeResource(
										getResources(), mapId[photosId]);
								Bitmap now_exact = Bitmap.createBitmap(now, 0,
										0, DataStore.getsMap_width(),
										DataStore.getsMap_height());
								canvas.drawBitmap(now_exact,
										DataCalaulate.getOutsideX(i),
										DataCalaulate.getOutsideY(j), null);
							}
						}

					}

				}
			}

		}

		return bitmap_addition;
	}

	// public class test extends Thread
	// {
	// public test()
	// {}
	// public void start()
	// {
	// super.start();
	// }
	// @Override
	// public void run()
	// {
	// Message msg = new Message();
	//
	// while(true)
	// {
	// if(DataStore.getX1()+DataStore.getWide()==DataStore.getMapWidth())
	// {
	// break;
	//
	// }
	// DataStore.setPlayerX(DataStore.getPlayerX()+1);
	// Log.v(TAG, "hehe"+DataStore.getPlayerX());
	// msg.what = DRAW;
	// handler.sendMessage(msg);
	// drawBitmap();
	// try
	// {
	// sleep(100);
	// }
	// catch (InterruptedException e)
	// {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	// }
	// }
	//
	// private static final int DRAW=0;
	// private Handler handler=new Handler()
	// {
	// @Override
	// public void handleMessage(Message msg)
	// {
	// if(msg.what==DRAW)
	// drawBitmap();
	// }
	// };
	//
	//
	//
	//
	//
	//
	//
	//
	//
	//
	// private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;
	//
	// public void Speech ()
	// {
	// try
	// {
	// Intent intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);//开启语音
	// //语音模式、自由模式的识别
	// intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
	// RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
	// //提示语音开始
	// intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "开始语音");
	// //开始语音识别
	// startActivityForResult(intent, 1234);
	// }
	// catch (Exception e)
	// {
	// Log.v(TAG, "语音识别错误");
	// Toast.makeText(getApplicationContext(), "找不到设备",1).show();
	// }
	// }
	//
	// protected void onActivityResult(int requestCode,int resultCode,Intent
	// data)
	// {
	// if(requestCode==VOICE_RECOGNITION_REQUEST_CODE&&requestCode==RESULT_OK)
	// {
	// ArrayList<String>
	// results=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
	// String resultString="";
	// for (int i = 0; i < results.size(); i++)
	// {
	// resultString+=results.get(i);
	// }
	// Toast.makeText(getApplicationContext(), resultString, 1).show();
	// }
	// super.onActivityResult(requestCode, resultCode, data);
	// }
	//
	//

}
