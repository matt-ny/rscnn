package com.rscnn.utils;

import org.json.JSONException;
import org.json.*;
import android.content.res.AssetManager;

import java.util.ArrayList;
import java.util.Arrays;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class DeserializeDlaaS {

    ArrayList<String> dLabels = new ArrayList<String>();

    public String[] ReturnLabels (AssetManager assetMan, String modelPath) {

        String dlaasName = null;
        String[] labelsList = new String[]{};

        try {
            // read asset manager for file list
            String[] fileList = assetMan.list(modelPath);

            // if file ends with '.json' ASSUME DlaaS...
            for (String f : fileList) {
                if (f.endsWith(".json")) {
                    dlaasName = f;
                }
            }

            //initiate input stream from model path + dlaasName
            InputStream input = assetMan.open(modelPath + "/" + dlaasName);
            Log.d("dlaasPath", modelPath + dlaasName);

            BufferedReader streamReader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
            StringBuilder responseStrBuilder = new StringBuilder();

            String inputStr;
            while ((inputStr = streamReader.readLine()) != null) {
                responseStrBuilder.append(inputStr);
            }

                try {
                JSONObject dlaas = new JSONObject(responseStrBuilder.toString());
                JSONArray layers = dlaas.getJSONArray("class_to_vector");


                // iteratively append class labels to labelList
                for (int i=0; i < layers.length(); i++ ) {
                    JSONObject layer = layers.getJSONObject(i);
                    String className = layer.getString("class");
                    dLabels.add(className);
                }


                Log.d("dlaas", dlaas.toString());
                Log.d("layersarray",layers.toString());




                }
                catch(JSONException j){
                    j.printStackTrace();
                }

            }

            catch (IOException e) {
            Log.d("dlaas-reader-exception!", e.toString());
            e.printStackTrace();

        }

        // convert the adjustable array list back to fixed size string array
        String[] rLabels = new String[dLabels.size()];
        rLabels = dLabels.toArray(rLabels);
        Log.d("rLabels",Arrays.toString(rLabels));


        return rLabels;
    }
}

