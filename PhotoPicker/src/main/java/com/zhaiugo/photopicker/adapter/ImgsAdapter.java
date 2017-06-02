package com.zhaiugo.photopicker.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.zhaiugo.photopicker.ImgsActivity;
import com.zhaiugo.photopicker.PreviewImageActivity;
import com.zhaiugo.photopicker.R;
import com.zhaiugo.photopicker.variable.PhotoPicker;
import com.zhaiugo.photopicker.variable.Variable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImgsAdapter extends BaseAdapter {
	
	private Context context;
	private List<String> data;
	
	private RelativeLayout.LayoutParams lp;
	
	private ArrayList<String> select_paths = new ArrayList<String>();
	
	private boolean isSingle;
	
	private int maxCount = 1;
	
	private String action;
	
	private boolean is_multi_photo;
	
	private OnSelectImageListener onSelectImageListener;
	
	public ImgsAdapter(Context context, List<String> data, int maxCount, String action, boolean is_multi_photo) {
		this.context = context;
		this.data = data;
		this.maxCount = maxCount;
		this.action = action;
		this.is_multi_photo = is_multi_photo;
		
		lp = new RelativeLayout.LayoutParams(Variable.WIDTH/3, Variable.WIDTH/3);
		
		if(!is_multi_photo){
			this.isSingle = true;
		}
	}

	public void notifyDataSetChanged(ArrayList<String> select_paths){
		this.select_paths = select_paths;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int arg0) {
		return data.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}
	
	@Override
	public View getView(final int arg0, View arg1, ViewGroup arg2) {
		Holder holder;
		if (arg1 == null) {
			arg1=LayoutInflater.from(context).inflate(R.layout.imgs_item, arg2, false);
			holder=new Holder();
			holder.image_layout = (RelativeLayout)arg1.findViewById(R.id.image_layout);
			holder.imageView = (ImageView) arg1.findViewById(R.id.imageView1);
			holder.checkBox = (CheckBox) arg1.findViewById(R.id.checkBox1);
			holder.clickView = arg1.findViewById(R.id.click_view);
			holder.coverView = arg1.findViewById(R.id.cover_view);
			holder.image_layout.setLayoutParams(lp);
			arg1.setTag(holder);
		}else {
			holder= (Holder)arg1.getTag();
		}
		
		if(isSingle){
			if(holder.checkBox.getVisibility() == View.VISIBLE){
				holder.checkBox.setVisibility(View.GONE);
			}
		}else{
			if(select_paths.contains(data.get(arg0))){
				if(holder.coverView.getVisibility() == View.GONE){
					holder.coverView.setVisibility(View.VISIBLE);
				}
				holder.checkBox.setChecked(true);
			}else{
				if(holder.coverView.getVisibility() == View.VISIBLE){
					holder.coverView.setVisibility(View.GONE);
				}
				holder.checkBox.setChecked(false);
			}
		}

		Glide.with(context)
		.load(new File(data.get(arg0)))
		.override(Variable.WIDTH/2, Variable.WIDTH/2)
		.centerCrop()
		.diskCacheStrategy(DiskCacheStrategy.NONE)
		.placeholder(R.drawable.img_default)
		.error(R.drawable.img_default)
		.into(holder.imageView);
		
		final CheckBox checkBox = holder.checkBox;
		final View coverView = holder.coverView;
		holder.clickView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(checkBox.isChecked()){
					checkBox.setChecked(false);
					coverView.setVisibility(View.GONE);
					
					if(select_paths.contains(data.get(arg0))){
						select_paths.remove(data.get(arg0));
					}
					if(onSelectImageListener != null){
						onSelectImageListener.onRemoveImage(arg0);
					}
				}else{
					if(select_paths.size() == maxCount){
						if(onSelectImageListener != null){
							onSelectImageListener.onErrorMessage(context.getString(R.string.most_photo_upload));
						}
						return ;
					}
					checkBox.setChecked(true);
					coverView.setVisibility(View.VISIBLE);
					
					if(!select_paths.contains(data.get(arg0))){
						select_paths.add(data.get(arg0));
					}
					if(onSelectImageListener != null){
						onSelectImageListener.onAddImage(arg0);
					}
				}
			}
		});
		
		arg1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(isSingle){
					if(context instanceof ImgsActivity){
						ArrayList<String> photos = new ArrayList<String>();
						photos.add(data.get(arg0));
						((ImgsActivity)context).sendBroadCast(photos);
					}
				}else{
					Intent intent = new Intent(context, PreviewImageActivity.class);
					intent.putStringArrayListExtra("photo_paths", (ArrayList<String>)data);
					intent.putStringArrayListExtra("select_paths", ((ImgsActivity)context).getSelectPhotos());
					intent.putExtra("cur_pos", arg0);
					intent.putExtra(PhotoPicker.KEY_ALLOW_COUNT, maxCount);
					intent.putExtra(PhotoPicker.KEY_SELECT_PHOTO_ACTION, action);
					intent.putExtra(PhotoPicker.KEY_IS_MULTI_PHOTO, is_multi_photo);
					context.startActivity(intent);
				}
			}
		});
		
		return arg1;
	}
	
	class Holder{
		RelativeLayout image_layout;
		ImageView imageView;
		CheckBox checkBox;
		View clickView;
		View coverView;
	}

	public void setOnSelectImageListener(OnSelectImageListener onSelectImageListener) {
		this.onSelectImageListener = onSelectImageListener;
	}

	public interface OnSelectImageListener{
		void onAddImage(int position);
		void onRemoveImage(int position);
		void onErrorMessage(String message);
	}
	
}
