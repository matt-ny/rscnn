package com.rscnn.preprocess;

import java.util.Arrays;

import android.graphics.Bitmap;
import android.util.Log;

import com.rscnn.postprocess.NetworkParameter;

public class PreProcess {
    protected ROI roi = null;
    protected float[] meanValueBGR = new float[]{127.5f, 127.5f, 127.5f};
    protected float scale1 = 0.007843f;
    protected float scale = 0.007843f;

    public PreProcess() {
    }

    public PreProcess(float[] meanValueBGR, float scale) {
        this.meanValueBGR = meanValueBGR;
        this.scale = scale;
    }

    public PreProcess(ROI roi, float[] meanValueBGR, float scale) {
        this.roi = roi;
        this.meanValueBGR = meanValueBGR;
        this.scale = scale;
    }

    public Object[] process(Bitmap image, NetworkParameter param) {
        float pixelMeanBlue = meanValueBGR[0];
        float pixelMeanGreen = meanValueBGR[1];
        float pixelMeanRed = meanValueBGR[2];

        int height = param.getNetworkInputHeight();
        Log.d("network-input-height = ", String.valueOf(height));

        int width = param.getNetworkInputWidth();
        Log.d("network-input-width = ", String.valueOf(width));

        float[] data = new float[height * width * 4];
        int[] pixels = new int[height * width];

        Bitmap subImage;
        if (roi !=null && (roi.getX() != 0 || roi.getY() !=0 || roi.getHeight() != image.getHeight()
                || roi.getWidth() != image.getWidth())) {
            subImage = Bitmap.createBitmap(image, roi.getX(), roi.getY(),
                    roi.getWidth(), roi.getHeight());
        }
        else {
            subImage = image;
            Log.d("sub-image-height/width",String.valueOf(subImage.getWidth())+" "+String.valueOf(subImage.getHeight()));
        }

        if(subImage.getWidth()!= width || subImage.getHeight() != height){
            Bitmap bmp2 = Bitmap.createScaledBitmap(subImage, width, height, false);
            bmp2.getPixels(pixels, 0, width, 0, 0, width, height);
            bmp2.recycle();
        }
        else{
            subImage.getPixels(pixels, 0, width, 0, 0, width, height);
        }

        int count = 0;
        int dataCount = 0;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int k = pixels[count++];
                data[dataCount++] = ((k & 0xFF) - pixelMeanBlue) * scale;
                data[dataCount++] = (((k >> 8) & 0xFF) - pixelMeanGreen) * scale;
                data[dataCount++] = (((k >> 16) & 0xFF) - pixelMeanRed) * scale;
                data[dataCount++] = 0;
                Log.d("scale = ",String.valueOf(scale));
            }
        }

        if (subImage != image) {
            subImage.recycle();
        }

        Log.d("return preprocess",Arrays.toString(data));
        return new Object[]{data};
    }
}
