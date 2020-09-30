package za.co.gundula.app.arereyeng.ui

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.preference.PreferenceManager
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.common.api.Status
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import za.co.gundula.app.arereyeng.R
import za.co.gundula.app.arereyeng.ui.BaseActivity
import za.co.gundula.app.arereyeng.utils.Constants

/**
 * Created by kgundula on 2016/11/07.
 */
abstract class BaseActivity : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {
    protected var mProvider: String? = null
    protected var mEncodedEmail: String? = null

    /* Client used to interact with Google APIs. */
    @JvmField
    protected var mGoogleApiClient: GoogleApiClient? = null
    protected var mAuth: FirebaseAuth? = null
    protected var mAuthListener: AuthStateListener? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /* Setup the Google API object to allow Google logins */
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        /**
         * Build a GoogleApiClient with access to the Google Sign-In API and the
         * options specified by gso.
         */

        /* Setup the Google API object to allow Google+ logins */mGoogleApiClient = GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build()
        val sp = PreferenceManager.getDefaultSharedPreferences(this@BaseActivity)
        /* Get mEncodedEmail and mProvider from SharedPreferences, use null as default value */mEncodedEmail = sp.getString(Constants.key_encoded_email, null)
        mProvider = sp.getString(Constants.key_provider, null)
        if (!(this is LoginActivity || this is RegisterActivity)) {
            mAuth = FirebaseAuth.getInstance()
            mAuthListener = AuthStateListener { firebaseAuth ->
                val user = firebaseAuth.currentUser
                if (user != null) {
                    //Log.i("Ygritte", "User UID : " + user.getUid());
                } else {
                    val spe = sp.edit()
                    spe.putString(Constants.key_encoded_email, "")
                    spe.putString(Constants.key_provider, "")
                    spe.apply()
                    takeUserToLoginScreenOnUnAuth()
                }
            }
            mAuth!!.addAuthStateListener(mAuthListener)
        }
    }

    public override fun onDestroy() {
        super.onDestroy()
        /* Cleanup the AuthStateListener */if (!(this is LoginActivity || this is RegisterActivity)) {
            mAuth!!.removeAuthStateListener(mAuthListener!!)
        }
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            super.onBackPressed()
            return true
        }
        if (id == R.id.action_logout) {
            logout()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun takeUserToLoginScreenOnUnAuth() {
        /* Move user to Login Activity, and remove the BackStack */
        val intent = Intent(this@BaseActivity, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    /**
     * Logs out the user from their current session and starts LoginActivity.
     * Also disconnects the mGoogleApiClient if connected and provider is Google
     */
    protected fun logout() {
        /* Logout if mProvider is not null */
        if (mProvider != null) {
            FirebaseAuth.getInstance().signOut()
            if (mProvider == Constants.google_provider) {
                /* Logout from Google+ */
                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                        object : ResultCallback<Status?> {
                            override fun onResult(status: Status?) {
                                //nothing
                            }
                        })
            }
        }
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {}

    companion object {
        var LOG_TAG = BaseActivity::class.java.name
    }
}