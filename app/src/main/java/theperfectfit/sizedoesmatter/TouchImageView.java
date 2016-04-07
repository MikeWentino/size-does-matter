package theperfectfit.sizedoesmatter;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

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
            found = true;
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




