package com.zhaiugo.photopicker;

import java.io.File;
import java.util.ArrayList;

import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
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

public class PreviewDeleteImageActivity extends BaseActivity {
	private LayoutInflater mInflater;
	
	private ViewPager vViewPager;
	private View vDeleteView;
	
	private ObjectAnimator mObjectAnimatorIn;
	private ObjectAnimator mObjectAnimatorOut;
	
	private AlertDialog alertDialog;
	
	private ArrayList<String> mAllPaths = new ArrayList<String>();
	private ArrayList<String> mOSSPaths;
	private ArrayList<String> mPhotoPaths;
	private int mCurPos;
	private String mDeleteAction;
	
	private BigImagesPaperAdapter adapter;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		overridePendingTransition(R.anim.activity_scale_fade_in, R.anim.slide_null);
		setContentView(R.layout.activity_preview_delete_image);
		mInflater = getLayoutInflater();
		
		mDeleteAction = getIntent().getStringExtra(PhotoPicker.KEY_DELETE_PHOTO_ACTION);
		mOSSPaths = getIntent().getStringArrayListExtra(PhotoPicker.KEY_OSS_PHOTOS);
		mPhotoPaths = getIntent().getStringArrayListExtra(PhotoPicker.KEY_LOCAL_PHOTOS);
		if(mOSSPaths != null && mOSSPaths.size() > 0){
			mAllPaths.addAll(mOSSPaths);
		}
		if(mPhotoPaths != null && mPhotoPaths.size() > 0){
			mAllPaths.addAll(mPhotoPaths);
		}
		
		mCurPos = getIntent().getIntExtra("cur_pos", 0);
		initToolBar((mCurPos+1)+"/"+mAllPaths.size(), "", R.color.main_color);
		initView();
		setListener();
		
	}

	@Override
	protected void initView() {
		vViewPager = (ViewPager)findViewById(R.id.vp);
		vDeleteView = findViewById(R.id.delete);
		
		adapter = new BigImagesPaperAdapter(mAllPaths);
		vViewPager.setAdapter(adapter);
		vViewPager.setCurrentItem(mCurPos);
		
	}

	@Override
	protected void setListener() {
		vViewPager.addOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {
				vToolbar.setTitle((arg0+1)+"/"+mAllPaths.size());
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
		
		vDeleteView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(alertDialog != null){
					alertDialog.dismiss();
				}
				AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		        builder.setTitle(R.string.warm_tip);
		        builder.setMessage(R.string.confirm_delete_this_photo);
		        builder.setPositiveButton(R.string.ok_label, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						alertDialog.dismiss();
						sendBroadCast();
					}
				});
		        builder.setNegativeButton(R.string.cancel_label, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						alertDialog.dismiss();
					}
				});
		        alertDialog = builder.show();
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
		
		public void notifyDataSetChanged(ArrayList<String> imgs){
			this.imgs = imgs;
			notifyDataSetChanged();
		}

		@Override
		public Object instantiateItem(View collection, final int pos) {
			View view = mInflater.inflate(R.layout.big_image_item, null);
			PhotoView imageView = (PhotoView)view.findViewById(R.id.image);
			final ProgressBar progressBar = (ProgressBar)view.findViewById(R.id.progressBar);
			
			String path = imgs.get(pos);
			if(mOSSPaths != null && mOSSPaths.contains(path)){
				Glide.with(mContext)
				.load(path)
				.override(Variable.WIDTH, Variable.HEIGHT)
				.diskCacheStrategy(DiskCacheStrategy.NONE)
				.error(R.drawable.failure_image)
				.placeholder(R.drawable.transparence)
				.listener(new RequestListener<String, GlideDrawable>() {
					@Override
					public boolean onException(Exception arg0, String arg1,
							Target<GlideDrawable> arg2, boolean arg3) {
						progressBar.setVisibility(View.GONE);
						return false;
					}

					@Override
					public boolean onResourceReady(GlideDrawable arg0,
							String arg1, Target<GlideDrawable> arg2,
							boolean arg3, boolean arg4) {
						progressBar.setVisibility(View.GONE);
						return false;
					}
				})
				.into(imageView);
			}else{
				Glide.with(mContext)
				.load(new File(path))
				.override(Variable.WIDTH, Variable.HEIGHT)
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
			}
			
			imageView.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
				@Override
				public void onViewTap(View view, float x, float y) {
					if(vToolbar.getTranslationY() != 0){
						if(mObjectAnimatorIn == null){
							mObjectAnimatorIn  = ObjectAnimator.ofFloat(vToolbar, "translationY", vToolbar.getTranslationY(), 0);
							mObjectAnimatorIn.setDuration(200);  
							mObjectAnimatorIn.setInterpolator(AnimationUtils.loadInterpolator(mContext, android.R.interpolator.linear));
						}
						mObjectAnimatorIn.start();  
					}else{
						
						if(mObjectAnimatorOut == null){
							mObjectAnimatorOut  = ObjectAnimator.ofFloat(vToolbar, "translationY", vToolbar.getTranslationY(), -vToolbar.getHeight()); ;  
							mObjectAnimatorOut.setDuration(200);  
							mObjectAnimatorOut.setInterpolator(AnimationUtils.loadInterpolator(mContext, android.R.interpolator.linear));
						} 
						mObjectAnimatorOut.start();  
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

		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}
		
	}
	
	private void sendBroadCast(){
		try {
			int position = vViewPager.getCurrentItem();
			mAllPaths.remove(position);
			adapter.notifyDataSetChanged(mAllPaths);
			vToolbar.setTitle((vViewPager.getCurrentItem()+1)+"/"+mAllPaths.size());
			
			LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(mContext);
			Intent intent = new Intent(mDeleteAction);
			intent.putExtra(PhotoPicker.KEY_DELETE_PHOTO_POSITION, position);
			broadcastManager.sendBroadcast(intent);
			
			if(mAllPaths.size() == 0){
				finish();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
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
