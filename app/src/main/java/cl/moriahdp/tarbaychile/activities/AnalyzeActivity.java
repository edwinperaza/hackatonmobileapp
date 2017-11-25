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
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.microsoft.projectoxford.vision.VisionServiceClient;
import com.microsoft.projectoxford.vision.contract.AnalysisResult;
import com.microsoft.projectoxford.vision.contract.Caption;
import com.microsoft.projectoxford.vision.contract.Tag;
import com.microsoft.projectoxford.vision.rest.VisionServiceException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cl.moriahdp.tarbaychile.R;
import cl.moriahdp.tarbaychile.models.AnalysisImageResult;
import cl.moriahdp.tarbaychile.models.product.Product;
import cl.moriahdp.tarbaychile.models.product.ProductRequestManager;
import cl.moriahdp.tarbaychile.network.AppResponseListener;
import cl.moriahdp.tarbaychile.network.VolleyManager;
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

        if (client == null) {
            client = new VisionServiceRestClient2(getString(R.string.subscription_key));
        }

        mEditText = (EditText) findViewById(R.id.editTextResult);
        mCameraImage = (ImageView) findViewById(R.id.selectedImage);
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

        mCameraImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        });
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
        mCameraImage.setClickable(false);
        try {
            new doRequest().execute();
        } catch (Exception e) {
            mEditText.setText("Error encountered. Exception is: " + e.toString());
        }
    }

    public void selectImage() {
        mEditText.setText("");
        Intent intent;
        intent = new Intent(AnalyzeActivity.this, SelectImageActivity.class);
        startActivityForResult(intent, REQUEST_SELECT_IMAGE);
    }

    /**
     * Check device version and launch camera depends on it.
     *
     * @param action      action to be implemented
     * @param requestCode code to know who launch the activity
     * @param errorToast  message to be shown if an issue occur.
     */
    private void dispatchMedia(String action, int requestCode, int errorToast) {
        // Create Intent to dispatch media and return control to the calling application
        Intent intent = new Intent(action);

        // Create the File where the photo should go
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            File photoFile = null;
            try {
                photoFile = ImageHelper.createImageFile(this);
                mCurrentPhotoPath = photoFile.getAbsolutePath();
                mCurrentFileName = photoFile.getName();
            } catch (IOException ex) {
                //Crashlytics.logException(ex);
                //Crashlytics.log("Error occurred while creating the File");
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri fileUriCreated = FileProvider.getUriForFile(
                        getApplicationContext(),
                        "cl.moriahdp.tarbaychile.fileprovider",
                        photoFile);

                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUriCreated);
            }

        } else {
            mCurrentFileName = ImageHelper.getFileName();

            Uri fileUriCreated = ImageHelper.getExternalFileUri(
                    this,
                    mCurrentFileName,
                    TAG);
            try {
                if (fileUriCreated != null) {
                    mCurrentPhotoPath = fileUriCreated.getPath();
                }
            } catch (Exception ex) {
                //Crashlytics.logException(ex);
                //Crashlytics.log("Error occurred while getting path");
            }

            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUriCreated);
        }

        // If you call startActivityForResult() using an intent that no app can handle, your app
        // will crash. So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Start the dispatch media intent
            startActivityForResult(intent, requestCode);
        } else {
            //Crashlytics.log("No app can handle the media");
            // No app can handle the media
            //showLongToast(errorToast);
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
                AnalysisImageResult result = gson.fromJson(data, AnalysisImageResult.class);
                if (result != null) {
                    if ((result.tags != null) && (result.tags.size() > 0)) {
                        List<Tag> tags = result.tags;
                        List<Tag> tagsUpdated = new ArrayList<>();
                        mEditText.append("Tags:");
                        for (Tag tag : tags) {
                            if (tag.confidence > 0.90) {
                                mEditText.append(" " + tag.name + ",");

                                tagsUpdated.add(tag);


                            }
                        }
                        getInformationFromServer(tagsUpdated);
                    }

                    if ((result.description.captions != null) && (result.description.captions.size() > 0)) {
                        for (Caption caption : result.description.captions) {
                            if (caption.confidence > 0.90) {
                                mEditText.append("\\nDescription:" + caption.text);
                                break;
                            }
                        }
                    }
                }

            }
            mCameraImage.setClickable(true);
        }
    }

    public void getInformationFromServer(List<Tag> tags){

        AppResponseListener<JSONObject> appResponseListener = new AppResponseListener<JSONObject>(getApplicationContext()){

            @Override
            public void onResponse(JSONObject response) {
                JSONObject jsonObject = null;
                try {
                    Log.d(TAG, response.toString());
                    jsonObject = response.getJSONObject("");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void noInternetConnectionError() {
                super.noInternetConnectionError();
            }

            @Override
            public void noInternetError() {
                super.noInternetError();
            }
        };

        //We add the request
        JsonObjectRequest request = ProductRequestManager.getInformationByTags(appResponseListener, tags.get(0).name);
        VolleyManager.getInstance(this).addToRequestQueue(request);
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
                        mCameraImage.setImageBitmap(mBitmap);
                        ImageHelper.setImageBitMapToImageView(this, mCameraImage, mCurrentPhotoPath, Constant.REPORT_ROFILE_WIDTH, Constant.REPORT_PROFILE_HEIGHT);
                        doAnalyze();
                    }
                }
                break;
            default:
                break;
        }
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (resultCode == -1) {
//            switch (requestCode) {
//                case REQUEST_TAKE_PHOTO: {
//                    mImageUri = Uri.parse(mCurrentPhotoPath);
//                    mBitmap = ImageHelper.loadSizeLimitedBitmapFromUri(
//                            Uri.parse(mCurrentPhotoPath), getContentResolver());
//                    if (mBitmap != null) {
//                        ImageHelper.saveResizedImage(this, mCurrentFileName, mCurrentPhotoPath);
//                        ImageHelper.setImageBitMapToImageView(this, mCameraImage, mCurrentPhotoPath, Constant.REPORT_ROFILE_WIDTH, Constant.REPORT_PROFILE_HEIGHT);
//                        doAnalyze();
//                    }
//                    break;
//                }
//                case REQUEST_PICK_PHOTO: {
//                    Uri photoUri = data.getData();
//                    mCurrentPhotoPath = ImageHelper.getPicturePathFromGalleryUri(this, photoUri);
//                    ImageHelper.setImageBitMapToImageView(this, mCameraImage, mCurrentPhotoPath, Constant.REPORT_ROFILE_WIDTH, Constant.REPORT_PROFILE_HEIGHT);
//                    doAnalyze();
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
