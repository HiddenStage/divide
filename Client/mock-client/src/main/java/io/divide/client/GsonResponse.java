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

/**
 * Created by williamwebb on 4/12/14.
 */
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
