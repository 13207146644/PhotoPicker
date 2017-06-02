package com.zhaiugo.photopicker.util;

import com.zhaiugo.photopicker.BaseActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gaojianming
 * @time 2017/4/20 15:42
 */
public class ActivityStack {
    private static List<BaseActivity> list=new ArrayList<>();

    /**
    * @author gaojianming
    * @time 2017/4/20 15:43
    * @des 新增栈
    */
    public static void addActivityToList(BaseActivity activity){
        list.add(activity);
    }

    /**
    * @author gaojianming
    * @time 2017/4/20 15:43
    * @des 清除栈
    */
    public static void clearActivity(){
        for(BaseActivity activity : list){
            if(activity!=null){
                activity.finish();
            }
        }
    }
}
