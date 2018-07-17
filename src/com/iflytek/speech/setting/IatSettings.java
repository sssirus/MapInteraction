package com.iflytek.speech.setting;

import com.iflytek.speech.util.SettingTextWatcher;
import com.interaction.activity.R;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.view.Window;

/**
 * 设置画面。
 *
 */
public class IatSettings extends PreferenceActivity implements OnPreferenceChangeListener {
	
	public static final String PREFER_NAME = "com.iflytek.setting";
	private String TAG = "IatSettings";
	private SharedPreferences mSharedPreferences;
	private EditTextPreference mVadbosPreference;
	private EditTextPreference mVadeosPreference;
	
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		getPreferenceManager().setSharedPreferencesName(PREFER_NAME);
//		addPreferencesFromResource(R.xml.iat_setting);
		
		mVadbosPreference = (EditTextPreference)findPreference("iat_vadbos_preference");
		mVadbosPreference.getEditText().addTextChangedListener(new SettingTextWatcher(IatSettings.this,mVadbosPreference,3));
		
		mVadeosPreference = (EditTextPreference)findPreference("iat_vadeos_preference");
		mVadeosPreference.getEditText().addTextChangedListener(new SettingTextWatcher(IatSettings.this,mVadeosPreference,4));
		
		mSharedPreferences = getSharedPreferences(IatSettings.PREFER_NAME, Activity.MODE_PRIVATE);
	}
	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		return true;
	}
}
