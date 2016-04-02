package theperfectfit.sizedoesmatter;

import android.graphics.Bitmap;

/**
 * Created by Joseph on 4/1/2016.
 */
public class currentImage {
    private  Bitmap imageBit;
    private static currentImage ourInstance = new currentImage();

    public static currentImage getInstance() {
        return ourInstance;
    }

    public void setBit(Bitmap iBit) {
        imageBit = iBit;
    }
    public Bitmap getBit(){
        Bitmap temp = imageBit;
        return temp;
    }
    private currentImage() {
        imageBit = null;
    }
}
