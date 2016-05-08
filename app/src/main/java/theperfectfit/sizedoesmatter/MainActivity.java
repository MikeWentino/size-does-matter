package theperfectfit.sizedoesmatter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;
import Structs.FloatPoint;

import Singletons.CurrentImage;
import Structs.FloatPoint;

public class MainActivity extends ActionBarActivity {
    public TouchOverlay overlay;
    TextView mTextView;
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        overlay = (TouchOverlay) findViewById(R.id.TouchOverlay);

        setSupportActionBar(mToolbar);
        mToolbar.setOnMenuItemClickListener(new ToolBarListener(this));



        //builder.show();
    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void switchSelectionMode(){
        TouchOverlay overlay = (TouchOverlay) findViewById(R.id.TouchOverlay);
        overlay.switchSelection();
    }
}

class ToolBarListener implements Toolbar.OnMenuItemClickListener{
    MainActivity mainActivity;

    public ToolBarListener(MainActivity mainActivity) {
        super();
        this.mainActivity = mainActivity;
    }

    // receives toolbar click input
    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {

            // photo button
            case R.id.take_picture:
                FragmentManager fragmentManager = mainActivity.getSupportFragmentManager();
                fragmentManager.popBackStack();

                CameraFragment newFragment = new CameraFragment();

                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, newFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                fragmentManager.addOnBackStackChangedListener(new BackStackListener(mainActivity));
                break;

            // scale button
            case R.id.choose_scale:
                mainActivity.switchSelectionMode();
                break;

            // calculator button
            case R.id.calculate:
                mainActivity.overlay.calculateDimensions();
                mainActivity.mTextView = (TextView) mainActivity.findViewById(R.id.textView);
                mainActivity.mTextView.setText(mainActivity.overlay.calculateDimensions());
                break;

            // save button
            case R.id.save:
                SaveMeasurements.saveTouchImage(mainActivity.getWindow(),mainActivity);
                Toast.makeText(mainActivity.getApplicationContext(),"Image saved",Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_tutorial:


                mainActivity.startActivity(new Intent(mainActivity, TutorialActivity.class));
                break;
            // size select menu
            case R.id.action_sizes:
                CharSequence colors[] = new CharSequence[] {"8.5\"x11\" DEFAULT: piece of paper (portrait)", "2.125\"x3.370\" credit card (portrait)",
                        "11\"x8.5\" piece of paper (landscape)", "3.370\"x2.125\" credit card (landscape)"};

                AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
                builder.setTitle("Pick a scale size");
                builder.setItems(colors, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which==0) {
                            mainActivity.overlay.ScaleSize = new FloatPoint(11,8.5);
                            System.out.println("PAPER portrait");
                            Toast.makeText(mainActivity.getApplicationContext(),"Scale changed to paper portrait",Toast.LENGTH_SHORT).show();
                        } else if(which==1) {
                            mainActivity.overlay.ScaleSize = new FloatPoint(3.370,2.125);
                            System.out.println("CREDITCARD portait");
                            Toast.makeText(mainActivity.getApplicationContext(),"Scale changed to credit card portrait",Toast.LENGTH_SHORT).show();
                        } else if(which==2) {
                            mainActivity.overlay.ScaleSize = new FloatPoint(8.5,11);
                            System.out.println("PAPER landscape");
                            Toast.makeText(mainActivity.getApplicationContext(),"Scale changed to paper landscape",Toast.LENGTH_SHORT).show();
                        } else if(which==3) {
                            mainActivity.overlay.ScaleSize = new FloatPoint(2.125,3.370);
                            System.out.println("CREDITCARD landscape");
                            Toast.makeText(mainActivity.getApplicationContext(),"Scale changed to credit card landscape",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.show();

            // button has not been mapped
            default : return false;
        }

        return true;
    }
}

// camera image receiver
class BackStackListener implements FragmentManager.OnBackStackChangedListener {
    MainActivity mainActivity;

    public BackStackListener(MainActivity mainActivity){
        super();
        this.mainActivity = mainActivity;
    }

    // event when image is taken from camera
    @Override
    public void onBackStackChanged() {
        TouchImageView imgView = (TouchImageView) mainActivity.findViewById(R.id.MainImageView);
        TouchOverlay overlay = (TouchOverlay) mainActivity.findViewById(R.id.TouchOverlay);
        ImageView imgView2 = (ImageView) mainActivity.findViewById(R.id.ZoomImageView);
        Switch setS = (Switch) mainActivity.findViewById(R.id.switch1);
        Bitmap bitmap = CurrentImage.getInstance().getBit();
        if (bitmap != null) {
            System.out.println(imgView.getFixedWidth() + " and " + imgView.getFixedWidth());

            float imageRatio = ((float) bitmap.getHeight()) / bitmap.getWidth();

            // create temp bitmap to be used for display
            Bitmap temp_bitmap = Bitmap.createScaledBitmap(bitmap, imgView.getFixedWidth(), Math.round(imgView.getFixedWidth() * imageRatio), false);

            // scale to imgview dimensions
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(temp_bitmap, temp_bitmap.getWidth(), temp_bitmap.getHeight(), true);

            // rotate 90 degrees
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
            Bitmap finBmap = Bitmap.createScaledBitmap(rotatedBitmap,imgView.getWidth(),imgView.getHeight(),true);
            // set imgview to use temp bitmap
            imgView.setImageBitmap(finBmap);
            overlay.setTouchImageView(imgView2, finBmap);
            setS.setVisibility(View.VISIBLE);
            overlay.setSwitch(setS);
        }
    }
}
