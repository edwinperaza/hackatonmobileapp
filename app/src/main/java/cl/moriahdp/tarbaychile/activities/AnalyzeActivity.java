//
// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license.
//
// Microsoft Cognitive Services (formerly Project Oxford): https://www.microsoft.com/cognitive-services
//
// Microsoft Cognitive Services (formerly Project Oxford) GitHub:
// https://github.com/Microsoft/Cognitive-Vision-Android
//
// Copyright (c) Microsoft Corporation
// All rights reserved.
//
// MIT License:
// Permission is hereby granted, free of charge, to any person obtaining
// a copy of this software and associated documentation files (the
// "Software"), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to
// permit persons to whom the Software is furnished to do so, subject to
// the following conditions:
//
// The above copyright notice and this permission notice shall be
// included in all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED ""AS IS"", WITHOUT WARRANTY OF ANY KIND,
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
// NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
// LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
// OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
// WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
//
package cl.moriahdp.tarbaychile.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.microsoft.projectoxford.vision.VisionServiceClient;
import com.microsoft.projectoxford.vision.contract.AnalysisResult;
import com.microsoft.projectoxford.vision.rest.VisionServiceException;
//import com.microsoft.projectoxford.visionsample.helper.ImageHelper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import cl.moriahdp.tarbaychile.R;
import cl.moriahdp.tarbaychile.fragments.CameraFragment;
import cl.moriahdp.tarbaychile.utils.Constant;
import cl.moriahdp.tarbaychile.utils.ImageHelper;
import cl.moriahdp.tarbaychile.utils.SelectImageActivity;
import cl.moriahdp.tarbaychile.utils.VisionServiceRestClient2;

public class AnalyzeActivity extends ActionBarActivity {

    // Flag to indicate which task is to be performed.
    private static final int REQUEST_SELECT_IMAGE = 0;

    // The button to select an image
    private Button mButtonSelectImage;

    // The URI of the image selected to detect.
    private Uri mImageUri;

    // The image selected to detect.
    private Bitmap mBitmap;

    // The edit to show status and result.
    private EditText mEditText;

    private VisionServiceClient client;

    public static final int REQUEST_TAKE_PHOTO = 1;
    public static final int REQUEST_PICK_PHOTO = 2;

    public ImageView mCameraImage;
    public Context mContext;

