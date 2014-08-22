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

import android.app.*;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import com.google.inject.Inject;
import com.jug6ernaut.debugdrawer.DebugDrawer;
import com.jug6ernaut.debugdrawer.views.DebugGroup;
import com.jug6ernaut.debugdrawer.views.SpinnerElement;
import com.jug6ernaut.debugdrawer.views.TextElement;
import io.divide.client.android.AndroidBackend;
import io.divide.client.android.mock.AndroidDebugConfig.ModuleType;
import io.divide.shared.logging.Logger;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static io.divide.client.android.mock.AndroidDebugConfig.ModuleType.*;

public class DivideDrawer {

    public static void attach(Activity activity, int contentView) {
        DrawerHolder dh = new DrawerHolder(activity);
        dh.attach(contentView);
    }

    public static void attach(Activity activity, View contentView){
        DrawerHolder dh = new DrawerHolder(activity);
        dh.attach(contentView);
    }

    private static class DrawerHolder extends DebugDrawer {
        private static Logger logger = Logger.getLogger(DrawerHolder.class);
        @Inject
        private AndroidDebugConfig config;
        private SpinnerElement spinner;
        private TextElement url;
        List<String> endpointList = Arrays.asList(Prod.name(),Dev.name(),Mock.name());
        List<String> urlList;

        @Inject
        public DrawerHolder(final Activity activity) {
            super(activity,false);
            AndroidBackend.inject(this);

            urlList = Arrays.asList(
                    config.getProdUrl(),
                    config.getDevUrl(),
                    "Mock");


            DebugGroup endpoints = new DebugGroup("Network",activity);
            url = new TextElement(activity,"URL: ");
            spinner = new SpinnerElement(
                    activity,
                    "Endpoints",
                    endpointList.toArray(new String[endpointList.size()])) {
                @Override
                public void onAction(String s) {
                    logger.debug("Changing endpoint: " + s);

                    showAlert(activity,s);
                }
            };
            endpoints.addElement(url);
            endpoints.addElement(spinner);
            this.addDebugGroup(endpoints);
        }

        @Override
        protected void postAttach(){
            int current = getIndex();
            url.setValue(urlList.get(current));
            url.getActionView().setTextSize(TypedValue.COMPLEX_UNIT_DIP,10);
            spinner.getActionView().setSelection(current);
        }

        private int getIndex(){
            String current = config.getCurrentModuleType();
            System.out.println("Current: " + current);
            return endpointList.indexOf(config.getCurrentModuleType());
        }

        public void showAlert(final Activity activity,final String endpoint){
            final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("WARNING");
            builder.setMessage("Data will be cleared and application restarted. Continue?");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    deleteCache(activity);
                    deleteData(activity);
                    deletePrefs(activity);

                    config.setModuleType(ModuleType.valueOf(endpoint));
//                    AndroidBackend.init(config);

//                    url.setValue(urlList.get(getIndex()));

//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                    activity.startActivity(intent);

                    activity.getApplication().registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {

                        @Override public void onActivityCreated(Activity activity, Bundle bundle) { }
                        @Override public void onActivityStarted(Activity activity) { }
                        @Override public void onActivityResumed(Activity activity) { }
                        @Override public void onActivityPaused(Activity activity) { }
                        @Override public void onActivityStopped(Activity activity) { }
                        @Override public void onActivitySaveInstanceState(Activity activity, Bundle bundle) { }

                        @Override
                        public void onActivityDestroyed(Activity activity) {
                            Intent intent = new Intent(activity, activity.getClass());
                            PendingIntent pi = PendingIntent.getActivity(activity, 0, intent, 0);
                            AlarmManager am=(AlarmManager)activity.getSystemService(Context.ALARM_SERVICE);
                            am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + ( 500 ), pi); // Millisec * Second * Minute

                            System.exit(0);
                        }
                    });
                    activity.finish();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            builder.show();
        }

    }

    public static void deletePrefs(Context context){
        try {
            File parent = context.getFilesDir().getParentFile();
            File dir = new File( parent.getPath() + File.separatorChar + "shared_prefs" );
            if (dir != null && dir.isDirectory()) {
                deleteDir(dir);
            }
        } catch (Exception e) {e.printStackTrace();}
    }

    public static void deleteData(Context context) {
        try {
            File dir = context.getFilesDir();
            if (dir != null && dir.isDirectory()) {
                deleteDir(dir);
            }
        } catch (Exception e) {e.printStackTrace();}
    }

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            if (dir != null && dir.isDirectory()) {
                deleteDir(dir);
            }
        } catch (Exception e) {e.printStackTrace();}
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String aChildren : children) {
                boolean success = deleteDir(new File(dir, aChildren));
                if (!success) {
                    return false;
                }
            }
        }
        System.out.println("Deleting: " + dir.getPath());
        return dir.delete();
    }

}
