/*
 * Copyright (C) 2014 Divide.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
