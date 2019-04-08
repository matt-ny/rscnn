### Render Script CNN for Android

This repo is a fork of chuanqi305's rscnn, a CNN framework based on RenderScript. 

The purpose of this repo is to demonstrate an implementation for **deploying prebuilt detection models** from **Caffe** (using ***MobileNetv1 + SSD***) onto an **Android device**.


<p align="left">
  <img src="/images/screenshot1.jpg" width="100" syle="padding: 40px" >
  <img src="/images/screenshot2.jpg" width="100" syle="padding: 40px" >
  <img src="/images/screenshot3.jpg" width="100" syle="padding: 40px" >

</p>

### What You'll Need

1) Android Studio w/ API 28

2) Caffe (w/ssd) : https://github.com/chuanqi305/ssd

3) Anaconda (Recomended) 

### Usage
1. Download [MobileNet-SSD](https://github.com/chuanqi305/MobileNet-SSD) model.
```
git clone https://github.com/chuanqi305/MobileNet-SSD
```

You'll want to download the pretrained MobileNetSSD_deploy.caffemodel and move it to MobileNet-SSD folder.

2. Use script/convert_caffe_model.py to convert the model to new format, do not forget to change the caffe root path in the converting script.

```
python script/convert_caffe_model.py --model MobileNet-SSD/deploy.prototxt --weights MobileNet-SSD/MobileNetSSD_deploy.caffemodel --savedir mobilenet-ssd
```

3. Push the converted model files to src/main/assets folder of your project.
```
cp -ar mobilenet-ssd demo/src/main/assets/
```
4. Run this demo, and you can select a photo to see the object detection result.


### Errors
See Wiki for more documentation details of additional errors you might run into and how to correct them. 
