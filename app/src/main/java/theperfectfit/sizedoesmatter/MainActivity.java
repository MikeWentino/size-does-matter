package theperfectfit.sizedoesmatter;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;

import Singletons.CurrentImage;

public class MainActivity extends ActionBarActivity {
    TouchOverlay overlay;
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

            // set imgview to use temp bitmap
            imgView.setImageBitmap(rotatedBitmap);
        }
    }
}
