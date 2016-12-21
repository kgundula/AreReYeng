package za.co.gundula.app.arereyeng.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by kgundula on 2016/12/07.
 */

public class AreYengSyncService extends Service {

    private static final Object aSyncAdapterLock = new Object();
    private static AreYengSyncAdapter areYengSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d("AReYengSyncService", "onCreate - AreYengSyncService");
        synchronized (aSyncAdapterLock) {
            if (areYengSyncAdapter == null) {
                areYengSyncAdapter = new AreYengSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return areYengSyncAdapter.getSyncAdapterBinder();
    }
}
