package Singletons;

import android.graphics.Bitmap;

public class CurrentImage {
    private  Bitmap imageBit;
    private static CurrentImage ourInstance = new CurrentImage();

    public static CurrentImage getInstance() {
        return ourInstance;
    }

    public void setBit(Bitmap iBit) {
        imageBit = iBit;
    }
    public Bitmap getBit(){
        Bitmap temp = imageBit;
        return temp;
    }
    private CurrentImage() {
        imageBit = null;
    }
}
