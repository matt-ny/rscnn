package com.rscnn.layers;

import com.rscnn.network.FeatureMap;
import com.rscnn.network.Layer;
import com.rscnn.algorithm.NMS;
import com.rscnn.utils.LogUtil;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.util.Log;

public class DetectionOutput extends Layer {

    private int numClasses;
    private boolean shareLocation = true;
    private int backgroundLabelId = 0;
    private float nmsParamNmsThreshold = 0.2f;
    private float softNMSSigma = 0.5f;
    private int nmsParamTopK = 2;
    private float nmsParamEta = 1.0f;
    private String codeType = "CORNER";
    private boolean varianceEncodedInTarget = false;
    private int keepTopK = -1;
    private float confidenceThreshold = 0.5f;
    private boolean visualize = false;
    private float visualizeThreshold = 0.2f;
    private String saveFile;

    private int num_loc_classes;
    private int num_priors;

    public void setNumClasses(int numClasses) {
        this.numClasses = numClasses;
    }

    public void setShareLocation(boolean shareLocation) {
        this.shareLocation = shareLocation;
    }

    public void setBackgroundLabelId(int backgroundLabelId) {
        this.backgroundLabelId = backgroundLabelId;
    }

    public void setNmsParamNmsThreshold(float nmsParamNmsThreshold) {
        this.nmsParamNmsThreshold = nmsParamNmsThreshold;
    }

    public void setNmsParamTopK(int nmsParamTopK) {
        this.nmsParamTopK = nmsParamTopK;
    }

    public void setNmsParamSigma(float s) {
        this.softNMSSigma = s;
    }
    public void setNmsParamEta(float nmsParamEta) {
        this.nmsParamEta = nmsParamEta;
    }

    public void setCodeType(String codeType) {
        this.codeType = codeType;
    }

    public void setVarianceEncodedInTarget(boolean varianceEncodedInTarget) {
        this.varianceEncodedInTarget = varianceEncodedInTarget;
    }

    public void setKeepTopK(int keepTopK) {
        this.keepTopK = keepTopK;
    }

    public void setConfidenceThreshold(float confidenceThreshold) {
        this.confidenceThreshold = confidenceThreshold;
    }

    public void setVisualize(boolean visualize) {
        this.visualize = visualize;
    }

    public void setVisualizeThreshold(float visualizeThreshold) {
        this.visualizeThreshold = visualizeThreshold;
    }

    public void setSaveFile(String saveFile) {
        this.saveFile = saveFile;
    }


    private float[][] GetConfidenceScores(float[] input, int numPredsPerClass)
    {
        float[][] output = new float[numClasses][numPredsPerClass];

        int count = 0;
        for(int p = 0; p < numPredsPerClass; p++){
            for(int c = 0; c < numClasses; c++){
                output[c][p] = input[count++];
            }
        }
        return output;
    }

    private float[][][] GetLocPredictions(float[] input, int numPredsPerClass,  int numLocClasses)
    {
        float[][][] output = new float[numPredsPerClass][numLocClasses][4];

        int count = 0;
        for(int p = 0; p < numPredsPerClass; p++){
            for(int c = 0; c < numLocClasses; c++){
                output[p][c][0] = input[count++];
                output[p][c][1] = input[count++];
                output[p][c][2] = input[count++];
                output[p][c][3] = input[count++];
            }
        }
        return output;
    }

    private float[][][] GetPriorBBoxes(float[][] input, int numPredsPerClass,  int numLocClasses)
    {
        int numPriors = numPredsPerClass;
        float[][][] output = new float[2][numPriors][4];

        for(int i = 0; i < numPriors; i++){
            output[0][i][0] = input[0][i * 4];
            output[0][i][1] = input[0][i * 4 + 1];
            output[0][i][2] = input[0][i * 4 + 2];
            output[0][i][3] = input[0][i * 4 + 3];
            output[1][i][0] = input[1][i * 4];
            output[1][i][1] = input[1][i * 4 + 1];
            output[1][i][2] = input[1][i * 4 + 2];
            output[1][i][3] = input[1][i * 4 + 3];
        }
        return output;
    }

