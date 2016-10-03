package com.shivisuper.alachat_mobile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import android.util.Log;

public class ResTClient {
    private static final String TAG = "ResTClient";
    private static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    private OkHttpClient httpclient = new OkHttpClient();

    // Prepare a request object
    void run(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = httpclient.newCall(request).execute()) {
            Log.d(TAG, response.body().string());
        }
    }

    // Prepare a response object
    void post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try (Response response = httpclient.newCall(request).execute()) {
            Log.d(TAG, response.body().string());
        }
    }
}
