package za.co.gundula.app.arereyeng.sync

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

/**
 * Created by kgundula on 2016/12/07.
 */
class AreYengSyncService : Service() {
    override fun onCreate() {
        Log.d("AReYengSyncService", "onCreate - AreYengSyncService")
        synchronized(aSyncAdapterLock) {
            if (areYengSyncAdapter == null) {
                areYengSyncAdapter = AreYengSyncAdapter(applicationContext, true)
            }
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return areYengSyncAdapter!!.syncAdapterBinder
    }

    companion object {
        private val aSyncAdapterLock = Any()
        private var areYengSyncAdapter: AreYengSyncAdapter? = null
    }
}