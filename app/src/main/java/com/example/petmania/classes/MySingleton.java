package com.example.petmania.classes;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

public class MySingleton {
    private static MySingleton mInstance;
    private RequestQueue requestQueue;
    private static Context context;

    public MySingleton(Context context) {
        this.requestQueue = requestQueue;
        this.context =context;
    }

    private RequestQueue getRequestQueue(){
        if (requestQueue==null){
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }
    public static synchronized MySingleton getInstance(Context context){
        if (mInstance==null){
            mInstance= new MySingleton(context);
        }
        return mInstance;
    }

    public<T> void addToRequest(Request<T> request){
        getRequestQueue().add(request);
    }
}
