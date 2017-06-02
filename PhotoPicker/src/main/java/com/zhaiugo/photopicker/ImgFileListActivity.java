package com.zhaiugo.photopicker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.zhaiugo.photopicker.adapter.ImgFileListAdapter;
import com.zhaiugo.photopicker.model.FileTraversal;
import com.zhaiugo.photopicker.util.ActivityStack;
import com.zhaiugo.photopicker.util.GetImagePath;
import com.zhaiugo.photopicker.variable.PhotoPicker;

/**
* @author Administrator
* @time 2017/4/21 8:43
* @des
 * Intent intent = new Intent(mContext, ImgFileListActivity.class);
 *intent.putExtra(PhotoPicker.KEY_SELECT_PHOTO_ACTION, "接受图片广播的Action");
 *intent.putExtra(PhotoPicker.KEY_ALLOW_COUNT, “允许的最大张数”);
 *intent.putExtra(PhotoPicker.KEY_IS_MULTI_PHOTO, false);//是否是多选
 *mContext.startActivity(intent);
*/
public class ImgFileListActivity extends BaseActivity implements OnItemClickListener {
	private static final int FAILURE = 0;
	private static final int SUCCESS = 1;

	private View vRequestView;
	private View vNoDataView;
	private ListView listView;
	private ImgFileListAdapter listAdapter;
	private List<FileTraversal> locallist;
	private int allow_count;
	private String action;
	private boolean is_multi_photo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_local_file_list);
		
		initToolBar(R.string.select_file_title, 0, R.color.main_color);
		initView();
		setListener();
		new LoadLocalAsyncTask().execute();

		ActivityStack.addActivityToList(this);
	}
	
	protected void initView(){
		allow_count = getIntent().getIntExtra(PhotoPicker.KEY_ALLOW_COUNT, 1);
		action = getIntent().getStringExtra(PhotoPicker.KEY_SELECT_PHOTO_ACTION);
		is_multi_photo = getIntent().getBooleanExtra(PhotoPicker.KEY_IS_MULTI_PHOTO, false);

		vRequestView = findViewById(R.id.request_layout);
		vNoDataView = findViewById(R.id.no_data_layout);
		listView = (ListView) findViewById(R.id.listView1);

	}
	
	//此处也可用ParseDataHandler处理异步任务，读取本地数据图片是一个相对耗时的操作
	private class LoadLocalAsyncTask extends AsyncTask<Void, Void, Integer>{
		@Override
		protected Integer doInBackground(Void... arg0) {
			try {
				GetImagePath util = new GetImagePath(mContext);
				locallist = util.LocalImgFileList();
				return SUCCESS;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return FAILURE;
		}

		@Override
		protected void onPostExecute(Integer result) {
			if(result == SUCCESS){
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						if (locallist != null && locallist.size() != 0) {
							List<HashMap<String, String>> listdata = new ArrayList<>();
							for (int i = 0; i < locallist.size(); i++) {
								HashMap<String, String> map = new HashMap<>();
								map.put("filecount", locallist.get(i).filecontent.size() + "张");
								map.put("imgpath", locallist.get(i).filecontent.get(0) == null ? null : (locallist.get(i).filecontent.get(0)));
								map.put("filename", locallist.get(i).filename);
								listdata.add(map);
							}
							listAdapter = new ImgFileListAdapter(mContext, listdata);
							listView.setAdapter(listAdapter);
							listView.setOnItemClickListener(ImgFileListActivity.this);
							listView.setVisibility(View.VISIBLE);
							vRequestView.setVisibility(View.GONE);
							vNoDataView.setVisibility(View.GONE);
						}else{
							listView.setVisibility(View.GONE);
							vRequestView.setVisibility(View.GONE);
							vNoDataView.setVisibility(View.VISIBLE);
						}
						
					}
				});
			}else{
				listView.setVisibility(View.GONE);
				vRequestView.setVisibility(View.GONE);
				vNoDataView.setVisibility(View.VISIBLE);
			}
		}
		
	}
	
	protected void setListener(){
		
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Intent intent = new Intent(this, ImgsActivity.class);
		intent.putExtra("data", locallist.get(arg2));
		intent.putExtra(PhotoPicker.KEY_ALLOW_COUNT, allow_count);
		intent.putExtra(PhotoPicker.KEY_SELECT_PHOTO_ACTION, action);
		intent.putExtra(PhotoPicker.KEY_IS_MULTI_PHOTO, is_multi_photo);
		startActivity(intent);
	}

	@Override
	protected void onStop() {
		super.onStop();
	}
	

}
