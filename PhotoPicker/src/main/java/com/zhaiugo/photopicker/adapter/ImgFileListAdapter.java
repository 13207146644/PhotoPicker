package com.zhaiugo.photopicker.adapter;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.zhaiugo.photopicker.variable.Variable;
import com.zhaiugo.photopicker.R;

public class ImgFileListAdapter extends BaseAdapter{
	
	private Context context;
	private String filecount="filecount";
	private String filename="filename";
	private String imgpath="imgpath";
	private List<HashMap<String, String>> listdata;
	
	public ImgFileListAdapter(Context context, List<HashMap<String, String>> listdata) {
		this.context=context;
		this.listdata=listdata;
		
	}
	
	@Override
	public int getCount() {
		return listdata.size();
	}

	@Override
	public Object getItem(int arg0) {
		return listdata.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@SuppressLint("InflateParams")
    @Override
	public View getView(final int arg0, View arg1, ViewGroup arg2) {
		Holder holder;
		if (arg1 == null) {
			holder=new Holder();
			arg1=LayoutInflater.from(context).inflate(R.layout.local_img_file_item, null);
			holder.photo_imgview=(ImageView) arg1.findViewById(R.id.filephoto_imgview);
			holder.filecount_textview=(TextView) arg1.findViewById(R.id.filecount_textview);
			holder.filename_textView=(TextView) arg1.findViewById(R.id.filename_textview);
			arg1.setTag(holder);
		}else{
			holder= (Holder)arg1.getTag();
		}
		
		holder.filename_textView.setText(listdata.get(arg0).get(filename));
		holder.filecount_textview.setText(listdata.get(arg0).get(filecount));

		Glide.with(context)
		.load(new File(listdata.get(arg0).get(imgpath)))
		.override(Variable.WIDTH/2, Variable.WIDTH/2)
		.centerCrop()
		.placeholder(R.drawable.img_default)
		.error(R.drawable.img_default)
		.diskCacheStrategy(DiskCacheStrategy.NONE)
		.into(holder.photo_imgview);
		
		return arg1;
	}
	
	class Holder{
		public ImageView photo_imgview;
		public TextView filecount_textview;
		public TextView filename_textView;
	}

	
}
