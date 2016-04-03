package theperfectfit.sizedoesmatter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends ActionBarActivity {

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;

    touchOverlay overlay;
    ToggleButton scaleButton;
    ToggleButton objectButton;
    EditText heightButton;
    EditText widthButton;



    private enum drawState {
        Scale,Object
    }

    // Constructor
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //switchState = ((Switch) findViewById(R.id.switch1)).isChecked() ? drawState.Object : drawState.Scale;

       //imgView = (ImageView) findViewById(R.id.MainImageView);

        //Log.d("", "-------------------------------------- " + String.valueOf(imgView.getMeasuredHeight()) + " " + String.valueOf(imgView.getHeight()));

        overlay = (touchOverlay) findViewById(R.id.TouchOverlay);
        scaleButton = (ToggleButton)  findViewById(R.id.ScaleButton);
        objectButton = (ToggleButton)  findViewById(R.id.ObjectButton);
        heightButton = (EditText) findViewById(R.id.HeightText);
        widthButton = (EditText) findViewById(R.id.WidthText);

    }

    // Opens camera app to get image
    public void createCameraIntent(View v){
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // start the image capture Intent
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    // Activity event listener
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                //try{
                    TouchImageView imgView = (TouchImageView)findViewById(R.id.MainImageView);

                    Uri uri = data.getData();
                    //InputStream is = getContentResolver().openInputStream(data.getData());
                    Bundle bundle = data.getExtras();
                    Bitmap bitmap = (Bitmap) bundle.get("data");
                    //Bitmap bitmap = BitmapFactory.decodeStream(is);
                    //is.close();
                    //RelativeLayout theLayout = (RelativeLayout) findViewById(R.id.the_layout);
                    bitmap = Bitmap.createScaledBitmap(bitmap, 1000, 1000, false);

                    //InputStream is = getContentResolver().openInputStream(data.getData());
                    //Bitmap bitmap = BitmapFactory.decodeStream(is);
                    //if(is != null) is.close();

                    Matrix matrix = new Matrix();
                    matrix.postRotate(90);
                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

                    System.out.println(imgView.getFixedWidth() + " " + imgView.getFixedWidth());

                    float imageRatio = ((float) bitmap.getHeight())/bitmap.getWidth();
                    bitmap = Bitmap.createScaledBitmap(bitmap, imgView.getFixedWidth(), Math.round(imgView.getFixedWidth()*imageRatio), false);

                    imgView.setImageBitmap(bitmap);

                    Log.d("USER VAR OUTPUT:::: ", "bitmap.getWidth() = " + bitmap.getWidth());
                    Log.d("USER VAR OUTPUT:::: ", "bitmap.getHeight() = " + bitmap.getHeight());
                    //Log.d("USER VAR OUTPUT:::: ", "imgViewWidth = " + imgViewWidth);
                    //Log.d("USER VAR OUTPUT:::: ", "imgViewHeight = " + imgViewHeight);
                    //Log.d("USER VAR OUTPUT:::: ", "scaledHeight = " + scaledHeight);

                    /*
                    TODO::
                        --check if width needs to be scaled as well
                        --check if img is in portrait view
                    */

                    //getContentResolver().delete(data.getData(), null, null);


//                }catch (FileNotFoundException e){
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                catch(Exception e){
//                    e.printStackTrace();
//                }



            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this,"Capture canceled", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this,"Capture error", Toast.LENGTH_LONG).show();
            }
        }
    }

    // Touch event listener
    public boolean imageTouchEvent(View v,MotionEvent e) {

        //TextView label = (TextView)findViewById(R.id.textView);
        float x = e.getX();
        float y = e.getY();

        //label.setText(String.format("x:%f y:%f",x,y));

        return true;
    }

    private static Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == 1){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else if(type == 2) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    public void scalePressed(View view){
        if(overlay.isEnabled && overlay.isScale){
            overlay.switchState();

            scaleButton.setChecked(false);
            objectButton.setChecked(false);
            heightButton.setEnabled(false);
            widthButton.setEnabled(false);
        }

        else {
            if (!overlay.isScale) overlay.switchSelection();
            if(!overlay.isEnabled) overlay.switchState();

            scaleButton.setChecked(true);
            objectButton.setChecked(false);
            heightButton.setEnabled(true);
            widthButton.setEnabled(true);
        }
    }

    public void objectPressed(View view){
        if(overlay.isEnabled && !overlay.isScale){
            overlay.switchState();

            scaleButton.setChecked(false);
            objectButton.setChecked(false);
            heightButton.setEnabled(false);
            widthButton.setEnabled(false);
        }

        else {
            if (overlay.isScale) overlay.switchSelection();
            if(!overlay.isEnabled) overlay.switchState();

            scaleButton.setChecked(false);
            objectButton.setChecked(true);
            heightButton.setEnabled(false);
            widthButton.setEnabled(false);
        }
    }



}
