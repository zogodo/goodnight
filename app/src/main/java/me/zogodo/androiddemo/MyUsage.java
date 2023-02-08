package me.zogodo.androiddemo;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;

import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MyUsage
{
    public static void TestEvent(Context context)
    {
        long endTime = System.currentTimeMillis();
        long beginTime = endTime - 1000 * 60 * 60 * 24; //最近一天
        UsageStatsManager manager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        UsageEvents usageEvents = manager.queryEvents(beginTime, endTime);

        SQLiteDatabase db = MainActivity.dbHelper.getWritableDatabase();

        UsageEvents.Event eventOut;
        Log.e("zzze0", "a", null);
        while (usageEvents.hasNextEvent()) {
            eventOut = new UsageEvents.Event();
            usageEvents.getNextEvent(eventOut);
            Timestamp ts = new Timestamp(eventOut.getTimeStamp());
            int t = eventOut.getEventType();
            if (t == UsageEvents.Event.SCREEN_INTERACTIVE         //亮屏15
                || t == UsageEvents.Event.SCREEN_NON_INTERACTIVE  //灭屏16
                || t == UsageEvents.Event.KEYGUARD_SHOWN          //锁屏17
                || t == UsageEvents.Event.KEYGUARD_HIDDEN)        //解锁18
            {
                Log.e("zzze1", ts.toString() + " t = " + eventOut.getEventType(), null);
                String sql = "insert into event(`time`, `type`) values(?, ?)";
                Object[] pras = {eventOut.getTimeStamp(), t};
                db.execSQL(sql, pras);
            }
        }
        Log.e("zzze0", "z", null);
    }

    @JavascriptInterface
    public static String GetAllEvent() {
        SQLiteDatabase db = MainActivity.dbHelper.getWritableDatabase();
        String sql = "select * from event";
        Cursor cursor = db.rawQuery(sql, null);

        Map<String, Integer> events = new HashMap<>();
        try {
            while (cursor.moveToNext()) {
                Map<Date, Integer> event = new HashMap<>();
                events.put(cursor.getString(cursor.getColumnIndexOrThrow("time")), cursor.getInt(cursor.getColumnIndexOrThrow("type")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        cursor.close();

        JSONObject json = new JSONObject(events);
        String jsonStr = json.toString();
        Log.e("xx ", "zzzj" + json.toString(), null);
        return  json.toString();
    }

}
