package com.boredomist.genesis;

import org.jsoup.nodes.Document;

import android.os.AsyncTask;

public class LoginTask extends AsyncTask<String, Void, Boolean> {

	private LoginActivity activity;
	private LoginSession session;

	public LoginTask(LoginActivity activity) {
		this.activity = activity;
	}

	@Override
	protected void onPreExecute() {
		activity.showDialog(LoginActivity.DIALOG.TRY_LOGIN.ordinal());
	}

	@Override
	protected void onPostExecute(Boolean result) {
		activity.dismissDialog(LoginActivity.DIALOG.TRY_LOGIN.ordinal());

		if (!result) activity.showDialog(LoginActivity.DIALOG.LOGIN_FAIL
				.ordinal());
		else
			activity.createSession(session);
	}

	@Override
	protected Boolean doInBackground(String... emailpass) {
		session = new LoginSession();

		String email = emailpass[0];
		String pass = emailpass[1];
		Document doc = null;

		// We need to grab a session cookie first
		session = new GenesisHttpRequest().createSession();
		if (session == null) { return false; }
		L.i("Created session id: " + session.getCookie("JSESSIONID"));

		doc = new GenesisHttpRequest(session).login(email, pass);
		if (doc == null) { return false; }

		// This is a disgusting way of going about this, but we
		// really aren't given much to work with thanks to Genesis being
		// so well thought out.
		String title = doc.title();

		// We've been redirected back to the login page, login failed.
		if (title.equals("Parents Login")) { return false; }

		L.i("I got this title: " + title);

		Document home = new GenesisHttpRequest(session).getURL(activity
				.getResources().getString(R.string.home_url));
		if (home == null) { return false; }

		session.parseHome(home);

		return true;
	}
}
