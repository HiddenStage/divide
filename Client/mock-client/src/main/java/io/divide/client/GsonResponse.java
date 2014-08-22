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

package io.divide.client;

import com.google.gson.Gson;
import io.divide.shared.util.IOUtils;
import retrofit.client.Header;
import retrofit.client.Response;
import retrofit.mime.TypedInput;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class GsonResponse{

    static Gson gson = new Gson();

    private final String url;
    private final int status;
    private final String reason;
    private final List<Header> headers;
    private String json;
    private InputStream is;
    private int length;

    public GsonResponse(String url, int status, String reason, List<Header> headers, Object o) {
        this.url = url;
        this.status = status;
        this.reason = reason;
        this.headers = headers;
        this.json = gson.toJson(o);

        try {
            is = IOUtils.toInputStream(json, "UTF-8");
            length = json.length();
        } catch (Exception e) {
            is = null;
            length = 0;
        }
    }

    public Response build(){
        return new Response(url,status,reason,new ArrayList<Header>(),new TypedInput() {
            @Override
            public String mimeType() {
                return null;
            }

            @Override
            public long length() {
                return length;
            }

            @Override
            public InputStream in() throws IOException {
                return is;
            }
        });
    }
}
