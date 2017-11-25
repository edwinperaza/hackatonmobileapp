package cl.moriahdp.tarbaychile.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

//import com.crashlytics.android.Crashlytics;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import cl.moriahdp.tarbaychile.R;

//import cl.magnet.vigia.R;

/**
 * Class to manage Image methods
 *
 * @author Magnet SPA
 *         Created 13/3/17
 */
public class ImageHelper {

    public static final int PHOTO = 1;
    public static final int VIDEO = 2;
    public static final String PHOTO_FORMAT = ".jpg";
    public static final String VIDEO_FORMAT = ".mp4";
    // The maximum side length of the image to detect, to keep the size of image less than 4MB.
    // Resize the image if its side length is larger than the maximum.
    private static final int IMAGE_MAX_SIDE_LENGTH = 1280;

    public static Bitmap getPhotoThumbnail(String path, int width, int height) {
//      Get the thumbnail using the Android class for it
        Bitmap image = BitmapFactory.decodeFile(path);
        Bitmap thumb = ThumbnailUtils.extractThumbnail(
                image, width, height);
        return thumb;
    }

    public static Bitmap getPhoto(String path) {
        Bitmap image = BitmapFactory.decodeFile(path);
        return image;
    }

    public static Bitmap rotateBitmapOrientation(String photoFilePath) {
        int rotationAngle = 0;
        int rotation;
        Bitmap bm = null;

        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoFilePath, bounds);
        BitmapFactory.Options opts = new BitmapFactory.Options();

        try {
            bm = BitmapFactory.decodeFile(photoFilePath, opts);

            ExifInterface exif = new ExifInterface(photoFilePath);
            String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
            rotation = (orientString != null) ? Integer.parseInt(orientString) : ExifInterface.ORIENTATION_NORMAL;

            if (rotation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
            if (rotation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
            if (rotation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;

        } catch (IOException e) {
            //Crashlytics.log("Error when try to get ExifInterface or decode file from path");
            e.printStackTrace();
        }

        if (bm != null) {
            Matrix matrix = new Matrix();
            matrix.setRotate(rotationAngle, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
            Bitmap rotatedBitmap = Bitmap.createBitmap(bm, 0, 0, bounds.outWidth, bounds.outHeight, matrix, true);
            return rotatedBitmap;
        }
        return null;
    }

    public static void setPhotoThumbnail(String path, ImageView imageView, int width, int height) {
//      Get the thumbnail using the Android class for it
        Bitmap img = BitmapFactory.decodeFile(path);
        Bitmap thumb = ThumbnailUtils.extractThumbnail(
                img, width, height);
        imageView.setImageBitmap(thumb);
    }

    public static void setPhotoThumbnail(Bitmap bitmap, ImageView imageView, int width, int height) {
        Bitmap thumb = ThumbnailUtils.extractThumbnail(
                bitmap, width, height);
        imageView.setImageBitmap(thumb);
    }

    public static Bitmap getPhotoThumbnail(Bitmap bitmap, int width, int height) {
        Bitmap thumb = ThumbnailUtils.extractThumbnail(
                bitmap, width, height);
        return thumb;
    }

    public static boolean saveResizedImage(Context context, String filename, String currentPhotoPath) {

        Bitmap rawTakenImage = rotateBitmapOrientation(currentPhotoPath);

        if (rawTakenImage != null) {
            Bitmap resizedBitmap = BitmapScaler.scaleToFitWidth(rawTakenImage, 600);
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
            Uri resizedUri = getPhotoFileUri(filename, context);
            if (resizedUri != null) {
                File resizedFile = new File(resizedUri.getPath());
                try {
                    FileOutputStream fos = new FileOutputStream(resizedFile);
                    // Write the bytes of the bitmap to file
                    fos.write(bytes.toByteArray());
                    fos.close();
                    return true;
                } catch (IOException e) {
                    //Crashlytics.log("Error when try to save resize Image");
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    public static boolean setImageBitMapToImageView(Context context, ImageView imageView, String currentPhotoPath, int width, int height) {

        if (currentPhotoPath != null) {
            Bitmap photoTaken = BitmapFactory.decodeFile(currentPhotoPath);
            if (photoTaken != null) {
                photoTaken = getPhotoThumbnail(photoTaken, width, height);
                imageView.setImageBitmap(photoTaken);
                return true;
            }
        } else {
            return false;
        }
        return false;
    }

    // Returns the Uri for a photo stored on disk given the fileName
    public static Uri getPhotoFileUri(String fileName, Context context) {
        // Only continue if the SD Card is mounted
        if (isExternalStorageAvailable()) {
            // Get safe storage directory for photos
            // Use `getExternalFilesDir` on Context to access package-specific directories.
            // This way, we don't need to request external read/write runtime permissions.
            File mediaStorageDir;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                mediaStorageDir = new File(
                        context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), context.getResources().getString(R.string.app_name));
            } else {
                mediaStorageDir =
                        context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            }

            // Create the storage directory if it does not exist
            if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
                Log.d("TAG", "failed to create directory");
            }

            // Return the file target for the photo based on filename
            return Uri.fromFile(new File(mediaStorageDir.getPath() + File.separator + fileName));
        }
        return null;
    }

    // Returns true if external storage for photos is available
    public static boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }

    public static File createImageFile(Context context) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName;
        File image;
        File storageDirPictures = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        imageFileName = "IMG_" + timeStamp;
        image = File.createTempFile(
                imageFileName,
                ImageHelper.PHOTO_FORMAT,
                storageDirPictures
        );
        return image;
    }

    public static String getFileName() {
        // Using http://stackoverflow.com/questions/7888670/how-to-save-own-file-name-using-default-video-recorder-in-android-in-acer500
        // Create a filename using a timestamp
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName;
        fileName = "IMG_" + timeStamp + ImageHelper.PHOTO_FORMAT;
        return fileName;
    }

    public static Uri getExternalFileUri(Context context, String fileName, String TAG) {
        // Only continue if the SD Card is mounted
        if (ImageHelper.isExternalStorageAvailable()) {
            // Get safe storage directory for photos
            // Use `getExternalFilesDir` on Context to access package-specific directories.
            // This way, we don't need to request external read/write runtime permissions.
            File mediaStorageDir;
            mediaStorageDir = new File(
                    context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                    Constant.APP_NAME
            );

            if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
                Log.e(TAG, "failed to create directory");
            }
            // Return the file target for the photo based on filename
            return Uri.fromFile(new File(mediaStorageDir.getPath() + File.separator + fileName));
        }
        return null;
    }

    public static String getPicturePathFromGalleryUri(Context context, Uri uri) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};

