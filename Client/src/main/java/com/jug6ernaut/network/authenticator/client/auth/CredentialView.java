package com.jug6ernaut.network.authenticator.client.auth;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.jug6ernaut.android.utilites.DisplayUtils;
import com.jug6ernaut.network.authenticator.client.BackendUser;

/**
 * Created by williamwebb on 12/31/13.
 */
public class CredentialView extends RelativeLayout {

    Context context;

    boolean createAccount = false;

    EditText emailField;
    EditText passwordField;
    Button submitButton;
    Button toggleButton;
    TextView titleView;
    EditText userNameField;

    public CredentialView(Context context, String title) {
        super(context);

        this.context = context;
        this.setBackgroundColor(Color.BLACK);

        emailField = new EditText(context);    emailField.setId(1);
        passwordField = new EditText(context); passwordField.setId(2);
        submitButton = new Button(context);    submitButton.setId(3);
        toggleButton = new Button(context);    toggleButton.setId(4);
        titleView = new TextView(context);     titleView.setId(5);
        userNameField = new EditText(context); userNameField.setId(6);

        // email setup
        int padding = DisplayUtils.convertDpToPixel(10, context);
        emailField.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        emailField.setPadding(padding, padding, padding, padding);
        emailField.setHint("email...");
        LayoutParams lp1 = new LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp1.addRule(ABOVE, passwordField.getId());
        lp1.addRule(ALIGN_PARENT_RIGHT);
        emailField.setLayoutParams(lp1);

        // password setup
        passwordField.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        passwordField.setPadding(padding, padding, padding, padding);
        passwordField.setHint("password...");
        LayoutParams lp2 = new LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp2.addRule(CENTER_VERTICAL);
        lp2.addRule(ALIGN_PARENT_RIGHT);
        passwordField.setLayoutParams(lp2);

        // login button setup
        submitButton.setPadding(padding,padding,padding,padding);
        submitButton.setText("Submit");
        LayoutParams lp3 = new LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp3.addRule(RIGHT_OF, passwordField.getId());
        lp3.addRule(BELOW,passwordField.getId());
        lp3.addRule(ALIGN_PARENT_LEFT);
        submitButton.setLayoutParams(lp3);

        // sign up button setup
        toggleButton.setPadding(padding,padding,padding,padding);
        toggleButton.setGravity(Gravity.CENTER);
        toggleButton.setText("Have an Account?");
        toggleButton.setBackgroundColor(Color.TRANSPARENT);
        LayoutParams lp4 = new LayoutParams(LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp4.addRule(ALIGN_PARENT_BOTTOM);
        lp4.addRule(CENTER_HORIZONTAL);
        toggleButton.setLayoutParams(lp4);

        // password setup
        userNameField.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        userNameField.setPadding(padding, padding, padding, padding);
        userNameField.setHint("username...");
        userNameField.setVisibility(View.INVISIBLE);
        LayoutParams lp5 = new LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp5.addRule(ABOVE,emailField.getId());
        lp5.addRule(ALIGN_PARENT_RIGHT);
        userNameField.setLayoutParams(lp5);

        titleView.setTextAppearance(context, android.R.attr.textAppearanceLarge);
        titleView.setText(title);
        LayoutParams lp6 = new LayoutParams(LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp6.addRule(ABOVE,emailField.getId());
        lp6.addRule(CENTER_HORIZONTAL);
        titleView.setLayoutParams(lp6);
        titleView.setPadding(0,0,0,DisplayUtils.convertDpToPixel(80, context));

        addView( emailField );
        addView( passwordField );
        addView( submitButton );
        addView( toggleButton );
        addView( titleView );
        addView( userNameField );

        submitButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = userNameField.getText().toString();
                String email = emailField.getText().toString();
                String password = passwordField.getText().toString();

                if(!createAccount){
                    handleLoginAccount(email,password);
                } else {
                    handleCreateAccount(username,email,password);
                }
            }
        });

        toggleButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userNameField.getVisibility() == View.INVISIBLE){
                    userNameField.setVisibility(View.VISIBLE);
                    userNameField.requestFocus();
                    toggleButton.setText("Create Account");
                    createAccount = true;
                } else {
                    userNameField.setVisibility(View.INVISIBLE);
                    toggleButton.setText("Have an Account?");
                    createAccount = false;
                }
            }
        });
    }

    private void handleLoginAccount(final String email, final String password){
        new AsyncTask<String, Void, SignInResponse>() {

            @Override
            protected SignInResponse doInBackground(String... params) {

                return BackendUser.signIn(email, password);
            }

            @Override
            protected void onPostExecute(SignInResponse response) {
                if(response.getStatus().isSuccess()){
                    Toast.makeText(context, "Sign In Success", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context,"Sign In Failed: " + response.getError(),Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
    }

    private void handleCreateAccount(final String username, final String email, final String password){
        new AsyncTask<String, Void, SignUpResponse>() {

            @Override
            protected SignUpResponse doInBackground(String... params) {

                return BackendUser.signUp(username,email,password);
            }

            @Override
            protected void onPostExecute(SignUpResponse response) {
                if(response.getStatus().isSuccess()){
                    Toast.makeText(context,"Sign Up Success",Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(context,"Sign Up Failed: " + response.getError(),Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
    }
}