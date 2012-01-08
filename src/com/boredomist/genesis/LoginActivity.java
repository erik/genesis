package com.boredomist.genesis;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class LoginActivity extends Activity implements OnClickListener {

	public static enum DIALOG {
		TRY_LOGIN, LOGIN_FAIL, NOT_CONNECTED;
	}

	public String html;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		Button loginbutton = (Button) findViewById(R.id.loginbutton);
		loginbutton.setOnClickListener(this);

		SharedPreferences prefs = getSharedPreferences("login", 0);
		String email = prefs.getString("email", null);
		String pass = prefs.getString("pass", null);

		if (email != null && pass != null) {
			EditText emailField = (EditText) findViewById(R.id.loginemail);
			EditText passField = (EditText) findViewById(R.id.loginpassword);
			CheckBox box = (CheckBox) findViewById(R.id.loginrememberme);

			emailField.setText(email);
			passField.setText(pass);

			box.setChecked(true);
		}

		GenesisHttpRequest.res = this.getResources();
	}

	@Override
	public void onClick(View view) {
		if (view.getId() != R.id.loginbutton) { return; }

		ConnectivityManager mgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = mgr.getActiveNetworkInfo();
		if (info == null || !info.isConnected()) {
			this.showDialog(DIALOG.NOT_CONNECTED.ordinal());
			return;
		}

		String email = ((EditText) findViewById(R.id.loginemail)).getText()
				.toString();
		String pass = ((EditText) findViewById(R.id.loginpassword)).getText()
				.toString();

		if (email == null || pass == null || email.length() == 0
				|| pass.length() == 0) {
			AlertDialog.Builder builder = new AlertDialog.Builder(
					LoginActivity.this);

			builder.setMessage(
					"You need to enter a valid username and password")
					.setCancelable(true)
					.setPositiveButton("Okay",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.cancel();
								}
							});

			builder.create().show();
			return;
		}

		// try to initiate a session
		new LoginTask(this).execute(email, pass);

		SharedPreferences settings = getSharedPreferences("login", 0);
		SharedPreferences.Editor editor = settings.edit();

		CheckBox box = (CheckBox) findViewById(R.id.loginrememberme);
		if (box.isChecked()) {

			editor.putString("email", email);
			editor.putString("pass", pass);
		} else {
			editor.remove("email");
			editor.remove("pass");
		}

		editor.commit();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		DIALOG d = DIALOG.values()[id];
		switch (d) {
			case TRY_LOGIN:
				ProgressDialog diag = new ProgressDialog(LoginActivity.this);
				diag.setCancelable(false);
				diag.setMessage("Logging in...");
				diag.show();
				return diag;
			case LOGIN_FAIL: {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						LoginActivity.this);

				builder.setCancelable(true)
						.setTitle("Login Failed")
						.setMessage(
								"Make sure your username and password are correct and try again.\n"
										+ "If errors persist, check that parents.sparta.org is available.")
						.setPositiveButton("Okay",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.cancel();
									}
								});

				return builder.create();
			}
			case NOT_CONNECTED: {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						LoginActivity.this);

				builder.setCancelable(true)
						.setTitle("Connection failed")
						.setMessage("You are not connected to a network.")
						.setPositiveButton("Okay",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.cancel();
									}
								});

				return builder.create();
			}

			default:
				return null;
		}
	}

	public void createSession(LoginSession session) {
		L.i("Session creation successful, switching activity...");

		this.dismissDialog(DIALOG.TRY_LOGIN.ordinal());
		
		Intent intent = new Intent(Intent.ACTION_VIEW, null,
				getApplicationContext(), StudentListActivity.class);

		intent.putExtra("session", session);
		startActivity(intent);
	}
}