package za.co.gundula.app.arereyeng.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import za.co.gundula.app.arereyeng.Constants;
import za.co.gundula.app.arereyeng.R;

public class RegisterActivity extends BaseActivity {

    ProgressDialog mAuthProgressDialog;

    @BindView(R.id.activity_register)
    RelativeLayout rootLayout;

    @BindView(R.id.email_register)
    EditText email_register;

    @BindView(R.id.password_register)
    EditText password_register;


    private SharedPreferences mSharedPref;
    private SharedPreferences.Editor mSharedPrefEditor;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        mSharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        mSharedPrefEditor = mSharedPref.edit();

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    mSharedPrefEditor.putString(Constants.key_provider, user.getProviderId()).apply();
                    mSharedPrefEditor.putString(Constants.key_encoded_email, user.getEmail()).apply();
                    if (user.getPhotoUrl() != null) {
                        mSharedPrefEditor.putString(Constants.key_user_avatar, user.getPhotoUrl().toString()).apply();
                    }
                    showOnBoarding();
                }
            }
        };

        mAuthProgressDialog = new ProgressDialog(this);
        mAuthProgressDialog.setTitle(getResources().getString(R.string.progress_dialog_loading));
        mAuthProgressDialog.setMessage(getResources().getString(R.string.progress_dialog_check_inbox));
        mAuthProgressDialog.setCancelable(false);

    }


    @OnClick(R.id.email_sign_up_button)
    public void email_sign_up_button(View view) {

        String email_address = email_register.getText().toString();
        String password = password_register.getText().toString().toLowerCase();

        /**
         *  Check that email and password filled
         */
        boolean validEmail = isEmailValid(email_address);
        boolean validPassword = emptyPassword(password);
        if (!validEmail || !validPassword) return;

        mAuthProgressDialog.show();

        mAuth.createUserWithEmailAndPassword(email_address, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    String unprocessedEmail = task.getResult().getUser().getEmail();
                    String user_uid = task.getResult().getUser().getUid();

                    mAuthProgressDialog.dismiss();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mAuthProgressDialog.dismiss();
                showSnackBar(e.getMessage());

            }
        });

    }

    private boolean emptyPassword(String mPassword) {
        boolean correct = (mPassword.length() > 0);
        if (!correct) {
            password_register.setError(String.format(getString(R.string.error_invalid_password_not_valid)));
            return false;
        }
        return correct;

    }

    private boolean isEmailValid(String email) {
        boolean isGoodEmail = (email != null && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches());
        if (!isGoodEmail) {
            email_register.setError(String.format(getString(R.string.error_invalid_email_not_valid), email));
            showSnackBar(String.format(getString(R.string.error_invalid_email_not_valid), email));
            return false;
        }
        return isGoodEmail;
    }

    public void showSnackBar(String message) {
        Snackbar snackbar = Snackbar
                .make(rootLayout, message, Snackbar.LENGTH_LONG);
        snackbar.show();

    }

    @OnClick(R.id.btn_sign_in)
    public void btn_sign_in(View view) {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {


    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuthListener != null) {
            mAuth.addAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAuthListener != null) {
            mAuth.addAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    public void showOnBoarding() {
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

}
