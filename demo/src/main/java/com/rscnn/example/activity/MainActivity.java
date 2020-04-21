package com.rscnn.example.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.renderscript.RenderScript;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import android.util.Log;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.rscnn.example.R;
import com.rscnn.model.MobileNetSSD;
import com.rscnn.model.MobileNet;
import com.rscnn.model.ObjectDetector;
import com.rscnn.model.PvaLite;
import com.rscnn.network.ConvNet;
import com.rscnn.network.DetectResult;
import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.lang.Object;
import java.util.Map;

import com.rscnn.utils.DeserializeDlaaS;
import com.rscnn.utils.PhotoViewHelper;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private RenderScript rs;
    private ObjectDetector detector = null;
    private String modelPath = null;
    private Spinner spinner;
    DeserializeDlaaS dlaasD = new DeserializeDlaaS();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rs = RenderScript.create(this);
        setContentView(R.layout.activity_main);

        spinner = (Spinner) this.findViewById(R.id.spinner);

        List<String> list = new ArrayList<>();

        final Map<String, String> assetMap = new HashMap<String, String>();

        try {
            //get all folders in our assets dir
            AssetManager myAssets = getAssets();
            String[] choices = myAssets.list("");

            //find which of these contain dlaas jsons
            for (String folder : choices) {
                String[] fileList = getAssets().list(folder);
                for (String file : fileList) {
                    if (dlaasD.isDescriptorFile(file)) {
                        String modelName = folder + ": " + dlaasD.ReturnModelName(myAssets, folder);
                        list.add(modelName);
                        modelPath = folder;
                        assetMap.put(modelName, folder);
                        break;  // exit after first descriptor in folder is found
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // apply an array adapter to our spinner, use the spinner to select the desired model to use
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, list);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = parent.getItemAtPosition(position).toString();

                // set model to selection
                modelPath = assetMap.get(selection);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        SeekBar sigmaSeekBar = (SeekBar) findViewById(R.id.seekBarSigma); // initiate the Seek bar
        final float maxValueSigma = (float)sigmaSeekBar.getMax();
        final TextView sigView = (TextView) findViewById(R.id.textView2);

        sigmaSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                 progressChangedValue = progress;
                 float floatVal = (float)progress / maxValueSigma;
                 System.setProperty("SIGMA_ENV", String.valueOf(floatVal));
                 sigView.setText(" Soft NMS Sigma: "+floatVal);
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                float floatVal = (float)progressChangedValue / maxValueSigma;
                System.setProperty("SIGMA_ENV", String.valueOf(floatVal));
            }
        });
        // 2 values to trigger a change
        sigmaSeekBar.setProgress(40);
        sigmaSeekBar.setProgress(50);

        SeekBar confSeekBar = (SeekBar) findViewById(R.id.seekBarThresh);
        final float maxValueConf = (float)confSeekBar.getMax();
        final TextView threshView = (TextView) findViewById(R.id.textView);
        confSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
                float floatVal = (float)progress / maxValueConf;
                System.setProperty("CTHRESH_ENV", String.valueOf(floatVal));
                threshView.setText(" Confidence Threshold: "+floatVal);
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                float floatVal = (float)progressChangedValue / maxValueConf;
                System.setProperty("CTHRESH_ENV", String.valueOf(floatVal));
            }
        });
        confSeekBar.setProgress(49);
        confSeekBar.setProgress(50);

    }

       // here we detect which type of model we are using from the dlaasD, and instantiate appropriate class
       private void setDetector() {

        try {
            AssetManager assetManager = getAssets();
            String[] fileList = assetManager.list(modelPath);
            String modelType = dlaasD.ReturnModelType(assetManager, modelPath);

            if (fileList.length != 0) {

                if (modelType.equals("mobilenet_1")) {
                    detector = new MobileNet(rs, assetManager, modelPath);
                } else {

                    detector = new MobileNetSSD(rs, assetManager, modelPath);
                }
            } else {
                if (modelType.equals("mobilenet_1")) {
                    String modelDir = Environment.getExternalStorageDirectory().getPath() + "/" + modelPath;
                    detector = new MobileNet(rs, assetManager, modelDir);
                } else {
                    String modelDir = Environment.getExternalStorageDirectory().getPath() + "/" + modelPath;
                    detector = new MobileNetSSD(rs, assetManager, modelDir);
                }

            }
        } catch (IOException e) {
            Log.d("onFindModule: ", e.toString());
            e.printStackTrace();
        }
      }





    public void btnClicked(View view) {
        Intent intent;
        intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 0);
    }

    private Bitmap cropImage(Bitmap image) {
        int height = image.getHeight();
        int width = image.getWidth();
        int newHeight = height;
        int newWidth = width;
        int x = 0;
        int y = 0;
        if (height > width) {
            newHeight = width;
            y = (height - newHeight) / 2;
        }
        else {
            newWidth = height;
            x = (width - newWidth) / 2;
        }
        return Bitmap.createBitmap(image, x, y, newWidth, newHeight);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        try {
            ContentResolver resolver = this.getContentResolver();
            Uri uri = data.getData();
            Bitmap bmp = MediaStore.Images.Media.getBitmap(resolver, uri);
            Bitmap image = cropImage(bmp);
            ImageView img = (ImageView) findViewById(R.id.imageView);
            setDetector();
            List<DetectResult> result = detector.detect(image);
            Bitmap toDraw = PhotoViewHelper.drawTextAndRect(image, result);
            img.setImageBitmap(toDraw);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