    private String mCurrentPhotoPath;
    private String mCurrentFileName;
    private View mRootView;
    private static final String TAG = AnalyzeActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyze);

        if (client==null){
            client = new VisionServiceRestClient2(getString(R.string.subscription_key));
        }

        mButtonSelectImage = (Button)findViewById(R.id.buttonSelectImage);
        mEditText = (EditText)findViewById(R.id.editTextResult);

        if (checkPermission(getApplicationContext())) {
            selectImage();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(
                        new String[]{
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.CAMERA
                        },
                        Constant.REQUEST_PERMISSION_CODE);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_analyze, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    public void doAnalyze() {
        mButtonSelectImage.setEnabled(false);
        mEditText.setText("Analyzing...");

        try {
            new doRequest().execute();
        } catch (Exception e)
        {
            mEditText.setText("Error encountered. Exception is: " + e.toString());
        }
    }

    // Called when the "Select Image" button is clicked.
    public void selectImage(View view) {
        mEditText.setText("");

        Intent intent;
        intent = new Intent(AnalyzeActivity.this, SelectImageActivity.class);
        startActivityForResult(intent, REQUEST_SELECT_IMAGE);
    }

    public void selectImage() {
        mEditText.setText("");

        Intent intent;
        intent = new Intent(AnalyzeActivity.this, SelectImageActivity.class);
        startActivityForResult(intent, REQUEST_SELECT_IMAGE);
    }

    // Called when image selection is done.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("AnalyzeActivity", "onActivityResult");
        switch (requestCode) {
            case REQUEST_SELECT_IMAGE:
                if(resultCode == RESULT_OK) {
                    // If image is selected successfully, set the image URI and bitmap.
                    mImageUri = data.getData();

                    mBitmap = ImageHelper.loadSizeLimitedBitmapFromUri(
                            mImageUri, getContentResolver());
                    if (mBitmap != null) {
                        // Show the image on screen.
                        ImageView imageView = (ImageView) findViewById(R.id.selectedImage);
                        imageView.setImageBitmap(mBitmap);

                        // Add detection log.
                        Log.d("AnalyzeActivity", "Image: " + mImageUri + " resized to " + mBitmap.getWidth()
                                + "x" + mBitmap.getHeight());

                        doAnalyze();
                    }
                }
                break;
            default:
                break;
        }
    }


    private String process() throws VisionServiceException, IOException {
        Gson gson = new Gson();
//        String[] features = {"ImageType", "Color", "Faces", "Adult", "Categories"};
        String[] features = {"Color", "Categories", "Description", "Tags"};
//        String[] features = {"Categories"};
        //String[] features = {};
        String[] details = {};

        // Put the image into an input stream for detection.
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());

        AnalysisResult v = this.client.analyzeImage(inputStream, features, details);

        String result = gson.toJson(v);
        Log.d("result", result);

        return result;
    }

    private class doRequest extends AsyncTask<String, String, String> {
        // Store error message
        private Exception e = null;

        public doRequest() {
        }

        @Override
        protected String doInBackground(String... args) {
            try {
                return process();
            } catch (Exception e) {
                this.e = e;    // Store error
            }

            return null;
        }

        @Override
        protected void onPostExecute(String data) {
            super.onPostExecute(data);
            // Display based on error existence

            mEditText.setText("");
            if (e != null) {
                mEditText.setText("Error: " + e.getMessage());
                this.e = null;
            } else {
                Gson gson = new Gson();
                AnalysisResult result = gson.fromJson(data, AnalysisResult.class);

                //mEditText.append("Image format: " + result.metadata.format + "\n");
                //mEditText.append("Image width: " + result.metadata.width + ", height:" + result.metadata.height + "\n");
                //mEditText.append("Clip Art Type: " + result.imageType.clipArtType + "\n");
                //mEditText.append("Line Drawing Type: " + result.imageType.lineDrawingType + "\n");
                //mEditText.append("Is Adult Content:" + result.adult.isAdultContent + "\n");
                //mEditText.append("Adult score:" + result.adult.adultScore + "\n");
                //mEditText.append("Is Racy Content:" + result.adult.isRacyContent + "\n");
                //mEditText.append("Racy score:" + result.adult.racyScore + "\n\n") ;

//                for (Category category: result.categories) {
//                    mEditText.append("Category: " + category.name + ", score: " + category.score + "\n");
//                }

//                mEditText.append("\n");
//                int faceCount = 0;
//                for (Face face: result.faces) {
//                    faceCount++;
//                    mEditText.append("face " + faceCount + ", gender:" + face.gender + "(score: " + face.genderScore + "), age: " + + face.age + "\n");
//                    mEditText.append("    left: " + face.faceRectangle.left +  ",  top: " + face.faceRectangle.top + ", width: " + face.faceRectangle.width + "  height: " + face.faceRectangle.height + "\n" );
//                }
//                if (faceCount == 0) {
//                    mEditText.append("No face is detected");
//                }
//                mEditText.append("\n");
//
//                mEditText.append("\nDominant Color Foreground :" + result.color.dominantColorForeground + "\n");
//                mEditText.append("Dominant Color Background :" + result.color.dominantColorBackground + "\n");

                mEditText.append("\n--- Raw Data ---\n\n");
                mEditText.append(data);
                mEditText.setSelection(0);
            }

            mButtonSelectImage.setEnabled(true);
        }
    }

    public static boolean checkPermission(Context context) {
        int result = ContextCompat.checkSelfPermission(context,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(context,
                android.Manifest.permission.ACCESS_COARSE_LOCATION);
        int result2 = ContextCompat.checkSelfPermission(context,
                android.Manifest.permission.CAMERA);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED &&
                result2 == PackageManager.PERMISSION_GRANTED;
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (resultCode == -1) {
//            switch (requestCode) {
//                case REQUEST_TAKE_PHOTO: {
//                    ImageHelper.saveResizedImage(mContext, mCurrentFileName, mCurrentPhotoPath);
//                    ImageHelper.setImageBitMapToImageView(mContext, mCameraImage, mCurrentPhotoPath, Constant.REPORT_ROFILE_WIDTH, Constant.REPORT_PROFILE_HEIGHT);
//                    break;
//                }
//                case REQUEST_PICK_PHOTO: {
//                    Uri photoUri = data.getData();
//                    mCurrentPhotoPath = ImageHelper.getPicturePathFromGalleryUri(mContext, photoUri);
//                    ImageHelper.setImageBitMapToImageView(mContext, mCameraImage, mCurrentPhotoPath, Constant.REPORT_ROFILE_WIDTH, Constant.REPORT_PROFILE_HEIGHT);
//                    break;
//                }
//            }
//
//        } else {
//            // Result was a failure
//            switch (requestCode) {
//                case REQUEST_TAKE_PHOTO:
//                    //Crashlytics.log("Foto not taken");
//                    Snackbar.make(mRootView, "Foto no tomada", Snackbar.LENGTH_LONG)
//                            .setAction("Action", null).show();
//                    break;
//                case REQUEST_PICK_PHOTO:
//                    //Crashlytics.log("No image selected");
//                    Snackbar.make(mRootView, "No seleccionó ninguna imagen", Snackbar.LENGTH_LONG)
//                            .setAction("Action", null).show();
//                    break;
//            }
//        }
//    }
}
