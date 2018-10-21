package com.example.thita.fabapp;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;


public class WidgetUpdateService extends IntentService {
    public static final String ACTION_UPDATE_TOTAL = "Update Total";

    public WidgetUpdateService(String name) {
        super("WidgetUpdateService");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent !=null){
            final String action = intent.getAction();
            //check action order if correct the get current recipe and handleAction update listview
            if (ACTION_UPDATE_TOTAL.equals(action)){
                String totalItem = intent.getStringExtra(String.valueOf(R.string.TotalItem));
                handleActionUpdateListView(totalItem);
            }
        }
    }

    private void handleActionUpdateListView(String totalItem) {
        if (totalItem != null) {
            SharedPreferences appSharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
            prefsEditor.putString(String.valueOf(R.string.TotalItem), totalItem);
            prefsEditor.commit();
        }
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, GrabMeWidget.class));

        GrabMeWidget.updateAppWidgets(this, appWidgetManager, appWidgetIds);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.appwidet_app);
    }

    public static void startActionUpdateTotal(Context context, String count) {
        Intent intent = new Intent(context, WidgetUpdateService.class);
        intent.setAction(WidgetUpdateService.ACTION_UPDATE_TOTAL);
        intent.putExtra(String.valueOf(R.string.TotalItem), count);
        Log.d("count..." , count + "");
        context.startService(intent);
    }
}
