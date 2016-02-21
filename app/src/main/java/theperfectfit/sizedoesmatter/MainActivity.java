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

    private enum drawState {
        Scale,Object
    }

    // Constructor
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        switchState = ((Switch) findViewById(R.id.switch1)).isChecked() ? drawState.Object : drawState.Scale;

        //findViewById(R.id.imageView).setOnTouchListener((View v, MotionEvent e) -> imageTouchEvent(v,e));
        //findViewById(R.id.imageView).setOnTouchListener(this::imageTouchEvent);

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
                    ImageView imgView = (ImageView)findViewById(R.id.imageView);
                    InputStream is = getContentResolver().openInputStream(data.getData());
                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                    Log.w("imgDebug", "imgView.getWidth(): "+imgView.getMeasuredWidth());
                    Log.w("imgDebug", "bitmap.getWidth(): "+bitmap.getWidth());
                    Log.w("imgDebug", "bitmap.getHeight(): " +bitmap.getHeight());
                    int scaledHeight = (int) ((bitmap.getHeight()*1.0)/(bitmap.getWidth()*1.0/imgView.getMeasuredWidth()*1.0));
                    bitmap = Bitmap.createScaledBitmap(bitmap,imgView.getMeasuredWidth(),scaledHeight,false );
                    is.close();


                    imgView.setImageBitmap(bitmap);
                    //imgView.setBackground(Drawable.createFromStream(stream,data.getData().toString()));

                }catch (FileNotFoundException e){} catch (IOException e) {
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
