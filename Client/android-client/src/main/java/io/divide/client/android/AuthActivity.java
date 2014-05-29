package io.divide.client.android;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import io.divide.client.BackendServices;
import io.divide.client.BackendUser;
import io.divide.client.auth.LoginListener;
import io.divide.shared.logging.Logger;

/**
 * The AuthUtils activity.
 *
 * Called by the AuthUtils and in charge of identifing the user.
 *
 * It sends back to the AuthUtils the result.
 */
public class AuthActivity extends Activity {
    Logger logger = Logger.getLogger(AuthActivity.class);

    public static final String TITLE_EXTRA = "title_extra_key";

    MockToggle mockToggle;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String title = this.getIntent().getStringExtra(TITLE_EXTRA);
        if(title == null) title = getApplicationName(this);
        setContentView(new CredentialView(this,title));
        BackendServices.addLoginListener(new LoginListener() {
            @Override
            public void onNext(BackendUser backendUser) {
                if(backendUser != null){
                    AuthActivity.this.finish();
                }
            }
        });

        this.mockToggle = new MockToggle(this);
        this.closeOptionsMenu();
    }

    private static String getApplicationName(Context context) {
        int stringId = context.getApplicationInfo().labelRes;
        return context.getString(stringId);
    }

    MenuHandler menuHandler;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menuHandler = new MenuHandler(menu);

//        menuHandler.addTopLevelOperation(0, android.R.drawable.ic_menu_add, addAppAction);

        MenuHandler.TopLevelMenu menuButton = menuHandler.addTopLevelMenu(1, "");
        menuButton.addItem(mockToggle.getText(),0, mockToggle);

        return super.onCreateOptionsMenu(menu);
    }

    private class MockToggle implements MenuHandler.MenuOperation {

        private final SharedPreferences storage;
        private final Context context;

        public MockToggle(Context context){
            this.storage = context.getSharedPreferences("mock",Context.MODE_PRIVATE);
            this.context = context;
        }

        private boolean isMockEnabled(){
            return storage.getBoolean("mock",false);
        }

        private void setMockEnabled(boolean enabled){
            storage.edit().putBoolean("mock",enabled).commit();
        }

        public String getText(){
            return "Mock: " + (isMockEnabled()?"Enabled":"Disabled");
        }

        @Override
        public boolean operation(MenuItem item) {
            setMockEnabled(!isMockEnabled());

//            AlertDialog.Builder builder = new AlertDialog.Builder(context);
//            builder.setMessage("Will now restart.")
//                    .setCancelable(false)
//                    .setOnKeyListener(new DialogInterface.OnKeyListener() {
//                        @Override
//                        public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
//                            Intent mStartActivity = new Intent(context, StartActivity.class);
//                            int mPendingIntentId = 123456;
//                            PendingIntent mPendingIntent = PendingIntent.getActivity(context, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
//                            AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//                            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
//                            System.exit(0);
//                            return false;
//                        }
//                    })
//                    .show();

            return false;
        }
    };

}
