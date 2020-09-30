package za.co.gundula.app.arereyeng.sync

import android.accounts.Account
import android.accounts.AccountManager
import android.content.*
import android.os.Build
import android.os.Bundle
import android.util.Log
import za.co.gundula.app.arereyeng.R
import za.co.gundula.app.arereyeng.rest.WhereIsMyTransportTokenApiClient

/**
 * Created by kgundula on 2016/12/07.
 */
class AreYengSyncAdapter(context: Context?, autoInitialize: Boolean) : AbstractThreadedSyncAdapter(context, autoInitialize) {
    val LOG_TAG = AreYengSyncAdapter::class.java.simpleName
    override fun onPerformSync(account: Account, bundle: Bundle, s: String, contentProviderClient: ContentProviderClient, syncResult: SyncResult) {

        // Implement the actualy sync code.
        val whereIsMyTransportTokenApiClient = WhereIsMyTransportTokenApiClient()
        whereIsMyTransportTokenApiClient.getToken(context)
    }

    companion object {
        const val ACTION_DATA_UPDATED = "za.co.gundula.app.arereyeng.ACTION_DATA_UPDATED"

        // Interval at which to sync with the whereismytransport, in seconds.
        // 56 seconds (1 minute) * 3 = 2.8 minutues
        const val SYNC_INTERVAL = 56 * 3
        const val SYNC_FLEXTIME = SYNC_INTERVAL / 3
        private const val DAY_IN_MILLIS = 1000 * 60 * 60 * 24.toLong()

        /**
         * Helper method to schedule the sync adapter periodic execution
         */
        fun configurePeriodicSync(context: Context, syncInterval: Int, flexTime: Int) {
            val account = getSyncAccount(context)
            val authority = context.getString(R.string.content_authority)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                // we can enable inexact timers in our periodic sync
                val request = SyncRequest.Builder().syncPeriodic(syncInterval.toLong(), flexTime.toLong()).setSyncAdapter(account, authority).setExtras(Bundle()).build()
                ContentResolver.requestSync(request)
            } else {
                ContentResolver.addPeriodicSync(account,
                        authority, Bundle(), syncInterval.toLong())
            }
        }

        /**
         * Helper method to have the sync adapter sync immediately
         *
         * @param context The context used to access the account service
         */
        fun syncImmediately(context: Context) {
            val bundle = Bundle()
            bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true)
            bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true)
            ContentResolver.requestSync(getSyncAccount(context),
                    context.getString(R.string.content_authority), bundle)
        }

        /**
         * Helper method to get the fake account to be used with SyncAdapter, or make a new one
         * if the fake account doesn't exist yet.  If we make a new account, we call the
         * onAccountCreated method so we can initialize things.
         *
         * @param context The context used to access the account service
         * @return a fake account.
         */
        fun getSyncAccount(context: Context): Account? {
            // Get an instance of the Android account manager
            val accountManager = context.getSystemService(Context.ACCOUNT_SERVICE) as AccountManager

            // Create the account type and default account
            val newAccount = Account(
                    context.getString(R.string.app_name), context.getString(R.string.sync_account_type))

            // If the password doesn't exist, the account doesn't exist
            if (null == accountManager.getPassword(newAccount)) {

                /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
                if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                    return null
                }
                /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */onAccountCreated(newAccount, context)
            }
            return newAccount
        }

        private fun onAccountCreated(newAccount: Account, context: Context) {
            /*
         * Since we've created an account
         */
            configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME)

            /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true)

            /*
         * Finally, let's do a sync to get things started
         */syncImmediately(context)
        }

        @JvmStatic
        fun initializeSyncAdapter(context: Context) {
            Log.i("Sync Adapter", "Initialiase")
            getSyncAccount(context)
        }
    }
}