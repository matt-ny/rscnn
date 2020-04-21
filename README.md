### Watson Visual Recognition on Android using RenderScript CNN 


The purpose of this repo is to demonstrate an implementation for **deploying custom built detection models** from **Watson Visual Recognition** (using ***MobileNetv1 + SSD***) onto an **Android device**.


<p>
  <img src="/images/lego-demo.jpg" width="100" syle="padding: 40px" >
</p>

### What You'll Need

1) Android Studio w/ API 28

2) An instance of the IBM [Watson Visual Recognition](https://www.ibm.com/cloud/watson-visual-recognition) cloud service.  There are free and paid plans. Either works fine.
    - not ready to sign up for Watson? Check out the [shortcut](#shortcut) below.

### Overview

1. Create a custom object detection model with Watson Visual Recognition.
    - see [Object Detection in 5 Minutes](https://medium.com/@vincent.perrin/watson-visual-recognition-object-detection-in-action-in-5-minutes-8f97c4b613c3) for how to do this
    
2. Download your model as a zip file, using the API endpoint [GET /v4/collections/{collection_id}/model/?version=2019-05-20&feature=objects&model_format=rscnn](https://cloud.ibm.com/apidocs/visual-recognition/visual-recognition-v4#create-a-collection) (link to update)

3. Create a subfolder in this repo under `demo/src/main/assets/` named after your model, such as `MyModel`.
 
4. Unzip the contents of your downloaded zip file into your new assets subfolder, `MyModel`

5. Repeat steps 2 - 4 for any additional models you want to use.

6. Deploy the project to your Android device or emulator in the Studio.

7. Select a model from the drop down spinner, and select test image to run the model on.
    - the spinner will show the folder name you used for the model and its UUID
    - the results will be overlaid on the image.

### [Shortcut](#shortcut)

We have included a model trained from the [data](https://github.com/vperrinfr/Lego_Detection) [@vperrinfr](https://github.com/vperrinfr) made.  
It's in the folder  `demo/src/main/assets/LegoPersonModel`.  If you don't want this model in your
app, just delete that folder.  We have also provided some images to test the model with in the `images/testimages` 
folder of this repo.  Just copy them to your device or emulator to use them with this demo model.

Quickstart with the Lego model:
1. Deploy the project to your Android device or emulator in the Studio.

2. Select the LegoPersonModel  from the drop down spinner, and select test image to run the model on.
    - the results will be overlaid on the image.
