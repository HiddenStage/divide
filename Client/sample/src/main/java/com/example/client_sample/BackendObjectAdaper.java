package com.example.client_sample;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.jug6ernaut.android.logging.Logger;
import com.jug6ernaut.network.authenticator.client.BackendObject;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: williamwebb
 * Date: 9/6/13
 * Time: 3:42 PM
 */
public class BackendObjectAdaper extends BaseAdapter {

    Logger logger = Logger.getLogger(BackendObjectAdaper.class);

    LayoutInflater inflater;
    List<BackendObject> users;

    public BackendObjectAdaper(Context context, List<BackendObject> users){
        this.inflater = LayoutInflater.from(context);
        this.users = users;
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public BackendObject getItem(int i) {
        return users.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BackendObject user = users.get(position);

        ViewHolder holder;
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = inflater.inflate(R.layout.creds_row, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }

        holder.name.setText("Key: " + user.getObjectKey());
        holder.id.setText("Owner: " + user.getOwnerId());

        return convertView;
    }

    static class ViewHolder {
        @InjectView(R.id.name)
        TextView name;
        @InjectView(R.id.id)
        TextView id;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
