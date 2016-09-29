package com.example.uhfsdkdemo.history;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;

import com.example.uhfsdkdemo.ListViewAdapter;
import com.example.uhfsdkdemo.R;

public class HistoryActivity extends Activity {
	
	private ListView lv;
	private Button btnBack;
	private Button btnCleanCache;
	private HistoryListViewAdapter historyListViewAdapter;
	private UHFDBManager uhfDBManager;
	private List<DBEPC> dbEPCList; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.activity_history);
		
		initView();
		
		//从数据库读取所有的epc
		uhfDBManager = new UHFDBManager(HistoryActivity.this);
		dbEPCList = uhfDBManager.selectAll();
		
		historyListViewAdapter = new HistoryListViewAdapter(HistoryActivity.this, dbEPCList);
		lv.setAdapter(historyListViewAdapter);
	}

	private void initView() {
		lv = (ListView) findViewById(R.id.lv_history);
		btnBack = (Button) findViewById(R.id.btn_back_history);
		btnCleanCache = (Button) findViewById(R.id.btn_clean_history);
	}
}
