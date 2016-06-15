package jp.ac.waseda.cs.dcl.barcodereader;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.WindowManager;

/**
 * Created by pineappler on 2016/06/14.
 */
public class SettingsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(android.R.id.content,new SettingPreferenceFragment());
        fragmentTransaction.commit();
    }
}
