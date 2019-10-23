package com.rscnn.utils;

import org.json.JSONException;
import org.json.*;
import android.content.res.AssetManager;

import java.util.ArrayList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.util.Log;


public class DeserializeDlaaS {

    public JSONObject ReadDlaaS(AssetManager assetMan, String modelPath) {

        String dlaasName = null;
        JSONObject dlaasD = null;

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

            BufferedReader streamReader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
            StringBuilder responseStrBuilder = new StringBuilder();

            String inputStr;
            while ((inputStr = streamReader.readLine()) != null) {
                responseStrBuilder.append(inputStr);
            }

            try {
                dlaasD = new JSONObject(responseStrBuilder.toString());
            } catch (JSONException j) {
                j.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return dlaasD;
    }


    public String ReturnModelType(AssetManager assetMan, String modelPath) {

        // fetch the descriptor
        JSONObject dlaasD = ReadDlaaS(assetMan, modelPath);
        String modelType = null;

        try {
            modelType = dlaasD.getString("model_type");
        } catch (JSONException j) {
            j.printStackTrace();
        }

        return modelType;
    }

    public String ReturnModelName(AssetManager assetMan, String modelPath) {

        // fetch dlaas descriptor
        JSONObject dlaasD = ReadDlaaS(assetMan, modelPath);
        String modelName = null;

        // first check if classifier id is set (default)
        try {
            modelName = dlaasD.getString("classifier_id");
        } catch (JSONException j) {
            j.printStackTrace();
        }

        // check for name if classifier_id was null
        if (modelName == null) {
            try {
                modelName = dlaasD.getString("name");
            } catch (JSONException j) {
                j.printStackTrace();
            }
        }
        return modelName;
    }

    public String[] ReturnLabels(AssetManager assetMan, String modelPath) {

        ArrayList<String> dLabels = new ArrayList<String>();
        String dlaasName = null;
        String[] labelsList = new String[]{};

        JSONObject dlaasD = ReadDlaaS(assetMan, modelPath);

        try {
            JSONArray layers = dlaasD.getJSONArray("class_to_vector");


            // iteratively append class labels to labelList
            for (int i = 0; i < layers.length(); i++) {
                JSONObject layer = layers.getJSONObject(i);
                String className = layer.getString("class");
                dLabels.add(className);
            }

        } catch (JSONException j) {
            j.printStackTrace();
        }

        // convert the adjustable array list back to fixed size string array
        String[] rLabels = new String[dLabels.size()];
        rLabels = dLabels.toArray(rLabels);

        return rLabels;
    }
}




