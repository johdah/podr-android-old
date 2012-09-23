package com.johandahlberg.podr.ui;

import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.johandahlberg.podr.BuildConfig;
import com.johandahlberg.podr.R;
import com.johandahlberg.podr.data.PodrDataHandler;
import com.johandahlberg.podr.data.Subscription;

public class NewSubscriptionActivity extends Activity {
	private static final String LOG_TAG = ".ui.NewSubscriptionActivity";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		if (BuildConfig.DEBUG)
			Log.d(LOG_TAG, "onCreate()");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_newsubscription);

		final Button button = (Button) findViewById(R.id.btn_add);
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				String url = ((EditText) findViewById(R.id.activity_newsubscription_url))
						.getText().toString();

				PodrDataHandler dataHandler = new PodrDataHandler(getApplicationContext());
				try {
					dataHandler.addSubscription(new Subscription(new URL(url)));
				} catch (MalformedURLException e) {
					Log.e(LOG_TAG, "onCreate() - MalformedURLException");
					Toast.makeText(
							getApplicationContext().getApplicationContext(),
							"Malformed URL",
							Toast.LENGTH_SHORT).show();
				}

				finish();
			}
		});

	}

}