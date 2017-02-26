package za.co.gundula.app.arereyeng.ui;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import za.co.gundula.app.arereyeng.R;
import za.co.gundula.app.arereyeng.data.AreYengContract;

/**
 * Created by kgundula on 2017/02/26.
 */

public class BusStopWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new BusStopRemoteViewFactory(this.getApplicationContext(), intent);
    }

    public class BusStopRemoteViewFactory implements RemoteViewsFactory {

        Context context;
        Cursor cursor;
        int appWidgetId;

        String[] BUS_STOP_PROJECTION = new String[]{
                AreYengContract.BusStopEntry.COLUMN_ID,
                AreYengContract.BusStopEntry.COLUMN_NAME,
                AreYengContract.BusStopEntry.COLUMN_CODE,
                AreYengContract.BusStopEntry.COLUMN_HREF,
                AreYengContract.BusStopEntry.COLUMN_MODES,
                AreYengContract.BusStopEntry.COLUMN_GEOMETRY_LATITUDE,
                AreYengContract.BusStopEntry.COLUMN_GEOMETRY_LONGITUDE,
                AreYengContract.BusStopEntry.COLUMN_GEOMETRY_TYPE,
        };

        public static final int COL_BUS_STOP_ID = 0;
        public static final int COL_BUS_STOP_NAME = 1;
        public static final int COL_BUS_STOP_CODE = 2;

        public BusStopRemoteViewFactory(Context context, Intent intent) {
            this.context = context;
            this.appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        @Override
        public void onCreate() {

            cursor = getContentResolver().query(
                    AreYengContract.BusStopEntry.CONTENT_URI,
                    BUS_STOP_PROJECTION,
                    null,
                    null,
                    null);

        }

        @Override
        public void onDataSetChanged() {
            cursor = getContentResolver().query(
                    AreYengContract.BusStopEntry.CONTENT_URI,
                    BUS_STOP_PROJECTION,
                    null,
                    null,
                    null);
        }

        @Override
        public void onDestroy() {
            if (this.cursor != null)
                this.cursor.close();
        }

        @Override
        public int getCount() {
            return (this.cursor != null) ? this.cursor.getCount() : 0;
        }

        @Override
        public RemoteViews getViewAt(int position) {
            RemoteViews remoteViews = new RemoteViews(this.context.getPackageName(), R.layout.list_item_bus_stop);

            if (cursor != null && this.cursor.moveToPosition(position)) {

                String bus_id = cursor.getString(COL_BUS_STOP_ID);
                String stop_name = cursor.getString(COL_BUS_STOP_NAME);
                String stop_code = cursor.getString(COL_BUS_STOP_CODE);

                remoteViews.setTextViewText(R.id.bus_stop_name, stop_name);
                //remoteViews.setTextViewText(R.id.bus_stop_code, stop_code);


                Bundle bundle = new Bundle();
                bundle.putString(BusStopWidget.EXTRA_BUS_STOP_ID, cursor.getString(COL_BUS_STOP_ID));
                bundle.putString(BusStopWidget.EXTRA_BUS_STOP_NAME, cursor.getString(COL_BUS_STOP_NAME));
                bundle.putString(BusStopWidget.EXTRA_BUS_STOP_CODE, cursor.getString(COL_BUS_STOP_CODE));

                Intent intent = new Intent();
                intent.putExtras(bundle);
                remoteViews.setOnClickFillInIntent(R.id.linearLayout, intent);

            }

            return remoteViews;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return this.cursor.getInt(0);
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }
    }
}
