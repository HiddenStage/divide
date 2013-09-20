package com.example.client_sample;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.Views;
import com.jug6ernaut.android.logging.Logger;
import com.jug6ernaut.network.authenticator.client.BackendObject;
import com.jug6ernaut.network.shared.web.transitory.Credentials;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: williamwebb
 * Date: 9/6/13
 * Time: 3:42 PM
 */
public class UserAdapter extends BaseAdapter {

    Logger logger = Logger.getLogger(UserAdapter.class);

    LayoutInflater inflater;
    List<BackendObject> users;

    public UserAdapter(Context context, List<BackendObject> users){
        this.inflater = LayoutInflater.from(context);
        this.users = users;
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int i) {
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

        holder.name.setText("Username: " + user.get(String.class, Credentials.USERNAME_KEY));
        holder.id.setText("UserId: " + user.getUserId());

        return convertView;
    }

    static class ViewHolder {
        @InjectView(R.id.name)
        TextView name;
        @InjectView(R.id.id)
        TextView id;

        public ViewHolder(View view) {
            Views.inject(this, view);
        }
    }
}
