package com.wuguowei.chatvoice;

import java.util.List;

import com.wuguowei.chatvoice.MainActivity.Recorder;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class RecorderAdapter extends ArrayAdapter<Recorder> {
	private int mMinItemwidth;
	private int mMaxItemwidth;
	private LayoutInflater mInflater;

	public RecorderAdapter(Context context, List<Recorder> datas) {
		super(context, -1, datas);// -1代表不使用它自带的，等会儿我自己指定
		mInflater = LayoutInflater.from(context);
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		mMaxItemwidth = (int) (outMetrics.widthPixels * 0.7f);
		mMinItemwidth = (int) (outMetrics.widthPixels * 0.15f);
	}
	// 重写getView，来控制item的显示

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_recorder, parent, false);
			holder = new ViewHolder();
			holder.seconds = (TextView) convertView.findViewById(R.id.id_recorder_time);
			holder.length = convertView.findViewById(R.id.id_recorder_length);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.seconds.setText(Math.round(getItem(position).time) + "\"");// +"\""是为了转换成秒
		ViewGroup.LayoutParams lp = holder.length.getLayoutParams();
		lp.width = (int) (mMinItemwidth + (mMaxItemwidth / 60f * getItem(position).time));
		return convertView;
	}

	private class ViewHolder {
		TextView seconds;
		View length;
	}

}
