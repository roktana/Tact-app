package com.tactile.tact.utils;

import android.content.Context;
import android.os.AsyncTask;

import com.tactile.tact.consts.TactConst;
import com.tactile.tact.database.TactDataSource;
import com.tactile.tact.services.events.EventAddSourceError;
import com.tactile.tact.services.events.EventAddSourceSuccess;
import com.tactile.tact.services.network.TactNetworking;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import de.greenrobot.event.EventBus;

/**
 * Created by Sebastian Muñoz on 8/6/14.
 */
public class ExchangeServiceConnection extends AsyncTask<String,Void, Void> {

    private String server;
    private String domain;
    private String email;
    private String username;
    private String password;
    private Boolean skipssl;
    private Context context;

    public ExchangeServiceConnection(Context context, String server, String domain, String email, String username, String password, Boolean skipssl){
        this.context = context;
        this.server = server;
        this.domain = domain;
        this.email = email;
        this.username = username;
        this.password = password;
        this.skipssl = skipssl;
    }

    @Override
    protected Void doInBackground(String...params){
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(TactNetworking.getURL() + TactConst.END_POINT_SOURCES);
        try {

            // Add headers
            HashMap<String, String> headers = TactNetworking.getMapAuthorizationHeaders(context);
            for (String key : headers.keySet()) {
                httppost.addHeader(key, headers.get(key));
            }

            // Add data
            JSONObject obj = TactDataSource.getAddSourceRequest("exchange", server, domain, email, username, password, skipssl);
            StringEntity entity = new StringEntity(obj.toString(), HTTP.UTF_8);
            entity.setContentType("application/json");
            httppost.setEntity(entity);

            BasicCookieStore cookieStore = new BasicCookieStore();
            HttpContext httpContext = new BasicHttpContext();
            httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost, httpContext);

            Log.w("response code", Integer.toString(response.getStatusLine().getStatusCode()));
            if (response.getStatusLine().getStatusCode() / 100 == 2){
                String jsonString = Utils.getEntityAsString(response.getEntity());
                try{
                    JSONArray jsonArray = new JSONArray(jsonString);
                    EventBus.getDefault().postSticky(new EventAddSourceSuccess(jsonArray));
                }
                catch (Exception e){
                    EventBus.getDefault().postSticky(new EventAddSourceError(false, response.getStatusLine().getStatusCode(), ""));
                }
            }
            else {
                EventBus.getDefault().postSticky(new EventAddSourceError(false, response.getStatusLine().getStatusCode(), ""));
            }


        } catch (ClientProtocolException e) {
            EventBus.getDefault().postSticky(new EventAddSourceError(false, 0, e.getMessage()));
        } catch (IOException e) {
            EventBus.getDefault().postSticky(new EventAddSourceError(false, 0, e.getMessage()));
        }
        return null;
    }
}
