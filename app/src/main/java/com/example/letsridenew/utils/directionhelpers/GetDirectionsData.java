package com.example.letsridenew.utils.directionhelpers;

import android.content.Context;
import android.os.AsyncTask;


import com.example.letsridenew.utils.interfaces.DiractionCallback;

import java.io.IOException;
import java.util.HashMap;

/**
 * @auth Priyanka
 */

public class GetDirectionsData extends AsyncTask<Object, String, String> {

    String url;
    String googleDirectionsData;
    String duration;
    String distance;
    DiractionCallback callback;
    Context c;

    @Override
    protected String doInBackground(Object... objects) {
        url = (String)objects[0];
        callback = (DiractionCallback) objects[1];

        try {
            googleDirectionsData = new FetchURL(c).downloadUrl(url);
            System.out.println("[doInBackgroundInner]"+duration+"[doInBackgroundInner]");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("[doInBackgroundOuter]"+duration+"[doInBackgroundOuter]");

        return googleDirectionsData;
    }

    @Override
    protected void onPostExecute(String s) {
        HashMap<String, String> directionList = null;
        DataParser parser = new DataParser();
        directionList = parser.parseDirections(s);
        duration = directionList.get("duration");
        distance = directionList.get("distance");
        System.out.println("[onPostExecute]"+duration+"[onPostExecute]");
        callback.onCallback(duration,distance);

    }
}

