package com.rscnn.model;

import android.content.res.AssetManager;
import android.renderscript.RenderScript;

import com.rscnn.network.ConvNet;
import com.rscnn.postprocess.ClassifierPostProcess;
import com.rscnn.postprocess.PostProcess;
import com.rscnn.preprocess.PreProcess;
import com.rscnn.utils.DeserializeDlaaS;

import java.io.IOException;

public class MobileNet extends ObjectDetector {
    public MobileNet(RenderScript renderScript, AssetManager assetManager, String modelDir) throws IOException {
        float[] meanValue = new float[]{106.0f,118.0f,123.0f};
        PreProcess preProcess = new PreProcess(meanValue, 1.0f);
        PostProcess postProcess = new ClassifierPostProcess();

        // dynamically load the classes from Dlaas file
        DeserializeDlaaS dlaas = new DeserializeDlaaS();
        String[] labelz = dlaas.ReturnLabels(assetManager,modelDir);
        postProcess.setLabels(labelz);

        this.convNet = new ConvNet(renderScript, assetManager, modelDir, preProcess, postProcess);
    }
}
