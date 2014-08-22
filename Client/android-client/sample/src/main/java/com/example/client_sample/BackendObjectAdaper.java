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
import io.divide.client.BackendObject;

import java.util.List;

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
