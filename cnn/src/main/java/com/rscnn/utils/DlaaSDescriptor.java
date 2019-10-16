package com.rscnn.utils;

import com.google.gson.annotations.*;

public class DlaaSDescriptor {

    @SerializedName("class_to_vector")
    public String[] labels;

    @SerializedName("model_type")
    public String modelType;

    @SerializedName("dlaas_training_id")
    public String dlaasTrainingId;

    @SerializedName("classifier_id")
    public String classifierId;
}
