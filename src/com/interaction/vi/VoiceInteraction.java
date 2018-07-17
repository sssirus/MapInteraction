package com.interaction.vi;

import com.iflytek.speech.ErrorCode;
import com.iflytek.speech.ISpeechModule;
import com.iflytek.speech.InitListener;
import com.iflytek.speech.RecognizerListener;
import com.iflytek.speech.RecognizerResult;
import com.iflytek.speech.SpeechConstant;
import com.iflytek.speech.SpeechRecognizer;
import com.iflytek.speech.SpeechUtility;
import com.iflytek.speech.util.JsonParser;
import com.interaction.data.DataStore;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;



public class VoiceInteraction extends Service{
	private static String TAG = "VoiceInteraction";
	public static final String PREFER_NAME = "com.iflytek.setting";
	private SharedPreferences mSharedPreferences;
	// 初始化识别对象
	private InitListener mInitListener=new InitListener() {

			@Override
			public void onInit(ISpeechModule module, int code) {
				
	        	Log.v("zjh", "mInitlistener");
			}
	    };	
	;
    SpeechRecognizer mIat =new SpeechRecognizer(this, mInitListener);;

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void onCreate (){
		super.onCreate();
		System.out.println("service begin");
//		//设置参数
//		setParam();
		//实例化
		mSharedPreferences = getSharedPreferences(PREFER_NAME, Activity.MODE_PRIVATE);
		speech();
		
	}
	public void onDestroy(){
		super.onDestroy();
		mIat.cancel(mRecognizerListener);
        mIat.destory();
		System.out.println("service destroyed");
	}
	
	

	
	    
	 
	 private RecognizerListener mRecognizerListener = new RecognizerListener.Stub() {
			@Override
			public void onVolumeChanged(int v) throws RemoteException {
			// 录音音量回调
				 
			}
			@Override
			public void onResult(final RecognizerResult result, boolean isLast)
	                throws RemoteException {
				//我的是一个service
				//嗯嗯，我在想把这个识别的函数转到Activity里边
				//大概就这样了！！
	        	DataStore.getMainActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (null != result) {
			            	// 显示
							Log.d(TAG, "recognizer result：" + result.getResultString());
							String iattext = JsonParser.parseIatResult(result.getResultString());
							
							Log.v("zjh", iattext);
							//一会你说“回家”
							//这里只是获得了语音识别的结果，还要进行处理
							
//							EditText editor = ((EditText)findViewById(R.id.iat_text));
//							String text = editor.getText().toString()+iattext;
//							editor.setText(text);
			            } else {
			                Log.d(TAG, "recognizer result : null");
//			                showTip("无识别结果");
			                //显示无法识别
			                Toast.makeText(DataStore.getMainActivity(), "无法识别", Toast.LENGTH_LONG);
			            }	
					}
				});
	            
	        }
			@Override
			public void onError(int errorCode) throws RemoteException {
			// 错误回调，那这里也可以显示？
				//这儿的错误就不用显示给别人看来，先跑跑再说，完成任务最要紧。
				System.out.println("onError Code："	+ errorCode);
			}
			@Override
			public void onEndOfSpeech() throws RemoteException {
			// 录音结束回调
				System.out.println("endofspeech");
			}
			@Override
			public void onBeginOfSpeech() throws RemoteException {
			// 录音启动回调
				System.out.println("beginofspeech");
			}
			
			};  
	
	 
	public void speech(){
		mIat.startListening(mRecognizerListener);

		// 设置申请的应用的appid
		SpeechUtility.getUtility(this).setAppid("544f51af");
		
		setParam();
		// 转写回话停止
		mIat.stopListening(mRecognizerListener);
		// 取消
		mIat.cancel(mRecognizerListener);
		
			
		
	}
	
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
	
	
	
	
}