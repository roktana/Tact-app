package com.tactile.tact.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tactile.tact.R;
import com.tactile.tact.services.network.TactNetworking;

/**
 * Created by sebafonseca on 1/15/15.
 */
public class ForgotPasswordActivity extends TactBaseActivity  {

    EditText textPassword;
    EditText textConfirmPassword;
    TextView loginError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.w("FORGOT PWD", getIntent().getDataString());
        setContentView(R.layout.forgot_password_layout);

        LinearLayout actionBar = (LinearLayout) findViewById(R.id.forgot_password_action_bar);
        actionBar.setBackgroundColor(getResources().getColor(R.color.action_bar_grey));
        actionBar.findViewById(R.id.fragment_menu_button).setVisibility(View.GONE);
        ((TextView) actionBar.findViewById(R.id.fragment_actionBar_title_text)).setText(getResources().getString(R.string.forgot_password));
        ((TextView) actionBar.findViewById(R.id.fragment_actionBar_title_text)).setTextColor(getResources().getColor(R.color.action_bar_orange));

        textPassword                = (EditText) findViewById(R.id.textPassword);
        textConfirmPassword         = (EditText) findViewById(R.id.textConfirmPassword);
        loginError                  = (TextView) findViewById(R.id.loginError);
        Button setPasswordButton    = (Button) findViewById(R.id.buttonSetPassword);
        setPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validatePassword();
            }
        });
    }

    private void validatePassword(){
        if (TactNetworking.checkInternetConnection(true)){
            if (passwordControl()) {
                //TODO: update password
                Log.w("FORGOT PWD", "success!!");
            } else {
                if (textPassword.getText().toString().length() < 6)
                {
                    loginError.setText(getResources().getString(R.string.password_length_error));
                }
                else if (textPassword.getText().toString().contains(" "))
                {
                    loginError.setText(getResources().getString(R.string.password_blank_error));
                } else
                {
                    loginError.setText(getResources().getString(R.string.not_match_passwords));
                }
                loginError.setVisibility(View.VISIBLE);
            }
        }
    }

    private boolean passwordControl() {
        String password = textPassword.getText().toString();
        String repeatPassword = textConfirmPassword.getText().toString();

        return password.length()>5 && password.equals(repeatPassword) && !password.contains(" ");
    }
}
