package com.example.uhfsdkdemo.history;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.uhfsdkdemo.R;

public class HistoryListViewAdapter extends BaseAdapter {

	private Context context;
	private List<DBEPC> dbEPCList;
	private Bitmap bitmap;
	
	public HistoryListViewAdapter(Context context, List<DBEPC> dbEPCList) {
		this.context = context;
		this.dbEPCList = dbEPCList;
	}
	
	@Override
	public int getCount() {
		return dbEPCList.size();
	}

	@Override
	public DBEPC getItem(int position) {
		return dbEPCList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return dbEPCList.get(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		
		if(convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.listview_item_history, null);
			holder = new ViewHolder();
			
			holder.imgv = (ImageView) convertView.findViewById(R.id.imgv_history);
			holder.tvEPC = (TextView) convertView.findViewById(R.id.tv_epc_history);
			holder.tvTime = (TextView) convertView.findViewById(R.id.tv_time_history);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		//º”‘ÿÕº∆¨
		String picPath = dbEPCList.get(position).getPicPath();
		File file = new File(dbEPCList.get(position).getPicPath());
		if(file.exists()) {
			bitmap = BitmapFactory.decodeFile(picPath);
			holder.imgv.setImageBitmap(bitmap);
		}
		holder.tvEPC.setText(dbEPCList.get(position).getEPC());
		holder.tvTime.setText(dbEPCList.get(position).getTimeString());
		return convertView;
	}
	
	class ViewHolder {
		ImageView imgv;
		TextView tvEPC;
		TextView tvTime;
	}

}
