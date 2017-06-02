package com.zhaiugo.photopicker;

import java.io.File;
import java.util.ArrayList;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.zhaiugo.photopicker.variable.PhotoPicker;
import com.zhaiugo.photopicker.variable.Variable;
import com.zhaiugo.photoview.PhotoView;
import com.zhaiugo.photoview.PhotoViewAttacher;
import com.zhaiugo.photopicker.util.ActivityStack;

public class PreviewImageActivity extends BaseActivity {
	private LayoutInflater mInflater;

	private ViewPager vViewPager;
	private View vBottomView;
	private View vChooseView;
	private CheckBox vCheckBox;
	
	private ArrayList<String> mPhotoPaths;
	private ArrayList<String> mSelectPaths;
	private int mCurPos;
	private String action;
	private int allow_count;
	private boolean is_multi_photo;
	
	private ObjectAnimator mObjectAnimatorIn;
	private ObjectAnimator mObjectAnimatorOut;
	private ObjectAnimator mObjectAnimatorIn2;
	private ObjectAnimator mObjectAnimatorOut2;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		overridePendingTransition(R.anim.activity_scale_fade_in, R.anim.slide_null);
		setContentView(R.layout.activity_preview_image);
		mInflater = getLayoutInflater();
		
		action = getIntent().getStringExtra(PhotoPicker.KEY_SELECT_PHOTO_ACTION);
		allow_count = getIntent().getIntExtra(PhotoPicker.KEY_ALLOW_COUNT, 1);
		mPhotoPaths = getIntent().getStringArrayListExtra("photo_paths");
		mSelectPaths = getIntent().getStringArrayListExtra("select_paths");
		is_multi_photo = getIntent().getBooleanExtra(PhotoPicker.KEY_IS_MULTI_PHOTO, false);

