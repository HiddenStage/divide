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

package io.divide.client.android.mock;

import android.content.Context;
import android.os.Bundle;
import io.divide.client.BackendServices;
import io.divide.client.BackendUser;
import io.divide.client.android.AuthActivity;
import io.divide.client.android.CredentialView;
import io.divide.client.auth.LoginListener;
import io.divide.shared.logging.Logger;

public class DebugAuthActivity extends AuthActivity {
    Logger logger = Logger.getLogger(AuthActivity.class);

    public static final String TITLE_EXTRA = "title_extra_key";

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String title = this.getIntent().getStringExtra(TITLE_EXTRA);
        if(title == null) title = getApplicationName(this);
        CredentialView credentialView = new CredentialView(this,title);
        DivideDrawer.attach(this,credentialView);
        BackendServices.addLoginListener(new LoginListener() {
            @Override
            public void onNext(BackendUser backendUser) {
                if (backendUser != null) {
                    DebugAuthActivity.this.finish();
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
