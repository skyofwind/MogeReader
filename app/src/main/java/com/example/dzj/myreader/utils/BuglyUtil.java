package com.example.dzj.myreader.utils;

import android.content.Context;
import android.text.TextUtils;

import com.tencent.bugly.crashreport.CrashReport;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class BuglyUtil {

    private static String APPID = "03d3b840ab";
    /**
     * 获取进程号对应的进程名
     *
     * @param pid 进程号
     * @return 进程名
     */
    public static String getProcessName(int pid) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
            String processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }

    public static void initBugly(Context context, boolean isDebug){
    // 获取当前包名
        String packageName = context.getPackageName();
    // 获取当前进程名
        String processName = getProcessName(android.os.Process.myPid());
    // 设置是否为上报进程
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(context);
        strategy.setUploadProcess(processName == null || processName.equals(packageName));
    // 初始化Bugly
        CrashReport.initCrashReport(context, APPID, isDebug, strategy);
    // 如果通过“AndroidManifest.xml”来配置APP信息，初始化方法如下
    // CrashReport.initCrashReport(context, strategy);
    }
}
