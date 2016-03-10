package theperfectfit.sizedoesmatter;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

/**
 * Created by Mikes Gaming on 3/9/2016.
 */
public class TouchImageView extends ImageView{

    private int width;
    private int height;
    private boolean found = false;

    public TouchImageView(Context context, AttributeSet attrs){
        super(context, attrs);


    }

    @Override
    public void onWindowFocusChanged(boolean focus){
        super.onWindowFocusChanged(focus);

        if(!found){
            width = getWidth();
            height = getHeight();
        }
    }

    public int getFixedWidth(){
        return width;
    }

    public int getFixedHeight(){
        return height;
    }




}




