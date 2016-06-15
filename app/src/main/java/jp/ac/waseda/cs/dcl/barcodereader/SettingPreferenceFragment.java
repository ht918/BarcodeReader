package jp.ac.waseda.cs.dcl.barcodereader;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by pineappler on 2016/06/14.
 */
public class SettingPreferenceFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
    }
}
