package ccanonizado.signlanguagetranslator;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.wonderkiln.camerakit.CameraListener;
import com.wonderkiln.camerakit.CameraProperties;
import com.wonderkiln.camerakit.CameraView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class SignToText extends AppCompatActivity {

    // widget variables
    private Button translateButton;
    private TextView hint;
    private TextView result;
    private CameraView cameraView;

    // translation variables
    private String resultText;
    private Boolean translated;
    static Classifier classifier;
    static final int INPUT_SIZE = 300;
    private static final int IMAGE_MEAN = 128;
    private static final float IMAGE_STD = 128;
    private static final String INPUT_NAME = "Mul";
    private static final String OUTPUT_NAME = "final_result";
    private static final String MODEL_FILE = "file:///android_asset/graph.pb";
    private static final String LABEL_FILE = "file:///android_asset/labels.txt";
    static List<Classifier.Recognition> results;
    static Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_to_text);

        // show back button
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // get references of widgets
        translateButton = findViewById(R.id.translateButtonST);
        hint = findViewById(R.id.stHint);
        result = findViewById(R.id.stResult);
        cameraView = findViewById(R.id.camera);

        // hide result first
        result.setVisibility(View.INVISIBLE);

        // initialize variables and model
        translated = false;
        results = new ArrayList<>();
        initializeModel();

        cameraView.setCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(byte[] picture) {

                bitmap = BitmapFactory.decodeByteArray(
                        picture,
                        0,
                        picture.length);
                bitmap = Bitmap.createScaledBitmap(
                        bitmap,
                        INPUT_SIZE,
                        INPUT_SIZE,
                        false);
//                bitmap = ImageHelper.getRotatedImage(bitmap, 90);

                // call tensorflow image recognition
                results = classifier.recognizeImage(bitmap);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        translateButton.setText("Translate");
                        translateButton.setEnabled(true);
                        translateButton.setClickable(true);
                    }
                });

                // use UI thread to make changes
                if (results.size() > 0)
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            resultText = results.get(0).getTitle();
                            result.setText(getString(resultText));
                        }
                    });
            }
        });

        translateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // hide and show other widgets
                hint.setVisibility(View.INVISIBLE);
                result.setVisibility(View.VISIBLE);
                
                // user has translated then take picture
                translated = true;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        translateButton.setText("Translating");
                        translateButton.setEnabled(false);
                        translateButton.setClickable(false);
                    }
                });

                result.setText(null);
                cameraView.captureImage();
            }
        });
    }

    private String getString(String resultText) {

        // format other labels
        switch (resultText) {
            case "hellohi":
                resultText = "hello / hi";
                break;
            case "okaygoodjob":
                resultText = "okay / good job";
                break;
            default:
                break;
        }

        return resultText;
    }

    private void initializeModel() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    classifier = TensorFlowImageClassifier.create(
                            getAssets(),
                            MODEL_FILE,
                            LABEL_FILE,
                            INPUT_SIZE,
                            INPUT_SIZE,
                            IMAGE_MEAN,
                            IMAGE_STD,
                            INPUT_NAME,
                            OUTPUT_NAME);
                } catch (final Exception e) {
                    throw new RuntimeException("Error initializing TensorFlow Model!", e);
                }
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraView.start();
    }

    @Override
    protected void onPause() {
        cameraView.stop();
        super.onPause();
    }

    // home button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) this.finish();
        return super.onOptionsItemSelected(item);
    }

    // back button
    @Override
    public void onBackPressed() {

        // if user has translated once - revert to default view
        if (translated) {
            result.setText(null);
            hint.setVisibility(View.VISIBLE);
            result.setVisibility(View.INVISIBLE);
            translated = false;
        }
        else
            super.onBackPressed();
    }
}
