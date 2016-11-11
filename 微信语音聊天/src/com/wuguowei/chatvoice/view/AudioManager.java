package com.wuguowei.chatvoice.view;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import android.media.MediaRecorder;

public class AudioManager {
	private MediaRecorder mMediaRecorder;
	private String mDir;
	private String mCurrentFilePath;
	private static AudioManager mInstance;
	private boolean isPrepare;

	private AudioManager(String dir) {
		mDir = dir;
	}

	/**
	 * �ص�׼�����
	 * 
	 * @author steven
	 *
	 */
	public interface AudioStateListener {
		void wellPrepared();
	}

	public AudioStateListener mListener;

	public void setOnAudioStateListener(AudioStateListener Listener) {
		mListener = Listener;
	}

	public static AudioManager getInstance(String dir) {
		if (mInstance == null) {
			synchronized (AudioManager.class) {
				if (mInstance == null)
					mInstance = new AudioManager(dir);

			}
		}
		return mInstance;

	}

	public void prepareAudio() {
		try {
			isPrepare = false;
			File dir = new File(mDir);
			if (!dir.exists()) {
				dir.mkdir();
				String fileName = generateFielName();
				File file = new File(dir, fileName);
				mCurrentFilePath = file.getAbsolutePath();
				mMediaRecorder = new MediaRecorder();
				// ��������ļ�
				mMediaRecorder.setOutputFile(file.getAbsolutePath());
				// ������ƵԴΪ��˷�
				mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
				// ������Ƶ�ĸ�ʽ
				mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
				// ������Ƶ�ı���Ϊamr
				mMediaRecorder.setAudioSource(MediaRecorder.AudioEncoder.AMR_NB);

				mMediaRecorder.prepare();
				mMediaRecorder.start();
				// ׼������
				isPrepare = true;
				if (mListener != null) {
					mListener.wellPrepared();
				}
			}
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * ��������ļ�������
	 * 
	 * @return
	 */
	private String generateFielName() {
		return UUID.randomUUID().toString() + ".amr";
	}

	public int getVoiceLevel(int maxLevel) {
		if (isPrepare) {
			// getMaxAmplitude��1-32767֮��
			try {
				return maxLevel * mMediaRecorder.getMaxAmplitude() / 32768 + 1;
			} catch (Exception e) {
			}
		}
		return 1;
	}

	public void release() {
		mMediaRecorder.stop();
		mMediaRecorder.release();
		mMediaRecorder = null;
	}

	public void cancel() {
		release();
		if (mCurrentFilePath != null) {
			File file = new File(mCurrentFilePath);
			file.delete();
			mCurrentFilePath = null;
		}

	}

	public String getCurrentFilePath() {
		return mCurrentFilePath;
	}
}
