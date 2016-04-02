package theperfectfit.sizedoesmatter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.media.ImageReader;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;



public class MainActivity extends ActionBarActivity {

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private drawState switchState;

    private double scaleLength;
    private double objectLength;

    private ImageView imgView;
    private int imgViewWidth;
    private int imgViewHeight;

    private Switch scaleSwitch;
    private TextView scaleSwitchText;


    private enum drawState {
        Scale,Object
    }

    // Constructor
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*switchState = ((Switch) findViewById(R.id.ScaleSwitch)).isChecked() ? drawState.Object : drawState.Scale;
        imgView = (ImageView) findViewById(R.id.MainImageView);
        scaleSwitch = (Switch) findViewById(R.id.ScaleSwitch);
        scaleSwitchText = (TextView) findViewById(R.id.ScaleSwitchText);

        scaleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                switchSelectionMode();
            }
        });*/

        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);



        try {

            CameraCharacteristics cameraCharacteristics =  manager.getCameraCharacteristics(manager.getCameraIdList()[0]);

            for(CameraCharacteristics.Key key : cameraCharacteristics.getKeys()){
                System.out.println(key.getName() + " " + cameraCharacteristics.get(key));
            }

        }catch (CameraAccessException e){System.out.println(e.getMessage());}

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

                try{
                    TouchImageView imgView = (TouchImageView)findViewById(R.id.MainImageView);
                    InputStream is = getContentResolver().openInputStream(data.getData());
                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                    is.close();

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

                    getContentResolver().delete(data.getData(), null, null);

                } catch (FileNotFoundException e){
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }


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

    public void switchSelectionMode(View view){
        touchOverlay overlay = (touchOverlay) findViewById(R.id.TouchOverlay);
        overlay.switchSelection();

        /*TextView scaleSwitchText = (TextView) findViewById(R.id.ScaleSwitchText);

        if(overlay.isScale) scaleSwitchText.setText("Scale");
        else scaleSwitchText.setText("Object");

        scaleSwitchText.invalidate();*/

    }



}
