package za.co.gundula.app.arereyeng.ui;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.RemoteViews;

import za.co.gundula.app.arereyeng.R;

/**
 * Implementation of App Widget functionality.
 */
public class BusStopWidget extends AppWidgetProvider {

    public static final String INTENT_ACTION = "INTENT_ACTION";

    public static final String EXTRA_BUS_STOP_ID = "EXTRA_BUS_STOP_ID";
    public static final String EXTRA_BUS_STOP_NAME = "EXTRA_BUS_STOP_NAME";
    public static final String EXTRA_BUS_STOP_CODE = "EXTRA_BUS_STOP_CODE";


    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (INTENT_ACTION.equals(intent.getAction())) {

            Intent busTimetableIntent = new Intent(context, BusTimetableActivity.class);
            busTimetableIntent.putExtra(EXTRA_BUS_STOP_ID, intent.getStringExtra(EXTRA_BUS_STOP_ID));
            busTimetableIntent.putExtra(EXTRA_BUS_STOP_NAME, intent.getStringExtra(EXTRA_BUS_STOP_NAME));
            busTimetableIntent.putExtra(EXTRA_BUS_STOP_CODE, intent.getStringExtra(EXTRA_BUS_STOP_CODE));
            busTimetableIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(busTimetableIntent);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int i = 0; i < appWidgetIds.length; i++) {
            Intent intent = new Intent(context, BusStopWidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.bus_stop_widget);
            remoteViews.setRemoteAdapter(appWidgetIds[i], R.id.bus_stop_widget_layout, intent);
            remoteViews.setEmptyView(R.id.bus_stop_widget_layout, R.id.empty_bus_stop_widget_layout);

            Intent busStopDetails = new Intent(context, BusStopWidget.class);
            busStopDetails.setAction(BusStopWidget.INTENT_ACTION);
            busStopDetails.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, busStopDetails, PendingIntent.FLAG_UPDATE_CURRENT);

            remoteViews.setPendingIntentTemplate(R.id.bus_stop_widget_layout, pendingIntent);
            appWidgetManager.updateAppWidget(appWidgetIds[i], remoteViews);

        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);
        context.startService(new Intent(context, BusStopWidgetService.class));
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {

        context.startService(new Intent(context, BusStopWidgetService.class));
    }

}

