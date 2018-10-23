package com.example.thita.fabapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 */
public class GrabMeWidget extends AppWidgetProvider {

    // TODO work on GrabeMeWiget  --> complete get data and service
    public static String  TOTAL = "24";
    private PendingIntent service;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        SharedPreferences pref = context.getSharedPreferences(DisplayImagesActivity.PREF_COUNT, context.MODE_PRIVATE);
        TOTAL = Integer.toString(pref.getInt(DisplayImagesActivity.PREF_COUNT, 0));
        Intent intent = new Intent(context, DisplayImagesActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.grab_me_widget);
        views.setOnClickPendingIntent(R.id.appwidget_btn, pendingIntent);
        views.setTextViewText(R.id.appwidget_btn, TOTAL + "");

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }


    public static void updateAppWidgets (Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }

        /**
        //get data
        SharedPreferences pref = context.getSharedPreferences(DisplayImagesActivity.PREF_COUNT, context.MODE_PRIVATE);
        int totalItem = pref.getInt(String.valueOf(R.string.TotalItem), 0);
        TOTAL = String .valueOf(totalItem);

        Intent intent = new Intent(context, DisplayImagesActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.grab_me_widget);
        views.setOnClickPendingIntent(R.id.appwidget_btn, pendingIntent);
        views.setTextViewText(R.id.appwidget_btn, TOTAL + "");

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);

        move to updateAppWidget **/
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        WidgetUpdateService.startActionUpdateTotal(context, null);

        /**
        final AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        final Intent i = new Intent(context, WidgetUpdateService.class);

        if (service == null) {
            service = PendingIntent.getService(context, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
        }
        manager.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), 60000, service);
        **/


        /**
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);s
        }
         Move to updateAppWidgets**/
    }


    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

        @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
//            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.grab_me_widget);
//            views.setTextViewText(R.id.appwidget_btn, String.valueOf(R.string.TotalItem));
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName componentName = new ComponentName(context.getPackageName(), GrabMeWidget.class.getName());
//            appWidgetManager.updateAppWidget(componentName, views);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(componentName);
        onUpdate(context, appWidgetManager, appWidgetIds);
    }
}

