package com.boredomist.genesis;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

public class StudentListActivity extends Activity {
	private LoginSession mSession;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		Bundle b = intent.getExtras();
		mSession = (LoginSession) b.getParcelable("session");

		setContentView(R.layout.student_list);

		ExpandableListView list = (ExpandableListView) findViewById(R.id.student_list);
		list.setAdapter(new StudentDataAdapter(mSession.students));

		setTitle("Select a student");
	}

	// TODO: actually implement this adapter
	private class StudentDataAdapter extends BaseExpandableListAdapter {

		private ArrayList<StudentData> mStudentData;
		private LayoutInflater mInflater;

		public StudentDataAdapter(ArrayList<StudentData> data) {
			mStudentData = data;
			mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public Object getChild(int groupPos, int childPos) {
			// TODO: this needs to be changed
			return mStudentData.get(groupPos);
		}

		@Override
		public long getChildId(int groupPos, int childPos) {
			return childPos;
		}

		@Override
		public View getChildView(int groupPos, int childPos,
				boolean isLastChild, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = mInflater.inflate(
						R.layout.student_list_child_item, null);
			}

			StudentData data = mStudentData.get(groupPos);
			HashMap<String, String> klass = data.getClass(childPos);

			TextView tv;

			tv = (TextView) convertView.findViewById(R.id.class_name);
			tv.setText(klass.get("course"));

			tv = (TextView) convertView.findViewById(R.id.class_teacher);
			tv.setText(klass.get("teacher"));

			return convertView;

		}

		@Override
		public int getChildrenCount(int groupPos) {
			return mStudentData.get(groupPos).courses.size();
		}

		@Override
		public Object getGroup(int groupPos) {
			return mStudentData.get(groupPos);
		}

		@Override
		public int getGroupCount() {
			return mStudentData.size();
		}

		@Override
		public long getGroupId(int groupPos) {
			return groupPos;
		}

		@Override
		public View getGroupView(int groupPos, boolean isExpanded,
				View convertView, ViewGroup parent) {

			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.student_list_item,
						null);
			}

			final StudentData data = mStudentData.get(groupPos);
			TextView tv;

			tv = (TextView) convertView.findViewById(R.id.student_name);
			tv.setText(data.get("name"));
			tv.setHeight(100);

			tv = (TextView) convertView.findViewById(R.id.student_grade);
			tv.setText("Grade " + data.get("grade"));

			/*
			 * convertView .setOnCreateContextMenuListener(new
			 * View.OnCreateContextMenuListener() {
			 * 
			 * @Override public void onCreateContextMenu(ContextMenu menu, View
			 * v, ContextMenu.ContextMenuInfo menuInfo) { StudentData s = data;
			 * 
			 * menu.setHeaderTitle("Details for " + s.get("name"));
			 * 
			 * menu.add("Birthday:\t" + s.get("birthday") + " (" + s.get("age")
			 * + " years old)"); menu.add("Grade:\t" + s.get("grade"));
			 * 
			 * menu.add("Student Id:\t" + s.get("studentId"));
			 * menu.add("State Id:\t" + s.get("stateId")); } });
			 */
			return convertView;
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

		@Override
		public boolean isChildSelectable(int groupPos, int childPos) {
			return true;
		}

	}

}