    private float[][][] decodeBBox(float[][][] location, float[][] prior, float[][] variance)
    {
        int numBboxes = prior.length;

        float[][][] output = new float[num_loc_classes][numBboxes][4];
        if(codeType.equals("CENTER_SIZE")){
            if(varianceEncodedInTarget){
                LogUtil.e("DetectionOutput", "varianceEncodedInTarget not implemented");
            }
            else{
                for (int i=0; i < num_loc_classes; i++){
                    for(int j=0; j< numBboxes; j++){
                        float xmin = prior[j][0];
                        float ymin = prior[j][1];
                        float xmax = prior[j][2];
                        float ymax = prior[j][3];
                        float bboxXmin = location[j][i][0];
                        float bboxYmin = location[j][i][1];
                        float bboxXmax = location[j][i][2];
                        float bboxYmax = location[j][i][3];

                        float prior_width = xmax - xmin;
                        float prior_height = ymax - ymin;
                        float prior_center_x = (xmin + xmax) / 2.f;
                        float prior_center_y = (ymin + ymax) / 2.f;
                        float centerX = variance[i][0] * bboxXmin * prior_width + prior_center_x;
                        float centerY = variance[i][1] * bboxYmin * prior_height + prior_center_y;
                        float width = (float) Math.exp(variance[i][2] * bboxXmax) * prior_width;
                        float height = (float) Math.exp(variance[i][3] * bboxYmax) * prior_height;

                        output[i][j][0] = centerX - width / 2;
                        output[i][j][1] = centerY - height / 2;
                        output[i][j][2] = centerX + width / 2;
                        output[i][j][3] = centerY + height / 2;
                    }
                }
            }
        }
        else{
            LogUtil.e("DetectionOutput", "code type " + codeType + " not implemented");
        }
        return output;
    }


    @Override
    public void computeFeatureMap() {
        float[] locData = ((FeatureMap) featureMapInput[0]).getData1D();
        float[][][][] input1 = ((FeatureMap)featureMapInput[1]).getData();
        float[][][][] input2 = ((FeatureMap)featureMapInput[2]).getData();
        float[] confData = input1[0][0][0];
        float[][] priorData = input2[0][0];

        float[][][] location = GetLocPredictions(locData, num_priors, num_loc_classes);
        float[][] scores = GetConfidenceScores(confData, num_priors); // 22 * 2252
        float[][][] priorAndVar = GetPriorBBoxes(priorData, num_priors, num_loc_classes);
        float[][] prior = priorAndVar[0];
        float[][] variance = priorAndVar[1];
        float[][][] boxes = decodeBBox(location, prior, variance);// 2252 * 4

        List<float[]> boxAndScore = new ArrayList<>();

        Log.d("box[0]: ",Arrays.deepToString(boxes[0]));

        for(int i=1;i<numClasses;i++){//skip the background class
            float[][] box1 = boxes[0].clone();

            long temp = System.currentTimeMillis();
            NMS.sortScores(box1,scores[i]);
            temp = System.currentTimeMillis() - temp;
            //LogUtil.i("NMS prep","sort compute time: "+name+"  " + temp + " ms.");

            Log.d("box1: ",Arrays.deepToString(box1));
            Log.d("all-scores-return", Arrays.toString(scores[i]));
            for (int k=1; k<scores[i].length; k++) {
                if (scores[i][k] > scores[i][k-1]) {
                Log.d("not-sorted-at ",k+ " "+scores[k]+" "+scores[k-1]);
                }
            }

            // sigma, nmsParamTopK & confidenceThreshold
            // can or are initially set from model proto file
            // but can be overridden with env vars
            String strSigmaUI = System.getProperty("SIGMA_ENV");
            if (null != strSigmaUI) {
                setNmsParamSigma(Float.parseFloat(strSigmaUI));
            }
            String strConfUI = System.getProperty("CTHRESH_ENV");
            if (null != strConfUI) {
                setConfidenceThreshold(Float.parseFloat(strConfUI));
            }
            //soft NMS
            int[] index = NMS.softNmsScoreFilter(box1, scores[i], nmsParamTopK, softNMSSigma,
                    confidenceThreshold);

            //hard NMS
            //int[] index = NMS.nmsScoreFilter(box1, scores[i], nmsParamTopK, .33f);

            Log.d("survival-index",Arrays.toString(index));

            Log.d("NMS Params: ","TopK: "+nmsParamTopK+" Thresh: "+nmsParamNmsThreshold
                        + "Sigma: "+softNMSSigma);
            if(index.length>0){
                for(int id:index){
                    if(scores[i][id] < confidenceThreshold) {
                        // This will not happen when softNmsScoreFilter() is used since it pre-filters
                        Log.d("scores","="+" in class: "+i+" the score: "+scores[i][id]
                                +" is less than conf-thresh: "+confidenceThreshold);
                        break;
                    }
                    if(Float.isNaN(scores[i][id])){//skip the NaN score, maybe not correct
                        continue;
                    }
                    float[] boxScore = new float[6];
                    for(int j=0;j<4;j++)
                        boxScore[j] = box1[id][j];//x1,y1,x2,y2
                    boxScore[4] = i;              //class index
                    boxScore[5] = scores[i][id];  //possibility
                    boxAndScore.add(boxScore);
                }
            }
        }
        float[][] out = new float[boxAndScore.size()][6];
        for(int i=0;i<out.length;i++){
            out[i] = boxAndScore.get(i);
        }
        featureMapOutput = out;
    }


    @Override
    public void computeOutputShape() {
        num_loc_classes = shareLocation? 1:numClasses;
        num_priors = inputShape[2][3] / 4;
        outputShape = new int[]{1, numClasses, inputShape[2][3], 4};
    }
}
