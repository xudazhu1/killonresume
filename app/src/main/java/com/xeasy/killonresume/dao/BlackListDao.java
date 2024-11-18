package com.xeasy.killonresume.dao;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class BlackListDao {
    private static Gson gson = new Gson();
    private static Type type = new TypeToken<List<String>>(){}.getType();

    public static boolean save(Context context, String list) {
        users = list;
        //  保存
        SharedPreferences sharedPreferences = context.getSharedPreferences("BLACK_LIST_EASY", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString("BLACK_LIST_EASY", list);
        userNameList = gson.fromJson(users, type);
        return edit.commit();
    }

    /**
     * 测试用的
     */
    public static boolean saveString(Context context, String fileName, String str) {
        // 交换俩对象的order值 然后保存
        SharedPreferences sharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();

        edit.putString(fileName, str);
        return edit.commit();
    }


    public static boolean add(Context context, String userName) {
        // 交换俩对象的order值 然后保存
        getUserNameList(context).add(userName);
        return save(context, gson.toJson(userNameList));
    }


    public static boolean remove(Context context, String userName) {
        // 交换俩对象的order值 然后保存
        getUserNameList(context).removeIf(o -> null == o || o.equals(userName));
        return save(context, gson.toJson(userNameList));
    }

    private static String users = null;
    private static List<String> userNameList = null;

    public static List<String> getUserNameList(Context context) {
        if ( null == userNameList ) {
            String read = read(context);
            userNameList = gson.fromJson(read, type);
        }
        return userNameList;
    }

    public static void initUserName(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("BLACK_LIST_EASY", Context.MODE_PRIVATE);
        users = sharedPreferences.getString("BLACK_LIST_EASY", "[\n\"被屏蔽用户1\",\n\"被屏蔽用户12\",\n]");
    }

    public static String read(Context context) {
        if ( null == users ) {
            initUserName(context);
        }
        // 交换俩对象的order值 然后保存
        return users;
    }

}
