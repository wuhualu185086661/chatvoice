package com.wuguowei.chatvoice;

import java.util.ArrayList;
import java.util.List;

import com.wuguowei.chatvoice.view.RecordButton;
import com.wuguowei.chatvoice.view.RecordButton.AudioFinishRecorderListener;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends Activity {
	private ListView mListView;
	private ArrayAdapter<Recorder> mAdapter;
	private List<Recorder> mDatas = new ArrayList<Recorder>();

	private RecordButton mAudioRecorderButton;
	private View mAnimView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mListView = (ListView) findViewById(R.id.id_listview);
		mAudioRecorderButton = (RecordButton) findViewById(R.id.id_record_button);
		mAudioRecorderButton.setAudioFinishRecorderListener(new AudioFinishRecorderListener() {

			@Override
			public void onFinish(float seconds, String filePath) {
				Recorder recorder = new Recorder(seconds, filePath);
				mDatas.add(recorder);
				mAdapter.notifyDataSetChanged();
				mListView.setSelection(mDatas.size() - 1);

			}
		});
		mAdapter = new RecorderAdapter(this, mDatas);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// ����һ����Ƶ�ڲ���ʱ������ڶ�����Ƶ��ʱ��Ҫ�رյ�һ����Ƶ�Ķ���Ч����
				if (mAnimView != null) {
					mAnimView.setBackgroundResource(R.drawable.adj);
					mAnimView = null;
				}
				// ���Ŷ���
				mAnimView = view.findViewById(R.id.id_recorder_anim);
				mAnimView.setBackgroundResource(R.drawable.play_anim);
				AnimationDrawable anim = (AnimationDrawable) mAnimView.getBackground();
				anim.start();
				// ������Ƶ
				MediaManager.playSound(mDatas.get(position).filePath, new MediaPlayer.OnCompletionListener() {
					// �ص����������������Ƶ������ϵ�ʱ��ȡ�������Ĳ��š�
					@Override
					public void onCompletion(MediaPlayer mp) {
						mAnimView.setBackgroundResource(R.drawable.adj);
					}
				});

			}
		});
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MediaManager.pause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MediaManager.resume();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		MediaManager.release();
	}

	class Recorder {
		float time;
		String filePath;

		public Recorder(float time, String filePath) {
			super();
			this.time = time;
			this.filePath = filePath;
		}

		public float getTime() {
			return time;
		}

		public void setTime(float time) {
			this.time = time;
		}

		public String getFilePath() {
			return filePath;
		}

		public void setFilePath(String filePath) {
			this.filePath = filePath;
		}

	}
}
