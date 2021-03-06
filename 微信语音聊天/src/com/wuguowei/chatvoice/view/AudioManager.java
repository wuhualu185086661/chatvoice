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
	 * 回调准备完毕
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
		isPrepare = false;
		
			
			File dir = new File(mDir);
			if (!dir.exists())
				dir.mkdirs();

			String fileName = generateFielName();
			File file = new File(dir, fileName);
			mCurrentFilePath = file.getAbsolutePath();
			mMediaRecorder = new MediaRecorder();
			// 设置输出文件
			mMediaRecorder.setOutputFile(file.getAbsolutePath());

			// 设置音频源为麦克风
			mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			// 设置音频的格式
			mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
			// 设置音频的编码为amr
			mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			try {
				mMediaRecorder.prepare();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mMediaRecorder.start();
			// 准备结束
			isPrepare = true;
			if (mListener != null) {
				mListener.wellPrepared();
			}
	
	}

	/**
	 * 随机生成文件的名称
	 * 
	 * @return
	 */
	private String generateFielName() {
		return UUID.randomUUID().toString() + ".amr";
	}

	public int getVoiceLevel(int maxLevel) {
		if (isPrepare) {
			// getMaxAmplitude在1-32767之间
			try {
				return maxLevel * mMediaRecorder.getMaxAmplitude() / 32768 + 1;
			} catch (Exception e) {
			}
		}
		return 1;
	}

	public void release() {
		mMediaRecorder.stop();
		if(mMediaRecorder!=null){
		mMediaRecorder.release();
		mMediaRecorder = null;}
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
