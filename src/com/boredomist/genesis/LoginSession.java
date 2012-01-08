package com.boredomist.genesis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.os.Parcel;
import android.os.Parcelable;

public class LoginSession implements Parcelable {
	public HashMap<String, String> cookies;
	public ArrayList<StudentData> students;

	public LoginSession() {
		cookies = new HashMap<String, String>();
		students = new ArrayList<StudentData>();
	}

	public LoginSession(Parcel p) {
		cookies = new HashMap<String, String>();
		students = new ArrayList<StudentData>();

		int size = p.readInt();
		for (int i = 0; i < size; ++i) {
			cookies.put(p.readString(), p.readString());
		}

		size = p.readInt();
		for (int i = 0; i < size; ++i) {
			HashMap<String, String> map = new HashMap<String, String>();
			ArrayList<HashMap<String, String>> array = new ArrayList<HashMap<String, String>>();

			int elems = p.readInt();
			for (int j = 0; j < elems; ++j) {
				map.put(p.readString(), p.readString());
			}

			elems = p.readInt();
			for (int j = 0; j < elems; ++j) {
				HashMap<String, String> m = new HashMap<String, String>();

				int n = p.readInt();

				for (int k = 0; k < n; ++k) {
					m.put(p.readString(), p.readString());
				}

				array.add(m);
			}
			students.add(new StudentData(map, array));
		}
	}

	public String getCookie(String name) {
		return cookies.get(name);
	}

	public boolean parseHome(Document home) {
		// Session expired if we're redirected to login page
		if (home.title().equals("Parents Login")) {
			L.e("Session seems to have expired.");
			return false;
		}
		Elements studentTables = home.select("td.fieldlabel > table");

		for (int i = 0; i < studentTables.size(); ++i) {
			StudentData studentData = new StudentData();

			// this is pretty messy, but Genesis' HTML doesn't leave us with
			// many options
			Element student = studentTables.get(i);
			Elements info = student.select("tr.listroweven > td[align=left]");

			String name = student.select("tr > td.fieldlabel > b").first()
					.text();
			String[] ids = student.select("tr.listrowodd > td[align=left]")
					.first().text().split(" ");
			String schoolid = ids[0], stateid = ids[1];
			String grade = info.get(1).text(), counselor = info.get(2).text(), age = info
					.get(3).text(), birthday = info.get(4).text();

			studentData.put("name", name);
			studentData.put("studentId", schoolid);
			studentData.put("stateId", stateid);
			studentData.put("grade", grade);
			studentData.put("counselor", counselor);
			studentData.put("age", age);
			studentData.put("birthday", birthday);

			Elements classes = student.select("table.list").get(3)
					.select("tr.listrowodd, tr.listroweven");
			for (Element klass : classes) {
				HashMap<String, String> map = new HashMap<String, String>();
				Elements elems = klass.select("td");

				map.put("period", elems.get(0).text().trim());
				map.put("course", elems.get(1).text().trim());
				map.put("term", elems.get(2).text().trim());
				map.put("days", elems.get(3).text().trim());
				map.put("room", elems.get(4).text().trim());
				map.put("teacher", elems.get(5).text().trim());

				studentData.addClass(map);
			}

			// remove the "Per Course Sem Days Room Teacher" header
			studentData.courses.remove(0);

			this.students.add(studentData);
			L.i("Adding student: " + studentData);
		}

		// students are presented in reverse alphabetical order by first name,
		// last name. *I think*
		Collections.reverse(students);

		return true;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	// TODO: This just keeps getting uglier. Fix it up eventually.
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(cookies.size());

		for (Entry<String, String> entry : cookies.entrySet()) {
			dest.writeString(entry.getKey());
			dest.writeString(entry.getValue());
		}

		dest.writeInt(students.size());
		for (StudentData student : students) {
			dest.writeInt(student.data.size());

			for (Entry<String, String> entry : student.data.entrySet()) {
				dest.writeString(entry.getKey());
				dest.writeString(entry.getValue());
			}

			dest.writeInt(student.courses.size());

			for (HashMap<String, String> course : student.courses) {
				dest.writeInt(course.size());

				for (Entry<String, String> entry : course.entrySet()) {
					dest.writeString(entry.getKey());
					dest.writeString(entry.getValue());
				}
			}
		}
	}

	public static final Parcelable.Creator<LoginSession> CREATOR = new Parcelable.Creator<LoginSession>() {
		public LoginSession createFromParcel(Parcel in) {
			return new LoginSession(in);
		}

		public LoginSession[] newArray(int size) {
			return new LoginSession[size];
		}
	};

}
