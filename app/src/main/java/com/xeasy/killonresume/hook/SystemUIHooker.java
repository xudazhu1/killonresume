package com.xeasy.killonresume.hook;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.AndroidAppHelper;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.xeasy.killonresume.dao.BlackListDao;
import com.xeasy.killonresume.utils.ReflexUtil;
import com.xeasy.killonresume.utils.XposedUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import okhttp3.Response;

@SuppressWarnings("deprecation")
public class SystemUIHooker implements IXposedHookLoadPackage {

    private static final String LOG_PREV = "killonresume---";

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) {
        try {
//            Dialog(loadPackageParam.classLoader);
            // 关闭首页弹出窗
            Dialog2(loadPackageParam.classLoader);
            // 首页的帖子
            resultAop(loadPackageParam.classLoader);
            // 给用户名控件加长按事件
            setText2(loadPackageParam.classLoader);
            // 测试贴吧内的效果
            baNeiLieBiao(loadPackageParam.classLoader);

        } catch (Exception e) {
            XposedBridge.log(LOG_PREV + "hook -- vibrateSuccessAndError 错误");
            XposedBridge.log(e);
        }
    }


    private void resultAop(ClassLoader classLoader) {

        // com.baidu.tieba.homepage.personalize.data.RecPersonalizeHttpResponse getResultData
        Class<?> clazz = XposedUtil.findClass4Xposed(
                "com.baidu.tieba.homepage.personalize.data.RecPersonalizeHttpResponse", classLoader);
        XposedBridge.log(LOG_PREV + "hook 了 类 " +
                "squareup.wire = " + clazz);

        XC_MethodHook xcMethodHook = new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) {

//                XposedBridge.log(LOG_PREV + "调用了 了 personalize.RecPersonalizeHttpResponse 的 getResultData 方法 = " + param.thisObject);
                Object result = param.getResult();
                List<?> list = (List<?>) ReflexUtil.getField4Obj(result, "thread_list");
                for (Object o : list) {
                    Object author = ReflexUtil.getField4Obj(o, "author");
                    Object name_show = ReflexUtil.getField4Obj(author, "name_show");
                    // todo 首页的帖子
                    ReflexUtil.setField4Obj("title", o, name_show + " | " + ReflexUtil.getField4Obj(o, "title"));
                }
            }
        };
        if (clazz != Exception.class) {
            //Hook有参构造函数，修改参数 pass real auth token once fp HAL supports it personalize_tab_shadow
            XposedHelpers.findAndHookMethod(clazz
                    , "getResultData"
                    , xcMethodHook);
        } else {
            XposedBridge.log(LOG_PREV + " clazz 为空");
        }
    }


    private void resultAop2(ClassLoader classLoader) {
        // 测试贴吧内页 todo
        // com.baidu.tieba.tbadkCore.FrsPageHttpResponseMessage getResponseData
        Class<?> clazz = XposedUtil.findClass4Xposed(
                "com.baidu.tieba.tbadkCore.FrsPageHttpResponseMessage", classLoader);
        Class<?> clazzWireInput = XposedUtil.findClass4Xposed(
                "com.squareup.wire.WireInput", classLoader);
        XposedBridge.log(LOG_PREV + "hook 了 类 " +
                "squareup.wire = " + clazz);

        XC_MethodHook xcMethodHook = new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) {

                XposedBridge.log(LOG_PREV + "调用了 了 FrsPageHttpResponseMessage 的 getResponseData 方法 = " + param.thisObject);
//                Object result = param.getResult();
                Object result = ReflexUtil.getField4Obj(param.thisObject, "responseData");
                System.out.println(result);
                System.out.println(new Gson().toJson(result));
                System.out.println("打完了");
                throw new RuntimeException("错错错getResponseData");
            }
        };
        if (clazz != Exception.class) {
            //Hook有参构造函数，修改参数 pass real auth token once fp HAL supports it personalize_tab_shadow
            XposedHelpers.findAndHookMethod(clazz
                    , "decodeInBackGround"
                    , int.class, byte[].class
                    , xcMethodHook);
        } else {
            XposedBridge.log(LOG_PREV + " clazz 为空");
        }
    }


    public static final int MAX_LENGTH = 2000; // Android 日志限制字符数

    // 将长字符串分段
    public static void printLongLog(String log) {
        int length = log.length();
        System.out.println("总长度 = " + length);
        for (int i = 0; i < length; i += MAX_LENGTH) {
            // 获取每个子串
            int end = Math.min(length, i + MAX_LENGTH);
            XposedBridge.log(log.substring(i, end)); // 输出日志
//            System.out.println(log.substring(i, end));
            if (end == length) {
                break;
            }
        }
    }

    private void baNeiLieBiao(ClassLoader classLoader) {

        // com.squareup.wire.parseFrom WireInput Class
        Class<?> clazz = XposedUtil.findClass4Xposed(
                "com.squareup.wire.Wire", classLoader);
        Class<?> clazzWireInput = XposedUtil.findClass4Xposed(
                "com.squareup.wire.WireInput", classLoader);
        XposedBridge.log(LOG_PREV + "hook 了 类 " +
                "squareup.wire = " + clazz);

        XC_MethodHook xcMethodHook = new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) {
                try {
                    // decodeInBackGround
                    Object result = param.getResult();
                    XposedBridge.log(LOG_PREV + "调用了 了 okhttp3.Request 的 build 方法 = " + param.getResult().getClass());
//                XposedBridge.log(LOG_PREV + param.getResult());
                    Object data = ReflexUtil.getField4Obj(result, "data");
                    if ( param.getResult().getClass().getName().contains("FrsPageResIdl") ) {
//                        BlackListDao.saveString(AndroidAppHelper.currentApplication(), "吧内", new Gson().toJson(data));
                        Object forum = ReflexUtil.getField4Obj(data, "forum");
                        // 拿到第一处帖子ids
                        Object tids = ReflexUtil.getField4Obj(forum, "tids");
                        // 第二处ids
                        Object thread_id_list = ReflexUtil.getField4Obj(data, "thread_id_list");
                        // 作者们
                        List<?> user_list = (List<?>) ReflexUtil.getField4Obj(data, "user_list");
                        // 建立作者和id和作者名的映射map
                        Map<Long, String> map = new HashMap<>();
                        for (Object userInfo : user_list) {
                            Long id = (Long) ReflexUtil.getField4Obj(userInfo, "id");
                            String name = (String) ReflexUtil.getField4Obj(userInfo, "name");
                            String name_show = (String) ReflexUtil.getField4Obj(userInfo, "name_show");
                            map.put(id, name_show);
                        }


                        // 第三处 帖子们的简易属性 "is_partial_visible": 0, "tid": 9219539338
                        Object thread_id_list_info = ReflexUtil.getField4Obj(data, "thread_id_list_info");
                        // 帖子们
                        List<?> thread_list = (List<?>) ReflexUtil.getField4Obj(data, "thread_list");
                        Application application = AndroidAppHelper.currentApplication();
                        // 拿到每个帖子的id 和 authorid

                        List list = (List) ReflexUtil.getField4Obj(thread_list.getClass().getSuperclass(), thread_list, "list");
//                        System.out.println("data 类型是 = " + list);
                        list.removeIf(threadInfo -> {
//                        Object tid = ReflexUtil.getField4Obj(threadInfo, "id");
                            Long authorId = (Long) ReflexUtil.getField4Obj(threadInfo, "author_id");
                            String authorName = map.get(authorId);
                            return BlackListDao.getUserNameList(application).contains(authorName);
//                        Object title = ReflexUtil.getField4Obj(threadInfo, "title");
//                        System.out.println("贴ID: " + tid);
//                        System.out.println("贴作者id: " + authorId);
//                        System.out.println("贴作者: " + map.get(authorId));
//                        System.out.println("贴标题: " + title);
                        });
                    }
                } catch (Exception e) {
                    XposedBridge.log(e);
                }

            }
        };
        if (clazz != Exception.class) {
            //Hook有参构造函数，修改参数 pass real auth token once fp HAL supports it personalize_tab_shadow
            XposedHelpers.findAndHookMethod(clazz
//                    , Request.Builder.class
                    , "parseFrom"
                    , clazzWireInput, Class.class
                    , xcMethodHook);
        } else {
            XposedBridge.log(LOG_PREV + " clazz 为空");
        }

    }

    private void setText2(ClassLoader classLoader) {
        Class<?> clazz = XposedUtil.findClass4Xposed(
                "android.widget.TextView", classLoader);
        Class<?> emTextView = XposedUtil.findClass4Xposed(
                "com.baidu.tbadk.core.elementsMaven.view.EMTextView", classLoader);
        XposedBridge.log(LOG_PREV + "hook 了 类 " +
                "okhttp3.Request = " + clazz);

        XC_MethodHook xcMethodHook = new XC_MethodHook() {
            @SuppressLint("ResourceAsColor")
            @Override
            protected void afterHookedMethod(MethodHookParam param) {

//                XposedBridge.log(LOG_PREV + "调用了 了 TextView 的 setText 方法 = " + param.args[0]);

//                System.out.println(currentActivity);
                // 这是 用户名那个的特征 com.baidu.tbadk.core.elementsMaven.view.EMTextView{5a8657c VFED..C.. ......ID 0,7-0,60 #7f092735 app:id/user_name}
                if( emTextView == param.thisObject.getClass() && param.thisObject.toString().contains("app:id/user_name") ) {
                    Object userName = param.args[0];
//                    System.out.println("用户名 = " + userName);
//                    System.out.println(param.thisObject);
                    // todo 长按询问是否屏蔽
                    TextView textView = (TextView) param.thisObject;
                    Context context = (Context) ReflexUtil.getField4Obj(View.class, param.thisObject, "mContext");
                    textView.setOnLongClickListener(v -> {
                        //添加确定按钮
                        AlertDialog.Builder builder = new AlertDialog.Builder(context, android.R.style.ThemeOverlay_Material_Dialog);
                        builder.setTitle("是否屏蔽: " + userName);
                        builder.setPositiveButton("确定", (dialog, which) -> {
                            // todo
                            boolean add = BlackListDao.add(context, userName.toString());
                            ((Activity)context).runOnUiThread(()
                                    -> Toast.makeText(context, "添加" + (add ? "成功" : "失败"), Toast.LENGTH_SHORT).show());
                        });
                        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                        return true;
                    });
                    return;
                }

                if ("意见反馈".equals(param.args[0]) && getCurrentActivity(AndroidAppHelper.currentApplication()).contains("MoreActivity")) {
//                    System.out.println("是这里了");
                    Context context = (Context) ReflexUtil.getField4Obj(View.class, param.thisObject, "mContext");
                    TextView textView = (TextView) param.thisObject;
                    textView.setText("意见反馈(长按管理屏蔽列表)");
                    textView.setOnLongClickListener(v -> {
                        //添加确定按钮
                        //添加返回按钮
                        AlertDialog.Builder builder = new AlertDialog.Builder(context, android.R.style.ThemeOverlay_Material_Dialog);
                        builder.setTitle("屏蔽列表 点击可删除");
                        // 起一行
                        LinearLayout linearLayout = new LinearLayout(context);
                        linearLayout.setBackgroundColor(android.R.color.holo_red_light);
                        // 设置LinearLayout的宽度为固定值，如100dp
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT, // 宽度值，单位为px
                                LinearLayout.LayoutParams.WRAP_CONTENT // 高度属性，这里设置为WRAP_CONTENT
                        );
                        linearLayout.setOrientation(LinearLayout.VERTICAL);
                        linearLayout.setGravity(Gravity.CENTER_VERTICAL);
                        linearLayout.setLayoutParams(layoutParams);
                        List<String> userNameList = BlackListDao.getUserNameList(context);
                        for (String userName : userNameList) {
                            Button button = new Button(context);
                            button.setText(userName);
                            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                                    LinearLayout.LayoutParams.WRAP_CONTENT, // 宽度值，单位为px
                                    LinearLayout.LayoutParams.WRAP_CONTENT // 高度属性，这里设置为WRAP_CONTENT
                            );
                            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                            params.setMargins(0, 0, 1, 0); // 设置右边距为16dp
                            button.setLayoutParams(params);
                            linearLayout.addView(button);
                            // 添加删除事件
                            button.setOnClickListener(v1 -> {
                                BlackListDao.remove(context, userName);
                                // 删除元素
                                linearLayout.removeView(v1);
                            });

                        }
                        // todo 设置进去?
                        ScrollView scrollView = new ScrollView(context);
                        FrameLayout.LayoutParams layoutParams1 = new FrameLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT, // 宽度值，单位为px
                                LinearLayout.LayoutParams.MATCH_PARENT // 高度属性，这里设置为WRAP_CONTENT
                        );
                        scrollView.setLayoutParams(layoutParams1);
                        scrollView.addView(linearLayout);
                        builder.setView(scrollView);
                        builder.setPositiveButton("清空", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                        return true;
                    });
                }
            }
        };


        if (clazz != Exception.class) {
            // android.widget.ImageView.setImageDrawable
            //Hook有参构造函数，修改参数 pass real auth token once fp HAL supports it
            XposedHelpers.findAndHookMethod(clazz
//                    , Request.Builder.class
                    , "setText"
                    , CharSequence.class
                    , xcMethodHook);
        } else {
            XposedBridge.log(LOG_PREV + " clazz 为空");
        }

    }

    public static String getCurrentActivity(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        String currentActivity = null;
        if (activityManager != null) {
            currentActivity = activityManager.getRunningTasks(1).get(0).topActivity.getClassName();
        }
        return currentActivity;
    }

    //  android.app.Dialog.show
    private void Dialog(ClassLoader classLoader) {
        // 安卓 12
        Class<?> clazz = XposedUtil.findClass4Xposed(
                "android.app.Dialog", classLoader);
        XposedBridge.log(LOG_PREV + "hook 了 show = " + clazz);

        XC_MethodHook xcMethodHook = new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) {

                XposedBridge.log(LOG_PREV + "调用了 了 AlertDialog 的 show 方法 = " + param.thisObject);

                AlertDialog dialog = (AlertDialog) param.thisObject;

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }
                }, 100); // 3秒后关闭
            }
        };


        if (clazz != Exception.class) {
            //Hook有参构造函数，修改参数 pass real auth token once fp HAL supports it
            XposedHelpers.findAndHookMethod(clazz, "show"
                    , xcMethodHook);
        }

    }

    private void Dialog2(ClassLoader classLoader) {
        // android.app.Dialog.setTitle(java.lang.CharSequence)
        Class<?> clazz = XposedUtil.findClass4Xposed(
                "android.app.Dialog", classLoader);
        XposedBridge.log(LOG_PREV + "hook 了 show = " + clazz);

        XC_MethodHook xcMethodHook = new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) {

                XposedBridge.log(LOG_PREV + "调用了 了 AlertDialog 的 show 方法 = " + param.thisObject);

                AlertDialog dialog = (AlertDialog) param.thisObject;
                Window mWindow = (Window) ReflexUtil.getField4Obj(clazz, dialog, "mWindow");
                System.out.println("title ==== " + mWindow.getAttributes().getTitle());
                System.out.println("getContext === " + dialog.getContext());
                String currentActivity = getCurrentActivity(dialog.getContext());
                System.out.println(currentActivity);
//                new RuntimeException("1").printStackTrace();
                for (StackTraceElement stackTraceElement : new RuntimeException("1").getStackTrace()) {
                    if (stackTraceElement.getClassName().contains("dymod")) {
                        Handler handler = new Handler();
                        handler.postDelayed(() -> {
                            if (dialog.isShowing()) {
                                dialog.dismiss();
                            }
                        }, 100); // 3秒后关闭

                        break;
                    }

                }
//                System.out.println("title ==== " + new Gson().toJson(dialog));


            }
        };


        if (clazz != Exception.class) {
            //Hook有参构造函数，修改参数 pass real auth token once fp HAL supports it
            XposedHelpers.findAndHookMethod(clazz, "show"
//                    , CharSequence.class
                    , xcMethodHook);
        }

    }


}
