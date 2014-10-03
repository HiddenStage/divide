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

package io.divide.client.android.security;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import io.divide.client.BackendUser;
import rx.Observable;
import rx.exceptions.OnErrorThrowable;
import rx.functions.Func1;

public class UserUtils {
    private static final String ANONYMOUS_KEY = "anonymous_key";

    public static Observable<BackendUser> getAnonymousUser(Context context){
        final String id = UserUtils.getDeviceIdUnique(context);

        return BackendUser.signInInBackground(id,id).onErrorFlatMap(new Func1<OnErrorThrowable, Observable<? extends BackendUser>>() {
            @Override
            public Observable<? extends BackendUser> call(OnErrorThrowable onErrorThrowable) {
                return BackendUser.signUpInBackground(id,id,id);
            }
        }).map(new Func1<BackendUser, BackendUser>() {
            @Override
            public BackendUser call(BackendUser user) {
                user.put(ANONYMOUS_KEY,true);
                user.saveASync();
                return user;
            }
        });
    }

    public static String getDeviceIdUnique(Context context)
    {
        try {
            String a = getDeviceIdTm(context);
            String b = getDeviceIdAndroid(context);
            String c = getDeviceIdPseudo();

            if (a!=null && a.length()>0 && a.replace("0", "").length()>0)
                return a;
            else if (b!=null && b.length()>0 && b.equals("9774d56d682e549c")==false)
                return b;
            else if (c!=null && c.length()>0)
                return c;
            else
                return "";
        }
        catch(Exception ex)
        {
            return "";
        }
    }

    private static String getDeviceIdTm(Context context)
    {
        TelephonyManager tm=(TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getDeviceId();
    }

    private static String getDeviceIdAndroid(Context context)
    {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    private static String getDeviceIdPseudo()
    {
        String tstr="";
        if ( Build.VERSION.SDK_INT == Build.VERSION_CODES.FROYO) {
            tstr+= Build.SERIAL;
            tstr += "::" + (Build.PRODUCT.length() % 10) + (Build.BOARD.length() % 10) + (Build.BRAND.length() % 10) + (Build.CPU_ABI.length() % 10) + (Build.DEVICE.length() % 10) + (Build.MANUFACTURER.length() % 10) + (Build.MODEL.length() % 10);
        }
        if(tstr.equals(""))tstr = "blah";

        return tstr;
    }
}
