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
import android.widget.FrameLayout;
import android.widget.TextView;

import com.wonderkiln.camerakit.CameraListener;
import com.wonderkiln.camerakit.CameraProperties;
import com.wonderkiln.camerakit.CameraView;

import java.util.ArrayList;
import java.util.List;

public class SignToText extends AppCompatActivity implements Camera.PreviewCallback {

    // widget variables
    private Button translateButton;
    private Button resetButton;
    private TextView hint;
    private TextView result;
    private CameraView cameraView;

    private Camera mCamera;
    private CameraPreview mPreview;

    // translation variables
    private String last;
    private String current;
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
        resetButton = findViewById(R.id.resetButton);
        hint = findViewById(R.id.stHint);
        result = findViewById(R.id.stResult);
//        cameraView = findViewById(R.id.camera);

        // hide other widgets
        result.setVisibility(View.INVISIBLE);
        resetButton.setVisibility(View.INVISIBLE);

        // initialize variables and model
        translated = false;
        results = new ArrayList<>();
        last = "";
        initializeModel();

//        // Create an instance of Camera
//        mCamera = getCameraInstance();
//
//        // Create our Preview view and set it as the content of our activity.
//        mPreview = new CameraPreview(this, mCamera);
//        FrameLayout preview = findViewById(R.id.camera_preview);
//        preview.addView(mPreview);

//        cameraView.setCameraListener(new CameraListener() {
//            @Override
//            public void onPictureTaken(byte[] picture) {
//                bitmap = BitmapFactory.decodeByteArray(
//                        picture,
//                        0,
//                        picture.length);
//                bitmap = Bitmap.createScaledBitmap(
//                        bitmap,
//                        INPUT_SIZE,
//                        INPUT_SIZE,
//                        false);
////                bitmap = ImageHelper.getRotatedImage(bitmap, 90);
//
//                // call tensorflow image recognition
//                results = classifier.recognizeImage(bitmap);
//
//                // use UI thread to make changes
//                if (results.size() > 0)
//
//                    // only change if current result is unique
//                    if (last != results.get(0).getTitle())
//                        new Handler(Looper.getMainLooper()).post(new Runnable(){
//                            @Override
//                            public void run() {
//                                last = results.get(0).getTitle();
//                                current = result.getText().toString();
//                                result.setText(getString(current, last));
//                            }
//                        });
//            }
//        });

        translateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // hide and show other widgets
                translateButton.setVisibility(View.INVISIBLE);
                hint.setVisibility(View.INVISIBLE);
                resetButton.setVisibility(View.VISIBLE);
                result.setVisibility(View.VISIBLE);
                
                // user has translated then take picture
                translated = true;
//                cameraView.captureImage();
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // reset result text if user thinks it is too long
                result.setText(null);
            }
        });
    }

    private String getString(String current, String result) {

        // format other labels
        switch (result) {
            case "hellohi":
                result = "hello / hi";
                break;
            case "okaygoodjob":
                result = "okay / good job";
                break;
            default:
                break;
        }

        // append single character
        if (result.length() == 1)
            current += result;
        else {

            /*

                This block just checks if last word from current
                is a word / phrase available in the vocabulary
                if not - add space before and after the result

            */

            if (current.length() > 0){
                if (current.charAt(current.length()-1) != ' ')
                    current += ' ' + result + ' ';
                else
                    current += result + ' ';
            }
            else if (current.length() == 0)
                current += result;
            else
                current += result + ' ';
        }

        return current;
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

    public static Camera getCameraInstance(){
        Camera c = null;
        try { c = Camera.open(); }
        catch (Exception e){ }
        return c;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mCamera != null){
            mCamera.setPreviewCallback(null);
            mPreview.getHolder().removeCallback(mPreview);
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Create an instance of Camera
        mCamera = getCameraInstance();

        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = findViewById(R.id.camera_preview);
        preview.addView(mPreview);
    }

    public void onPreviewFrame(byte[] picture, Camera camera) {
        if (translated) {
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

            // use UI thread to make changes
            if (results.size() > 0)

                // only change if current result is unique
                if (last != results.get(0).getTitle())
                    new Handler(Looper.getMainLooper()).post(new Runnable(){
                        @Override
                        public void run() {
                            last = results.get(0).getTitle();
                            current = result.getText().toString();
                            result.setText(getString(current, last));
                        }
                    });
        }
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        cameraView.start();
//    }
//
//    @Override
//    protected void onPause() {
//        cameraView.stop();
//        super.onPause();
//    }

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
            translateButton.setVisibility(View.VISIBLE);
            hint.setVisibility(View.VISIBLE);
            resetButton.setVisibility(View.INVISIBLE);
            result.setVisibility(View.INVISIBLE);
            translated = false;
            last = "";
        }
        else
            super.onBackPressed();
    }
}
