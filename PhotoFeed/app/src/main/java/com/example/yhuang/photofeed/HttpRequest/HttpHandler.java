package com.example.yhuang.photofeed.HttpRequest;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

/**
 * Created by yhuang on 9/17/2015.
 */
public class HttpHandler {

    public RequestQueue queue;

    public HttpHandler(Context context) {
        queue = Volley.newRequestQueue(context);
    }

    public void searchWithCallback(String searchQuery, final SuccessCallback callback) {
        String query =searchQuery.replaceAll(" ", "+");
        final String key = "AIzaSyD5I9oSv9hw5CLfnZLy-bTOHMpJpsrRxwI";
        final String cx = "004900652497093970773:3gxd_h1qem0";
        final String searchType = "image";


        String url = "https://www.googleapis.com/customsearch/v1?";
        url += "key=" + key;
        url += "&cx=" + cx;
        url += "&q=" + query;
        url += "&searchType=" + searchType;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                new JSONObject(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        callback.callback(true, response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError err) {
                        Log.e("Error: ", err.getMessage());
                    }
                }
        );

        queue.add(request);
    }



}
