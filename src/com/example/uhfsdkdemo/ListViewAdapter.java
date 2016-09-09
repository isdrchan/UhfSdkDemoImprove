package com.example.uhfsdkdemo;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

public class ListViewAdapter extends BaseAdapter {

	private Context context;
	private List<EPC> epcList;
	private TextView tvId;
	private TextView tvEPC;
	private TextView tvCount;
	private HashMap<String, Boolean> states = new HashMap<String, Boolean>(); // 用于记录每个RadioButton的状态，并保证只可选一个
	
	public ListViewAdapter(Context context, List<EPC> epcList) {
		this.context = context;
		this.epcList = epcList;
	}
	
	@Override
	public int getCount() {
		return epcList.size();
	}

	@Override
	public EPC getItem(int position) {
		return epcList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return epcList.get(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(context);
		convertView = inflater.inflate(R.layout.listview_item, null);
		
		tvId = (TextView) convertView.findViewById(R.id.textView_id);
		tvEPC = (TextView) convertView.findViewById(R.id.textView_epc);
		tvCount = (TextView) convertView.findViewById(R.id.textView_count);
		tvId.setText(epcList.get(position).getId());
		tvEPC.setText(epcList.get(position).getEpc());
		tvCount.setText(epcList.get(position).getId());
				
		RadioButton radio= (RadioButton) convertView.findViewById(R.id.rb);
		radio.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// 重置，确保最多只有一项被选中
		        for (String key : states.keySet()) {
		          states.put(key, false);

		        }
		        states.put(String.valueOf(position), radio.isChecked());
		        ListViewAdapter.this.notifyDataSetChanged();
			}
		});
		return null;
	}

}
