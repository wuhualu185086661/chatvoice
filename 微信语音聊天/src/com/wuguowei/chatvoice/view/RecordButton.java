package com.wuguowei.chatvoice.view;

import com.wuguowei.chatvoice.R;
import com.wuguowei.chatvoice.view.AudioManager.AudioStateListener;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

public class RecordButton extends Button implements AudioStateListener {
	private static final int DISTANCE_Y_CANCEL = 50;
	private static final int STATE_NORMAL = 1;
	private static final int STATE_RECORDING = 2;
	private static final int STATE_WANT_TO_CANCEL = 3;
	private int mCurrentState = STATE_NORMAL;
	// �Ѿ���ʼ¼��
	private boolean isRecording = false;

	private DialogManager mDialogManager;

	private AudioManager mAudioManager;

	private float mTime;
	// �Ƿ񴥷�longclick
	private boolean mReady;

	public RecordButton(Context context) {
		this(context, null);
	}

	public RecordButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		mDialogManager = new DialogManager(getContext());
		String dir = Environment.getExternalStorageDirectory() + "/imooc_recorder_audios";
		mAudioManager = AudioManager.getInstance(dir);
		mAudioManager.setOnAudioStateListener(this);
		setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				System.out.println("onLongClick");
				mReady = true;
				mAudioManager.prepareAudio();
				return false;
			}
		});

	}

	/**
	 * ��ȡ������С
	 */

	private Runnable mGetVoiceLevelRunnable = new Runnable() {

		@Override
		public void run() {
			while (isRecording) {
				try {
					Thread.sleep(100);
					mTime += 0.1f;
					mHandler.sendEmptyMessage(MSG_VOICE_CHANGED);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	};

	/*
	 * ¼����ɺ�Ļص�
	 */
	public interface AudioFinishRecorderListener {
		void onFinish(float seconds, // ¼��ʱ��
				String filePath);

	}

	private AudioFinishRecorderListener mListener;

	public void setAudioFinishRecorderListener(AudioFinishRecorderListener listener) {
		mListener = listener;
	}

	private static final int MSG_AUDIO_PREPARED = 0X110;
	private static final int MSG_VOICE_CHANGED = 0X111;
	private static final int MSG_DIALOG_DISMISS = 0X112;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_AUDIO_PREPARED:
				// ��ʾ��audio end prepared�Ժ�
				mDialogManager.showRecordingDialog();
				isRecording = true;
				new Thread(mGetVoiceLevelRunnable).start();
				break;
			case MSG_VOICE_CHANGED:
				mDialogManager.updateVoiceLevel(mAudioManager.getVoiceLevel(7));

				break;
			case MSG_DIALOG_DISMISS:
				mDialogManager.dimissDIalog();

				break;
			}

		};
	};

	@Override
	public void wellPrepared() {
		System.out.println("�����ص��ɹ�");
		mHandler.sendEmptyMessage(MSG_AUDIO_PREPARED);

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		int x = (int) event.getX();
		int y = (int) event.getY();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			isRecording = true;
			changeState(STATE_RECORDING);
			break;
		case MotionEvent.ACTION_MOVE:

			if (isRecording) {
				if (wantToCancel(x, y)) {
					changeState(STATE_WANT_TO_CANCEL);
				} else {
					changeState(STATE_RECORDING);
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			if (!mReady) {
				reset();
				return super.onTouchEvent(event);
			}
			if (!isRecording || mTime < 0.6f) {
				// ��ʾ��ʾ���ȴ�ʱ��̫��
				mDialogManager.tooShort();
				mAudioManager.cancel();
				mHandler.sendEmptyMessageDelayed(MSG_DIALOG_DISMISS, 1300);// ��ʾ1.3��
			} else if (mCurrentState == STATE_RECORDING) {// ����¼�ƽ���

				mDialogManager.dimissDIalog();
				// release

				mAudioManager.release();
				if (mListener != null) {
					mListener.onFinish(mTime, mAudioManager.getCurrentFilePath());
				}

				// callbackToAct
			} else if (mCurrentState == STATE_WANT_TO_CANCEL) {
				// cancel
				mDialogManager.dimissDIalog();
				mAudioManager.cancel();
			}
			reset();
			break;
		}
		return super.onTouchEvent(event);
	}

	private boolean wantToCancel(int x, int y) {
		if (x < 0 || x > getWidth()) {
			return true;
		}
		if (y < -DISTANCE_Y_CANCEL || y > getHeight() + DISTANCE_Y_CANCEL) {
			return true;
		}
		return false;
	}

	/**
	 * �ָ���־λ
	 */
	private void reset() {
		isRecording = false;
		mReady = false;
		mTime = 0;
		changeState(STATE_NORMAL);
	}

	private void changeState(int state) {
		if (mCurrentState != state) {
			mCurrentState = state;
			switch (state) {
			case STATE_NORMAL:
				setBackgroundResource(R.drawable.btn_record_normal);
				setText(R.string.str_recorder_normal);
				break;
			case STATE_RECORDING:
				setBackgroundResource(R.drawable.btn_recording);
				setText(R.string.str_recorder_recording);
				if (isRecording) {
					mDialogManager.recording();
				}
				break;
			case STATE_WANT_TO_CANCEL:
				setBackgroundResource(R.drawable.btn_recording);
				setText(R.string.str_recorder_want_cancel);
				mDialogManager.wantToCancel();
				break;

			default:
				break;
			}
		}
	}

}
