package theperfectfit.sizedoesmatter;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;


public class MainActivity extends ActionBarActivity {

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private drawState switchState;

    private double scaleLength;
    private double objectLength;

    private int imgViewWidth;
    private int imgViewHeight;


    private enum drawState {
        Scale,Object
    }

    // Constructor
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        switchState = ((Switch) findViewById(R.id.switch1)).isChecked() ? drawState.Object : drawState.Scale;

        imgViewWidth = -1;
        imgViewHeight = -1;

        Log.w("IMG_DEBUG", "ONCREATE imgViewWidth: "+imgViewWidth);
        Log.w("IMG_DEBUG", "ONCREATE imgViewHeight: " +imgViewHeight);

        //findViewById(R.id.imageView).setOnTouchListener((View v, MotionEvent e) -> imageTouchEvent(v,e));
        //findViewById(R.id.imageView).setOnTouchListener(this::imageTouchEvent);

    }

    @Override
    protected void onStart() {
        int imgViewWidthTemp = imgViewWidth;
        int imgViewHeightTemp = imgViewHeight;
        super.onStart();
        imgViewWidth = imgViewWidthTemp;
        imgViewHeight = imgViewHeightTemp;
    }
    @Override
    protected void onPause() {
        int imgViewWidthTemp = imgViewWidth;
        int imgViewHeightTemp = imgViewHeight;
        super.onPause();
        imgViewWidth = imgViewWidthTemp;
        imgViewHeight = imgViewHeightTemp;
    }

    @Override
    protected void onStop() {
        int imgViewWidthTemp = imgViewWidth;
        int imgViewHeightTemp = imgViewHeight;
        super.onStop();
        imgViewWidth = imgViewWidthTemp;
        imgViewHeight = imgViewHeightTemp;
    }

    @Override
    protected void onRestart() {
        int imgViewWidthTemp = imgViewWidth;
        int imgViewHeightTemp = imgViewHeight;
        super.onRestart();
        imgViewWidth = imgViewWidthTemp;
        imgViewHeight = imgViewHeightTemp;
    }

    /*
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        // TODO Auto-generated method stub
        super.onWindowFocusChanged(hasFocus);

        ImageView imgView = (ImageView) findViewById(R.id.imageView);
        if(imgViewWidth==0)
            imgViewWidth = imgView.getWidth();
        if(imgViewHeight==0)
            imgViewHeight = imgView.getHeight();
        Log.w("IMG_DEBUG", "METHOD width : " + imgView.getWidth());
        Log.w("IMG_DEBUG", "METHOD height : " + imgView.getHeight());

    }
    */

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
                    ImageView imgView = (ImageView)findViewById(R.id.imageView);
                    InputStream is = getContentResolver().openInputStream(data.getData());
                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                    is.close();
                    RelativeLayout theLayout = (RelativeLayout) findViewById(R.id.the_layout);

                    //Debug output
                    Log.w("IMG_DEBUG", "theLayout.getWidth(): "+theLayout.getWidth());
                    Log.w("IMG_DEBUG", "theLayout.getHeight(): " + theLayout.getHeight());
                    Log.w("IMG_DEBUG", "imgView.getPaddingBottom(): "+imgView.getPaddingBottom());
                    Log.w("IMG_DEBUG", "imgView.getPaddingTop(): "+imgView.getPaddingTop());
                    Log.w("IMG_DEBUG", "imgView.getPaddingLeft(): "+imgView.getPaddingLeft());
                    Log.w("IMG_DEBUG", "imgView.getPaddingRight(): "+imgView.getPaddingRight());
                    Log.w("IMG_DEBUG", "imgView.getMeasuredHeight(): "+imgView.getMeasuredHeight());
                    //Log.w("IMG_DEBUG", "imgView.getWidth(): "+imgView.getParent());
                    Log.w("IMG_DEBUG", "imgView.getWidth(): "+imgView.getWidth());
                    Log.w("IMG_DEBUG", "imgView.getHeight(): "+imgView.getHeight());
                    Log.w("IMG_DEBUG", "imgView.getMaxWidth(): "+imgView.getMaxWidth());
                    Log.w("IMG_DEBUG", "imgView.getMaxHeight(): "+imgView.getMaxHeight());
                    Log.w("IMG_DEBUG", "bitmap.getWidth(): "+bitmap.getWidth());
                    Log.w("IMG_DEBUG", "bitmap.getHeight(): " +bitmap.getHeight());

                    //loads original image (still need to put an original image into resources)

                    if(imgViewWidth==-1 && imgViewHeight==-1) {
                        bitmap = Bitmap.createScaledBitmap(bitmap, 1000, 1000, false);
                        imgView.setImageBitmap(bitmap);
                        imgViewWidth = imgView.getWidth();
                        imgViewHeight = imgView.getHeight();
                        Log.w("IMG_DEBUG", "CHANGING IMGVIEW DATA: ");
                    }


                    Log.w("IMG_DEBUG", "imgViewWidth: "+imgViewWidth);
                    Log.w("IMG_DEBUG", "imgViewHeight: " +imgViewHeight);

                    //loads attempted scaled image (not resizable yet)
                    int scaledHeight = (int) ((bitmap.getHeight()*1.0)/(bitmap.getWidth()*1.0/imgViewWidth*1.0));

                    Log.w("IMG_DEBUG", "scaledHeight: "+scaledHeight);

                    Bitmap bitmap2 = Bitmap.createScaledBitmap(bitmap,imgViewWidth,scaledHeight,false );
                    imgView.setImageBitmap(bitmap2);

                }catch (FileNotFoundException e){
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

    // Changes drawing state
    public void switchStateChange(View v){
        switchState = ((Switch)v).isChecked() ? drawState.Object : drawState.Scale;
        TextView label = (TextView)findViewById(R.id.textView2);

        if(switchState == drawState.Object) label.setText("Object");
        else label.setText("Scale");
    }

    // Touch event listener
    public boolean imageTouchEvent(View v,MotionEvent e) {

        TextView label = (TextView)findViewById(R.id.textView);
        float x = e.getX();
        float y = e.getY();

        label.setText(String.format("x:%f y:%f",x,y));

        return true;
    }



}
