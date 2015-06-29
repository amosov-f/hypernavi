package ru.hypernavi.app;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Created by bakharevk on 30.06.15.
 */
public class AppActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toast.makeText(AppActivity.this, "Hi!", Toast.LENGTH_SHORT).show();
    }
}
