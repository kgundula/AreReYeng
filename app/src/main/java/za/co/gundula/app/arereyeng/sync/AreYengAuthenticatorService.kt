package za.co.gundula.app.arereyeng.sync

import android.app.Service
import android.content.Intent
import android.os.IBinder

/**
 * Created by kgundula on 2016/12/07.
 */
class AreYengAuthenticatorService : Service() {
    private var mAuthenticator: AreYengAuthenticator? = null
    override fun onCreate() {
        // Create a new authenticator object
        mAuthenticator = AreYengAuthenticator(this)
    }

    override fun onBind(intent: Intent): IBinder? {
        return mAuthenticator!!.iBinder
    }
}