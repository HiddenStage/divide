/*
 * Copyright (C) 2014 Divide.io
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.divide.client.android;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import io.divide.client.BackendServices;
import io.divide.client.BackendUser;
import io.divide.client.auth.LoginListener;

/**
 * The AuthUtils activity.
 *
 * Called by the AuthUtils and in charge of identifying the user.
 *
 * It sends back to the AuthUtils the result.
 */

public class AuthActivity extends Activity {

    public static final String EXTRA_TITLE = "title_extra_key";
    public static final String EXTRA_ENABLE_ANONYMOUS_LOGIN = "enable_anonymous_login_key";

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String title = this.getIntent().getStringExtra(EXTRA_TITLE);
        if(title == null) title = getApplicationName(this);
        boolean enableAnonymous = this.getIntent().getBooleanExtra(EXTRA_ENABLE_ANONYMOUS_LOGIN,false);
        CredentialView credentialView = new CredentialView(this,title);
        if(enableAnonymous)credentialView.enableAnonymousLogin();

        setContentView(credentialView);
        BackendServices.addLoginListener(new LoginListener() {
            @Override
            public void onNext(BackendUser backendUser) {
            if(backendUser != null){
                AuthActivity.this.finish();
            }
            }
        });

        this.closeOptionsMenu();
    }

    private static String getApplicationName(Context context) {
        int stringId = context.getApplicationInfo().labelRes;
        return context.getString(stringId);
    }

}
