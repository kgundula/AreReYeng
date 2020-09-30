package za.co.gundula.app.arereyeng.sync

import android.accounts.AbstractAccountAuthenticator
import android.accounts.Account
import android.accounts.AccountAuthenticatorResponse
import android.accounts.NetworkErrorException
import android.content.Context
import android.os.Bundle

/**
 * Created by kgundula on 2016/12/07.
 */
class AreYengAuthenticator(context: Context?) : AbstractAccountAuthenticator(context) {
    override fun editProperties(accountAuthenticatorResponse: AccountAuthenticatorResponse, s: String): Bundle {
        throw UnsupportedOperationException()
    }

    @Throws(NetworkErrorException::class)
    override fun addAccount(accountAuthenticatorResponse: AccountAuthenticatorResponse, s: String, s1: String, strings: Array<String>, bundle: Bundle): Bundle {
        return null
    }

    @Throws(NetworkErrorException::class)
    override fun confirmCredentials(accountAuthenticatorResponse: AccountAuthenticatorResponse, account: Account, bundle: Bundle): Bundle {
        return null
    }

    @Throws(NetworkErrorException::class)
    override fun getAuthToken(accountAuthenticatorResponse: AccountAuthenticatorResponse, account: Account, s: String, bundle: Bundle): Bundle {
        throw UnsupportedOperationException()
    }

    override fun getAuthTokenLabel(s: String): String {
        throw UnsupportedOperationException()
    }

    @Throws(NetworkErrorException::class)
    override fun updateCredentials(accountAuthenticatorResponse: AccountAuthenticatorResponse, account: Account, s: String, bundle: Bundle): Bundle {
        throw UnsupportedOperationException()
    }

    @Throws(NetworkErrorException::class)
    override fun hasFeatures(accountAuthenticatorResponse: AccountAuthenticatorResponse, account: Account, strings: Array<String>): Bundle {
        throw UnsupportedOperationException()
    }
}