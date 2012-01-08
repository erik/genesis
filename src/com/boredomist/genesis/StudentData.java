package com.boredomist.genesis;

import java.util.ArrayList;
import java.util.HashMap;

public class StudentData {
	// keys: name, studentId, stateId, grade, counselor, age, birthday

	public HashMap<String, String> data;
	public ArrayList<HashMap<String, String>> courses;

	public StudentData() {
		this.data = new HashMap<String, String>();
		this.courses = new ArrayList<HashMap<String, String>>();
	}

	public StudentData(HashMap<String, String> data,
			ArrayList<HashMap<String, String>> array) {
		this.data = new HashMap<String, String>(data);
		this.courses = new ArrayList<HashMap<String, String>>(array);
	}

	public void addClass(HashMap<String, String> s) {
		this.courses.add(s);
	}

	public HashMap<String, String> getClass(int index) {
		return this.courses.get(index);
	}

	public void put(String k, String v) {
		this.data.put(k, v);
	}

	public String get(String k) {
		return this.data.get(k);
	}

	// TODO: include classes in the string representation?
	public String toString() {

		return get("name") + " (" + get("studentId") + "): born "
				+ get("birthday") + ", " + get("age") + " years old, grade: "
				+ get("grade") + ", counselor: " + get("counselor");
	}
}
