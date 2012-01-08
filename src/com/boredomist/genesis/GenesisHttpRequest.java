package com.boredomist.genesis;

import java.util.HashMap;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.content.res.Resources;

public class GenesisHttpRequest {

	public static Resources res = null;

	private LoginSession mSession;

	public GenesisHttpRequest(LoginSession session) {
		mSession = session;
	}

	public GenesisHttpRequest() {
		mSession = null;
	}

	public LoginSession createSession() {
		mSession = new LoginSession();

		try {
			Connection.Response resp = buildRequest(R.string.base_url)
					.execute();

			mSession.cookies = new HashMap<String, String>(resp.cookies());
		} catch (Exception e) {
			L.e("Session creation failed: " + e.getMessage());
			e.printStackTrace();
			return null;
		}

		return mSession;
	}

	public Document getURL(String url) {
		try {
			return buildRequest(url).get();
			
		} catch (Exception e) {
			L.e("Failed to fetch " + url + ": " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	public Document login(String email, String pass) {
		try {
			return buildRequest(R.string.login_url).data("j_username", email)
					.data("j_password", pass).post();
		} catch (Exception e) {
			L.e("Login failed: " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	private Connection buildRequest(int id) {
		return buildRequest(res.getString(id));
	}

	private Connection buildRequest(String url) {
		Connection conn = Jsoup.connect(url).userAgent(
				res.getString(R.string.user_agent));

		for (Map.Entry<String, String> entry : mSession.cookies.entrySet()) {
			conn.cookie(entry.getKey(), entry.getValue());
		}

		return conn;
	}

}