        Cursor cursor = context.getContentResolver().query(uri, filePathColumn, null, null, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        return cursor.getString(columnIndex);
    }

    // Decode image from imageUri, and resize according to the expectedMaxImageSideLength
    // If expectedMaxImageSideLength is
    //     (1) less than or equal to 0,
    //     (2) more than the actual max size length of the bitmap
    //     then return the original bitmap
    // Else, return the scaled bitmap
    public static Bitmap loadSizeLimitedBitmapFromUri(
            Uri imageUri,
            ContentResolver contentResolver) {
        try {
            // Load the image into InputStream.
            InputStream imageInputStream = contentResolver.openInputStream(imageUri);

            // For saving memory, only decode the image meta and get the side length.
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            Rect outPadding = new Rect();
            BitmapFactory.decodeStream(imageInputStream, outPadding, options);

            // Calculate shrink rate when loading the image into memory.
            int maxSideLength =
                    options.outWidth > options.outHeight ? options.outWidth : options.outHeight;
            options.inSampleSize = 1;
            options.inSampleSize = calculateSampleSize(maxSideLength, IMAGE_MAX_SIDE_LENGTH);
            options.inJustDecodeBounds = false;
            imageInputStream.close();


            // Load the bitmap and resize it to the expected size length
            imageInputStream = contentResolver.openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(imageInputStream, outPadding, options);
            maxSideLength = bitmap.getWidth() > bitmap.getHeight()
                    ? bitmap.getWidth(): bitmap.getHeight();
            double ratio = IMAGE_MAX_SIDE_LENGTH / (double) maxSideLength;
            if (ratio < 1) {
                bitmap = Bitmap.createScaledBitmap(
                        bitmap,
                        (int)(bitmap.getWidth() * ratio),
                        (int)(bitmap.getHeight() * ratio),
                        false);
            }

            return rotateBitmap(bitmap, getImageRotationAngle(imageUri, contentResolver));
        } catch (Exception e) {
            return null;
        }
    }

    // Return the number of times for the image to shrink when loading it into memory.
    // The SampleSize can only be a final value based on powers of 2.
    private static int calculateSampleSize(int maxSideLength, int expectedMaxImageSideLength) {
        int inSampleSize = 1;

        while (maxSideLength > 2 * expectedMaxImageSideLength) {
            maxSideLength /= 2;
            inSampleSize *= 2;
        }

        return inSampleSize;
    }

    // Get the rotation angle of the image taken.
    private static int getImageRotationAngle(
            Uri imageUri, ContentResolver contentResolver) throws IOException {
        int angle = 0;
        Cursor cursor = contentResolver.query(imageUri,
                new String[] { MediaStore.Images.ImageColumns.ORIENTATION }, null, null, null);
        if (cursor != null) {
            if (cursor.getCount() == 1) {
                cursor.moveToFirst();
                angle = cursor.getInt(0);
            }
            cursor.close();
        } else {
            ExifInterface exif = new ExifInterface(imageUri.getPath());
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    angle = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    angle = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    angle = 90;
                    break;
                default:
                    break;
            }
        }
        return angle;
    }

    // Rotate the original bitmap according to the given orientation angle
    private static Bitmap rotateBitmap(Bitmap bitmap, int angle) {
        // If the rotate angle is 0, then return the original image, else return the rotated image
        if (angle != 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(angle);
            return Bitmap.createBitmap(
                    bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } else {
            return bitmap;
        }
    }

}
