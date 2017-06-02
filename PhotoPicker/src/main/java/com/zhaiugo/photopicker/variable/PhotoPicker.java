package com.zhaiugo.photopicker.variable;

/**
 * @author gaojianming
 * @time 2017/4/21 9:36
 */
public class PhotoPicker {

    public static final String KEY_SELECTED_PHOTOS = "photos";//选择图片后发送广播的KEY
    public static final String KEY_ALLOW_COUNT = "allow_count";//允许最多照片数
    public static final String KEY_SELECT_PHOTO_ACTION = "select_action";//选择图片的广播ACTION
    public static final String KEY_IS_MULTI_PHOTO = "is_multi_photo";//是否多选
    public static final String KEY_DELETE_PHOTO_ACTION = "delete_action";//预览删除的广播的ACTION
    public static final String KEY_OSS_PHOTOS = "oss_paths";//预览删除的OSS路径
    public static final String KEY_LOCAL_PHOTOS = "photo_paths";//预览删除的本地路径
    public static final String KEY_DELETE_PHOTO_POSITION = "position";//删除后通过广播发送删除图片的位置
}
