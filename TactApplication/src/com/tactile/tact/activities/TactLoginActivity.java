package com.tactile.tact.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tactile.tact.R;
import com.tactile.tact.services.events.EventGoToSync;
import com.tactile.tact.services.events.EventLogInError;
import com.tactile.tact.services.events.EventLogInSuccess;
import com.tactile.tact.services.events.EventResetPasswordError;
import com.tactile.tact.services.events.EventResetPasswordSuccess;
import com.tactile.tact.services.network.TactNetworking;
import com.tactile.tact.utils.TactDialogHandler;
import com.tactile.tact.utils.Utils;

import de.greenrobot.event.EventBus;

public class TactLoginActivity extends TactBaseActivity {

    private EditText    edtUsername;
    private EditText    edtPassword;
    private TextView    txtError;
    private TactDialogHandler dialog;
    private Button btnLogin;
    private LinearLayout progresLayout;

    private String username;
    private String password;


    private enum ErrorType{
        InvalidUsernameOrPassword,
        CanNotLoginInTactServer,
        BlankEmailOrPassword,
        ResetPasswordEmailNotFound,
        incorrectEmailFormat
    }

    //*********************  UTILITIES *********************\\

    /**
     * Close dialog with 2 s of delay
     */
    public void closeDialogTimer(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                dialog.hide();
            }
        }, 2000);
    }

    /**
     * Show the error in the error TextView
     * @param error type of the error
     */
    public void showErrorPrompt(ErrorType error){
        switch (error) {
            case incorrectEmailFormat:
                this.txtError.setText(R.string.login_email_format_error);
                break;

            case CanNotLoginInTactServer:
                this.txtError.setText(R.string.login_server_error);
                break;

            case InvalidUsernameOrPassword:
                this.txtError.setText(R.string.login_email_error);
                break;

            case BlankEmailOrPassword:
                this.txtError.setText(R.string.login_email_error_empty_string);
                break;

            case ResetPasswordEmailNotFound:
                    this.txtError.setText(R.string.invalid_email);
                break;
        }
        this.txtError.setVisibility(View.VISIBLE);
        hideProgress();
    }

    private void showProgress() {
        btnLogin.setVisibility(View.GONE);
        btnLogin.setClickable(false);
        progresLayout.setVisibility(View.VISIBLE);
    }

    public void hideProgress() {
        btnLogin.setVisibility(View.VISIBLE);
        progresLayout.setVisibility(View.GONE);
        btnLogin.setClickable(true);
    }

    public void onEventMainThread(EventLogInError eventLogInError) {
        if (eventLogInError != null && eventLogInError.getError() != null && eventLogInError.getError().getCodeString() != null) {
            if (eventLogInError.getError().getCodeString().startsWith("4")) {
                showErrorPrompt(ErrorType.InvalidUsernameOrPassword);
            } else {
                showErrorPrompt(ErrorType.CanNotLoginInTactServer);
            }
        } else {
            showErrorPrompt(ErrorType.CanNotLoginInTactServer);
        }
    }

    public void onEventMainThread(EventLogInSuccess eventLogInSuccess) {
        EventBus.getDefault().post(new EventGoToSync());
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                finish();
            }
        }, 2000);
    }

    public void onEventMainThread(EventResetPasswordError eventResetPasswordError) {
        dialog.hideProcessCallback();
        //If error is 404 means the email do not exist
        if (eventResetPasswordError != null && eventResetPasswordError.getError() != null && eventResetPasswordError.getError().getCodeInt() == 404) {
            dialog.showErrorMessage(getResources().getString(R.string.invalid_email));
        } else {
            dialog.showErrorMessage(getResources().getString(R.string.login_server_error));
        }
        hideProgress();
    }

    public void onEventMainThread(EventResetPasswordSuccess eventResetPasswordSuccess) {
        dialog.hideProcessCallback();
        dialog.showSuccessMessage(getResources().getString(R.string.reset_password_success));
        closeDialogTimer();
        hideProgress();
    }

    //*********************  OVERRIDES METHODS  *********************\\

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login_to_tact);
        this.edtUsername    = (EditText) findViewById(R.id.email_text);
        this.edtPassword    = (EditText) findViewById(R.id.password_text);
        this.txtError       = (TextView) findViewById(R.id.incorrect_login_error);
        this.progresLayout  = (LinearLayout) findViewById(R.id.progress_layout_login);
        progresLayout.setVisibility(View.GONE);

        LinearLayout back_btn               = (LinearLayout) findViewById(R.id.back_btn);
        TextView title_text             = (TextView) findViewById(R.id.title_text);
//        ImageButton question_btn           = (ImageButton) findViewById(R.id.question_btn);

        title_text.setText(R.string.log_me_in);
//        question_btn.setVisibility(View.INVISIBLE);

        dialog = new TactDialogHandler(this);

        //Sets the click listener to "log in" button
        btnLogin = (Button) findViewById(R.id.log_in);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TactNetworking.checkInternetConnection(true)) {
                    username = edtUsername.getText().toString().trim();
                    password = edtPassword.getText().toString().trim();
                    if (username.isEmpty() || password.isEmpty()) {
                        showErrorPrompt(ErrorType.BlankEmailOrPassword);
                    } else if (!Utils.isEmailValid(username)) {
                        showErrorPrompt(ErrorType.incorrectEmailFormat);
                    } else {
                        TactNetworking.callLogin(TactLoginActivity.this, username, password);
                        showProgress();
                    }
                }
            }
        });

        TextView txtForgotPassword = (TextView) findViewById(R.id.forgot_password);
        txtForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.resetPassword(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (TactNetworking.checkInternetConnection(true)) {
                            String resetEmail = dialog.getResetEmail();
                            if (Utils.isEmailValid(resetEmail)) {
                                dialog.showProcessCallback();
                                TactNetworking.callResetPassword(TactLoginActivity.this, resetEmail);
                            }
                        }
                    }
                });
            }
        });

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    public void onBackPressed(){
        startActivity(new Intent(this, LandingActivity.class));
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        edtUsername = null;
        edtPassword = null;
        txtError = null;
        dialog = null;
        username = null;
        password = null;
        TactApplication.unbindDrawables(findViewById(R.id.login_layout));
        System.gc();
    }

}
