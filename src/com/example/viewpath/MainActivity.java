package com.example.viewpath;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {
private Myview myview;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		myview=(Myview) findViewById(R.id.temperature_view);
		new Thread(new Runnable() {
            @Override
            public void run() {
                for (float i = 0; i <=40; i ++) {
                    myview.setCurrentTemp(i);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
	}

}
