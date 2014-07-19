package io.divide.client.android.mock;

import android.app.Activity;
import android.util.TypedValue;
import android.view.View;
import com.google.inject.Inject;
import com.jug6ernaut.debugdrawer.DebugDrawer;
import com.jug6ernaut.debugdrawer.views.DebugGroup;
import com.jug6ernaut.debugdrawer.views.SpinnerElement;
import com.jug6ernaut.debugdrawer.views.TextElement;
import io.divide.client.android.AndroidBackend;
import io.divide.shared.logging.Logger;

import java.util.Arrays;
import java.util.List;

import static io.divide.client.android.mock.AndroidDebugConfig.ModuleType;
import static io.divide.client.android.mock.AndroidDebugConfig.ModuleType.*;

/**
 * Created by williamwebb on 7/13/14.
 */
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

                    config.setModuleType(ModuleType.valueOf(s));
                    AndroidBackend.init(config);

                    url.setValue(urlList.get(getIndex()));
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

    }

}
