package theperfectfit.sizedoesmatter;

/**
 * Created by Weldon on 4/1/2016.
 */
public class FloatPoint {
    public float x, y;

    public FloatPoint(float X, float Y) {
        x = X;
        y = Y;
    }

    public FloatPoint(double X, double Y) {
        x = (float)X;
        y = (float)Y;
    }

    public FloatPoint(int X, int Y) {
        x = X;
        y = Y;
    }
}
