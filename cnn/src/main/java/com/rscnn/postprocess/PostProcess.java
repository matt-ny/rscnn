package com.rscnn.postprocess;

import android.graphics.Bitmap;

import com.rscnn.network.DetectResult;

import java.util.List;
import java.util.Map;

public abstract class PostProcess {

    protected String[] labels = new String[]{
      "Background","Default1","Default2","Default3"
    };

    public abstract List<DetectResult> process(Bitmap image, NetworkParameter param, Map<String, Object> output);

    public void setLabels(String[] labels) {
        this.labels = labels;
    }
}
