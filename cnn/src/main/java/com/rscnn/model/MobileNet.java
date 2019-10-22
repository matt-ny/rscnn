package com.rscnn.model;

import android.renderscript.RenderScript;

import com.rscnn.network.ConvNet;
import com.rscnn.postprocess.ClassifierPostProcess;
import com.rscnn.postprocess.PostProcess;
import com.rscnn.preprocess.PreProcess;

import java.io.IOException;

public class MobileNet extends ObjectDetector {
    public MobileNet(RenderScript renderScript, String modelDir) throws IOException {
        float[] meanValue = new float[]{106.0f,118.0f,123.0f};
        PreProcess preProcess = new PreProcess(meanValue, 1.0f);
        PostProcess postProcess = new ClassifierPostProcess();
        this.convNet = new ConvNet(renderScript, null, modelDir, preProcess, postProcess);
    }
}
