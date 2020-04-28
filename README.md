# Watson Visual Recognition on Android

This repo demonstrates how to deploy custom object detection models from Watson Visual Recognition to an Android device. It uses RenderScript CNN with a MobileNetv1 + SSD implementation.

<p>
  <img src="/images/lego-demo.jpg" width="100" syle="padding: 40px" >
</p>

## Before you begin

Make sure that you have an up-to-date version of [Android Studio](https://developer.android.com/studio) and the Android SDK (API level 28 or later).

## Create a custom object detection model

You can create a custom object detection model by using the API or Watson Studio. Or you can use the already trained [model](#trained-model).

- To create a model in a graphical interface, use [Watson Studio](https://dataplatform.ibm.com/registration/stepone?target=watson_vision_combined&context=wdp&apps=watson_studio&cm_sp=WatsonPlatform-WatsonPlatform-_-OnPageNavCTA-IBMWatson_VisualRecognition-_-docs).
- To use the API, see [How to use Custom Object Detection](https://cloud.ibm.com/docs/visual-recognition?topic=visual-recognition-object-detection-overview#object-detection-sequence).

> **_Tip:_**  Use the [Lego Detection with Watson Visual Recognition](https://github.com/vperrinfr/Lego_Detection) project to create an example model in less than 10 minutes

## Download the model

If you created a model, use the API to download it.

1.  Create a directory in `demo/src/main/assets/` for your model, for example `demo/src/main/assets/mymodel`. If you have multiple models, create a separate directory for each.
1.  Use the [`Get a model` method](https://cloud.ibm.com/apidocs/visual-recognition/visual-recognition-v4#get-a-model) in the API to download the model. You might need to use the `List collections` method to find the collection ID.
1.  Extract the downloaded .zip model file to your new directory (`mymodel`).

## Deploy the app

To use the app on the Android Emulator, copy images that you want to analyze to the virtual device. For example, copy the images in the `images/testimages` directory if you're using the included `LegoPersonModel`.

## Run the app

When you run the app, you select your object detection model that you downloaded.

1.  On your device or the emulator, select your object detection model. It is identified by the directory name that you created for the model and the collection ID.

    > **_Tip:_** To use the [included model](#trained-model), select `LegoPersonModel`

1.  Click **Select Image** and choose an image from the device.
1.  About the controls:
    - Set the **Confidence threshold**, which is the minimum score that a feature must have to be returned.
    - Set the **Soft NMS Sigma**, which controls how much the score of one bounding box is penalized by an overlapping box with the same label and higher score. A higher setting might display extra boxes around an object. Use a lower setting when you don’t expect much overlap of objects of the same name.
    - After changing a control value, you must re-apply the model via the **Select Image** button to see updated results.

## Trained model

You can use the already trained model in this project by itself or with your own models. It is trained from the [Lego Detection with Watson Visual Recognition](https://github.com/vperrinfr/Lego_Detection) project.

The model is in the `demo/src/main/assets/LegoPersonModel` directory and is deployed with your app. If you don't want this model in your app, delete that directory before you deploy it.

You can test the model with the images in the `images/testimages` directory. Make sure to copy them to your device or virtual device.