		if(mSelectPaths == null){
			mSelectPaths = new ArrayList<>();
		}
		mCurPos = getIntent().getIntExtra("cur_pos", 0);
		initToolBar((mCurPos+1)+"/"+mPhotoPaths.size(), getString(R.string.ok_label), R.color.main_color);
		initView();
		setListener();
		
	}

	@Override
	protected void initView() {
		vViewPager = (ViewPager)findViewById(R.id.vp);
		vBottomView = findViewById(R.id.bottom_layout);
		vChooseView = findViewById(R.id.choose_layout);
		vCheckBox = (CheckBox)findViewById(R.id.checkBox);
		
		vViewPager.setAdapter(new BigImagesPaperAdapter(mPhotoPaths));
		vViewPager.setCurrentItem(mCurPos);
		
		if(mSelectPaths.contains(mPhotoPaths.get(mCurPos))){
			vCheckBox.setChecked(true);
		}else{
			vCheckBox.setChecked(false);
		}
		
		if(mSelectPaths.size() > 0){
			vMenuText.setText(getString(R.string.ok_label)+"("+mSelectPaths.size()+"/"+allow_count+")");
		}else{
			vMenuText.setText(getString(R.string.ok_label));
		}
	}

	@Override
	protected void setListener() {
		vViewPager.addOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {
				vToolbar.setTitle((arg0+1)+"/"+mPhotoPaths.size());
				if(mSelectPaths.contains(mPhotoPaths.get(arg0))){
					vCheckBox.setChecked(true);
				}else{
					vCheckBox.setChecked(false);
				}
				
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
		
		vChooseView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(vCheckBox.isChecked()){
					vCheckBox.setChecked(false);
					mSelectPaths.remove(mPhotoPaths.get(vViewPager.getCurrentItem()));
				}else{
					if(mSelectPaths.size() < allow_count){
						vCheckBox.setChecked(true);
						mSelectPaths.add(mPhotoPaths.get(vViewPager.getCurrentItem()));
					}else{
						showToast(R.string.most_photo_upload);
						return ;
					}
				}
				
				if(mSelectPaths.size() > 0){
					vMenuText.setText(getString(R.string.ok_label)+"("+mSelectPaths.size()+"/"+allow_count+")");
				}else{
					vMenuText.setText(getString(R.string.ok_label));
				}
				if(is_multi_photo){
					sendBroadCast(mSelectPaths, ImgsActivity.SYNC_SELECT_IMAGE_ACTION);
				}
				
			}
		});
		
		vMenuText.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mSelectPaths.size() > 0){
					sendBroadCast(mSelectPaths, action);
					finish();
					ActivityStack.clearActivity();
				}else{
					showToast(R.string.select_photo_tip);
				}
			}
		});
	}
	
	private class BigImagesPaperAdapter extends PagerAdapter {
		private ArrayList<String> imgs;
		
		public BigImagesPaperAdapter(ArrayList<String> imgs){
			this.imgs = imgs;
		}
		
		@Override
		public int getCount() {
			return imgs == null? 0 : imgs.size();
		}

		@Override
		public Object instantiateItem(View collection, final int pos) {
			View view = mInflater.inflate(R.layout.big_image_item, null);
			PhotoView imageView = (PhotoView)view.findViewById(R.id.image);
			final ProgressBar progressBar = (ProgressBar)view.findViewById(R.id.progressBar);
			
			Glide.with(mContext)
			.load(new File(imgs.get(pos)))
			.override(Variable.WIDTH, Variable.HEIGHT)
			//.centerCrop()
			.diskCacheStrategy(DiskCacheStrategy.NONE)
			.error(R.drawable.failure_image)
			.placeholder(R.drawable.transparence)
			.listener(new RequestListener<File, GlideDrawable>() {
					@Override
					public boolean onException(Exception arg0, File arg1,
							Target<GlideDrawable> arg2, boolean arg3) {
						progressBar.setVisibility(View.GONE);
						return false;
					}

					@Override
					public boolean onResourceReady(GlideDrawable arg0,
							File arg1, Target<GlideDrawable> arg2,
							boolean arg3, boolean arg4) {
						progressBar.setVisibility(View.GONE);
						return false;
					}
				})
			.into(imageView);
			
			imageView.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
				@Override
				public void onViewTap(View view, float x, float y) {
					if(vToolbar.getTranslationY() != 0){
						
						if(mObjectAnimatorIn == null){
							mObjectAnimatorIn  = ObjectAnimator.ofFloat(vToolbar, "translationY", vToolbar.getTranslationY(), 0); ;  
							mObjectAnimatorIn.setDuration(200);  
							mObjectAnimatorIn.setInterpolator(AnimationUtils.loadInterpolator(mContext, android.R.interpolator.linear));
							
							mObjectAnimatorIn2  = ObjectAnimator.ofFloat(vBottomView, "translationY", vBottomView.getTranslationY(), vBottomView.getTranslationY() - vBottomView.getHeight()); ;  
							mObjectAnimatorIn2.setDuration(200);  
							mObjectAnimatorIn2.setInterpolator(AnimationUtils.loadInterpolator(mContext, android.R.interpolator.linear));
						}
						mObjectAnimatorIn.start();  
						mObjectAnimatorIn2.start();
					}else{
						
						if(mObjectAnimatorOut == null){
							mObjectAnimatorOut  = ObjectAnimator.ofFloat(vToolbar, "translationY", vToolbar.getTranslationY(), -vToolbar.getHeight()); ;  
							mObjectAnimatorOut.setDuration(200);  
							mObjectAnimatorOut.setInterpolator(AnimationUtils.loadInterpolator(mContext, android.R.interpolator.linear));
							
							mObjectAnimatorOut2  = ObjectAnimator.ofFloat(vBottomView, "translationY", vBottomView.getTranslationY(), vBottomView.getTranslationY() + vBottomView.getHeight()); ;  
							mObjectAnimatorOut2.setDuration(200);  
							mObjectAnimatorOut2.setInterpolator(AnimationUtils.loadInterpolator(mContext, android.R.interpolator.linear));
						} 
						mObjectAnimatorOut.start();  
						mObjectAnimatorOut2.start();
					}
				}
			});
			
			((ViewPager) collection).addView(view, 0);
			return view;
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView((View) arg2);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

	}
	
	private void sendBroadCast(ArrayList<String> filelist, String action){
		LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(mContext);
		Intent intent = new Intent(action);
		intent.putStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS, filelist);
		broadcastManager.sendBroadcast(intent);
	}
	
	public void goBack() {
		finish();
	}
	
	@Override
	public void onBackPressed() {
		finish();
	}
	
	@Override
	public void finish() {
		super.finish();
		Glide.get(mContext).clearMemory();
		overridePendingTransition(R.anim.slide_null, R.anim.activity_scale_fade_out);
	}

}
