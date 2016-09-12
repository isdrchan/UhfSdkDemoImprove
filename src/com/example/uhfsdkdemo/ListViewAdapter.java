package com.example.uhfsdkdemo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	private Map<String, Boolean> ckbStatus = new HashMap<String, Boolean>(); // 用于记录每个RadioButton的状态，并保证只可选一个
	
	public ListViewAdapter(Context context, List<EPC> epcList) {
		this.context = context;
		this.epcList = epcList;
		//listview动态刷新，需要初始化为刷新前已选的epc
		for(EPC epc:epcList) {
			String epcValue = epc.getEpc();
			if(epcValue == Data.getChooseEPC()) {
				ckbStatus.put(epcValue, true);
			} else {
				ckbStatus.put(epcValue, false);
			}
		}
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(context);
		convertView = inflater.inflate(R.layout.listview_item, null);
		
		tvId = (TextView) convertView.findViewById(R.id.textView_id);
		tvEPC = (TextView) convertView.findViewById(R.id.textView_epc);
		tvCount = (TextView) convertView.findViewById(R.id.textView_count);
		tvId.setText(epcList.get(position).getId() + "");
		tvEPC.setText(epcList.get(position).getEpc());
		tvCount.setText(epcList.get(position).getCount() + "");
				
		//当RadioButton被选中时，将其状态记录进States中，并更新其他RadioButton的状态使它们不被选中 
		final RadioButton radio= (RadioButton) convertView.findViewById(R.id.rb);
		radio.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// 重置，确保最多只有一项被选中
		        for(EPC epc:epcList) {
		        	ckbStatus.put(epc.getEpc(), false);
				}
		        ckbStatus.put(epcList.get(position).getEpc(), radio.isChecked());  
		        ListViewAdapter.this.notifyDataSetChanged();
			}
		});
		
		boolean res = false;  
		if(ckbStatus.get(epcList.get(position).getEpc()) == null || ckbStatus.get(epcList.get(position).getEpc())== false){  
			res = false;  
			ckbStatus.put(epcList.get(position).getEpc(), false);  
		} else {
			res = true;  
			Data.setChooseEPC(epcList.get(position).getEpc()); //选中的epc记录到Data
		}
		radio.setChecked(res);  

		return convertView;
	}

}
