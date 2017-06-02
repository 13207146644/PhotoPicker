package com.zhaiugo.photopicker;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zhaiugo.photopicker.adapter.ImgsAdapter;
import com.zhaiugo.photopicker.model.FileTraversal;
import com.zhaiugo.photopicker.util.ActivityStack;
import com.zhaiugo.photopicker.variable.PhotoPicker;

public class ImgsActivity extends BaseActivity implements ImgsAdapter.OnSelectImageListener{
	public static final String SYNC_SELECT_IMAGE_ACTION = "sunc.select.image.action";

	private LocalBroadcastManager localBroadcastManager;
	private BroadcastReceiver broadcastReceiver;
	
	private FileTraversal fileTraversal;
	private GridView imgGridView;
	private ImgsAdapter imgsAdapter;
	private View vPreviewView;
	
	private ArrayList<String> filelist;
	private int allow_count;
	private String action;
	private boolean is_multi_photo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_local_img_grid);
		
		Intent intent = getIntent();
		fileTraversal = intent.getParcelableExtra("data");
		allow_count = intent.getIntExtra(PhotoPicker.KEY_ALLOW_COUNT, 1);
		action = intent.getStringExtra(PhotoPicker.KEY_SELECT_PHOTO_ACTION);
		is_multi_photo = intent.getBooleanExtra(PhotoPicker.KEY_IS_MULTI_PHOTO, false);
		
		initToolBar(R.string.select_photo_title, R.string.ok_label, R.color.main_color);
		initView();
		setListener();
		initBroadCast();

		ActivityStack.addActivityToList(this);
	}

	protected void initView(){
		imgGridView = (GridView) this.findViewById(R.id.grid_view);
		vPreviewView = this.findViewById(R.id.preview);

		imgsAdapter = new ImgsAdapter(this, fileTraversal.filecontent, allow_count, action, is_multi_photo);
		imgsAdapter.setOnSelectImageListener(this);
		imgGridView.setAdapter(imgsAdapter);
		filelist = new ArrayList<>();
		
		if(!is_multi_photo){
			vMenuText.setVisibility(View.GONE);
		}
	}
	
	@Override
	protected void setListener() {
		vMenuText.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(filelist.size()>0){
					sendBroadCast(filelist);
				}else{
					showToast(getString(R.string.select_photo_tip));
				}
			}
		});
		
		vPreviewView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, PreviewImageActivity.class);
				intent.putStringArrayListExtra("photo_paths", filelist);
				intent.putStringArrayListExtra("select_paths", filelist);
				intent.putExtra(PhotoPicker.KEY_ALLOW_COUNT, allow_count);
				intent.putExtra(PhotoPicker.KEY_SELECT_PHOTO_ACTION, action);
				intent.putExtra(PhotoPicker.KEY_IS_MULTI_PHOTO, is_multi_photo);
				startActivity(intent);
			}
		});
	}

	public void sendBroadCast(ArrayList<String> filelist){
		LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(mContext);
		Intent intent = new Intent(action);
		intent.putStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS, filelist);
		broadcastManager.sendBroadcast(intent);
		//销毁本地图片相关Activity
		finish();
		ActivityStack.clearActivity();
	}
	
	@Override
	public void onAddImage(int position) {
		operatorImageFiles(position, false);
	}

	@Override
	public void onRemoveImage(int position) {
		operatorImageFiles(position, true);
	}

	@Override
	public void onErrorMessage(String message) {
		showToast(message);
	}

	private void operatorImageFiles(int position, boolean isDelete){
		String filapath = fileTraversal.filecontent.get(position);
		if(isDelete){
			if(filelist.contains(filapath)){
				filelist.remove(filapath);
			}
		}else{
			if(!filelist.contains(filapath)){
				filelist.add(filapath);
				
			}
		}
		
		//如果低于一张图片则不能预览
		if(filelist.size() == 0){
			((TextView)vPreviewView).setText(getString(R.string.preview));
			vMenuText.setText(getString(R.string.ok_label));
			vPreviewView.setEnabled(false);
		}else{
			vMenuText.setText(getString(R.string.ok_label)+ "(" + filelist.size() + "/" + allow_count + ")");
			((TextView)vPreviewView).setText(getString(R.string.preview)+"("+String.valueOf(filelist.size())+")");
			vPreviewView.setEnabled(true);
		}
	}
	
	private void initBroadCast(){
		localBroadcastManager = LocalBroadcastManager.getInstance(mContext);
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(SYNC_SELECT_IMAGE_ACTION);
		broadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				filelist = intent.getStringArrayListExtra("photos");
				imgsAdapter.notifyDataSetChanged(filelist);
				if(filelist.size() == 0){
					((TextView)vPreviewView).setText(getString(R.string.preview));
					vMenuText.setText(getString(R.string.ok_label));
					vPreviewView.setEnabled(false);
				}else{
					((TextView)vPreviewView).setText(getString(R.string.preview)+"("+String.valueOf(filelist.size())+")");
					vMenuText.setText(getString(R.string.ok_label)+ "(" + filelist.size() + "/" + allow_count + ")");
					vPreviewView.setEnabled(true);
				}
				
			}
		};
		localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);
		
	}
	
	public ArrayList<String> getSelectPhotos(){
		return filelist;
	}
	
	@Override
	protected void onStop() {
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Glide.get(mContext).clearMemory();
		if(localBroadcastManager != null && broadcastReceiver != null){
			localBroadcastManager.unregisterReceiver(broadcastReceiver);
		}
	}
	
}
