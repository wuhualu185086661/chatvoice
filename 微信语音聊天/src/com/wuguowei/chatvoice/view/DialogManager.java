package com.wuguowei.chatvoice.view;

import com.wuguowei.chatvoice.R;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class DialogManager {
	private Dialog mDialog;
	private ImageView mIcon;
	private ImageView mVoice;
	private TextView mLable;
	private Context mContext;

	public DialogManager(Context context) {
		mContext = context;
	}

	public void showRecordingDialog() {
		mDialog = new Dialog(mContext, R.style.ThemeAudioDialog);
		LayoutInflater inflater = LayoutInflater.from(mContext);
		View view = inflater.inflate(R.layout.dialog_record, null);
		mDialog.setContentView(view);
		mIcon = (ImageView) mDialog.findViewById(R.id.recorder_dialog_icon);
		mVoice = (ImageView) mDialog.findViewById(R.id.recorder_dialog_voice);
		mLable = (TextView) mDialog.findViewById(R.id.id_recorder_dialog_label);
		mDialog.show();
	}

	public void recording() {
		if (mDialog != null && mDialog.isShowing()) {
			mIcon.setVisibility(View.VISIBLE);
			mVoice.setVisibility(View.VISIBLE);
			mLable.setVisibility(View.VISIBLE);
			mIcon.setImageResource(R.drawable.recorder);
			mLable.setText("手指上滑,取消发送");
		}
	}

	public void wantToCancel() {
		if (mDialog != null && mDialog.isShowing()) {
			mIcon.setVisibility(View.VISIBLE);
			mVoice.setVisibility(View.GONE);
			mLable.setVisibility(View.VISIBLE);
			mIcon.setImageResource(R.drawable.cancel);
			mLable.setText("松开手指,取消发送");
		}

	}

	public void tooShort() {
		if (mDialog != null && mDialog.isShowing()) {
			mIcon.setVisibility(View.VISIBLE);
			mVoice.setVisibility(View.GONE);
			mLable.setVisibility(View.VISIBLE);
			mIcon.setImageResource(R.drawable.voice_to_short);
			mLable.setText("录音时间过短");
		}

	}

	public void dimissDIalog() {
		if (mDialog != null && mDialog.isShowing()) {
			mDialog.dismiss();
			mDialog = null;
		}

	}

	/**
	 * 通过level更新voice上的图片
	 * 
	 * @param level
	 *            1-7
	 */

	public void updateVoiceLevel(int level) {
		if (mDialog != null && mDialog.isShowing()) {

			int resId = mContext.getResources().getIdentifier("v" + level, "drawable", mContext.getPackageName());
			mVoice.setImageResource(resId);
		}
	}

}
