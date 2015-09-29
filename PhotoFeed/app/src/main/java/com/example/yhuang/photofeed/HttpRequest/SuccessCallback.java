package com.example.yhuang.photofeed.HttpRequest;

import org.json.JSONObject;

/**
 * Created by yhuang on 9/17/2015.
 */
public interface SuccessCallback { // creates SuccessCallback class which has a function to be called later
    void callback(boolean success, JSONObject response);
}
