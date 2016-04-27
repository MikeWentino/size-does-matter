package theperfectfit.sizedoesmatter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.*;
import android.widget.ImageView;
import android.widget.Switch;

import Structs.FloatPoint;

public class TouchOverlay extends View {
    private final Paint pointPaint;
    private final Paint scaleLinePaint;
    private final Paint objectLinePaint;

    private FloatPoint[] points;
    private FloatPoint[] scalePoints;
    private FloatPoint[] objectPoints;

    public boolean isScale;
    public boolean isEnabled;
    private boolean calcDim = false;
    private float width;
    private float height;

    private FloatPoint currentPoint;
    private FloatPoint touchDistance;
    private FloatPoint ScaleSize;

    private ImageView zoomView;
    private Bitmap zoomMap;
    private Switch zoomSet;

    public TouchOverlay(Context context) {
        this(context, null);
    }

    public TouchOverlay(Context context, AttributeSet attrs) {
        super(context, attrs);
        //zoomView = (TouchImageView) R.
        // create colors for lines and circles
        pointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pointPaint.setStyle(Style.STROKE );
        pointPaint.setColor(Color.RED);

        scaleLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        scaleLinePaint.setStyle(Style.STROKE);
        scaleLinePaint.setColor(Color.GREEN);
        scaleLinePaint.setStrokeWidth(2);

        objectLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        objectLinePaint.setStyle(Style.STROKE);
        objectLinePaint.setColor(Color.BLUE);
        objectLinePaint.setStrokeWidth(2);

        // point arrays for scale/object
        scalePoints = new FloatPoint[4];
        objectPoints = new FloatPoint[4];

        isScale = true;
        isEnabled = true;

        // TransformationMatrix stuff
        ScaleSize = new FloatPoint(3.370,2.125);
        isScale = true;
        isEnabled = true;
        points = scalePoints;
        currentPoint = null;
        touchDistance = null;

    }
    public void setSwitch(Switch setS){
        zoomSet = setS;
    }


    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // runs once and sets the init locations
        if(!calcDim){
            calcDim = true;
            width = canvas.getWidth();
            height = canvas.getHeight();

            // set initial location of touch interface points
            scalePoints[0] = new FloatPoint(width/8,height/8);
            scalePoints[1] = new FloatPoint(width/8*3,height/8);
            scalePoints[2] = new FloatPoint(width/8*3,height/8*3);
            scalePoints[3] = new FloatPoint(width/8,height/8*3);

            objectPoints[0] = new FloatPoint(width/8*5,height/8*5);
            objectPoints[1] = new FloatPoint(width/8*7,height/8*5);
            objectPoints[2] = new FloatPoint(width/8*7,height/8*7);
            objectPoints[3] = new FloatPoint(width/8*5,height/8*7);
        }

        // draw interconnecting lines
        float prev_x = scalePoints[3].x;
        float prev_y = scalePoints[3].y;
        for(FloatPoint fp : scalePoints){
            canvas.drawLine(prev_x,prev_y,fp.x,fp.y, scaleLinePaint);

            prev_x = fp.x;
            prev_y = fp.y;
        }

        prev_x = objectPoints[3].x;
        prev_y = objectPoints[3].y;
        for(FloatPoint fp : objectPoints){
            canvas.drawLine(prev_x,prev_y,fp.x,fp.y, objectLinePaint);

            prev_x = fp.x;
            prev_y = fp.y;
        }

        // draw circles at corners
        if(isEnabled) {
            if (isScale)
                for (FloatPoint fp : scalePoints) canvas.drawCircle(fp.x, fp.y, 10, pointPaint);
            else
                for (FloatPoint fp : objectPoints) canvas.drawCircle(fp.x, fp.y, 10, pointPaint);
        }


    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {

        // ignore touch events when not enabled
        if(!isEnabled) return true;

        float touch_x = event.getX();
        float touch_y = event.getY();

        switch (event.getAction()) {

            // when touch interface is just pressed
            case MotionEvent.ACTION_DOWN:

                // select and move the closest point to the finger
                float minDistance = Float.MAX_VALUE;
                FloatPoint closestFp = null;
                for(FloatPoint fp : points){
                    float distance = (float) Math.sqrt(Math.pow(touch_x-fp.x,2) + Math.pow(touch_y-fp.y,2));

                    if(distance < minDistance){
                        minDistance = distance;
                        closestFp = fp;
                    }
                }

                currentPoint = closestFp;

                // save distance from point so it will remain static
                if (zoomSet.isChecked())
                    touchDistance = new FloatPoint(touch_x/2-currentPoint.x,touch_y/2-currentPoint.y);
                else
                    touchDistance = new FloatPoint(touch_x-currentPoint.x,touch_y-currentPoint.y);

                invalidate();
                break;

            // when the touch interface is dragged
            case MotionEvent.ACTION_MOVE:
                if (zoomSet.isChecked()) {
                    zoomView.setVisibility(View.VISIBLE);
                    currentPoint.x = touch_x / 2 - touchDistance.x;
                    currentPoint.y = touch_y / 2 - touchDistance.y;
                    if (currentPoint.x < 0) currentPoint.x = 0;
                    if (currentPoint.x > width) currentPoint.x = width;
                    if (currentPoint.y < 0) currentPoint.y = 0;
                    if (currentPoint.y > height) currentPoint.y = height;
                    getScaleBitmap(zoomMap, currentPoint.x, currentPoint.y);
                }
                else {
                    currentPoint.x = touch_x - touchDistance.x;
                    currentPoint.y = touch_y - touchDistance.y;
                    if (currentPoint.x < 0) currentPoint.x = 0;
                    if (currentPoint.x > width) currentPoint.x = width;
                    if (currentPoint.y < 0) currentPoint.y = 0;
                    if (currentPoint.y > height) currentPoint.y = height;
                }
                invalidate();
                //zoomView.setVisibility(View.INVISIBLE);

                break;
            case MotionEvent.ACTION_UP:
                zoomView.setVisibility(View.INVISIBLE);
        }
        return true;
    }
    public void setTouchImageView(ImageView touchView, Bitmap bMap) {
        zoomView = touchView;
        zoomMap = bMap;
    }
    private void setMap(Bitmap bMap){
        zoomView.setImageBitmap(bMap);
    }

    //Creates zoomed in bitmap on region user is currently measuring
    public void getScaleBitmap(Bitmap bitmap, Float x, Float y) {
        //calculate section of main bitmap to display in zoomed region
        int x2 = Math.round(bitmap.getWidth()/8);
        int y2 = Math.round(bitmap.getHeight()/8);
        int x1 = Math.round(x - (bitmap.getWidth()/(16)));
        int y1 = Math.round(y - (bitmap.getHeight()/(16)));
        if (x1 < 0)
            x1 = 0;
        if (y1 < 0)
            y1 = 0;
        if (x1 + x2 > bitmap.getWidth())
            x2 = bitmap.getWidth() - x1;
        if (y2 + y1 > bitmap.getHeight())
            y2 = bitmap.getHeight() - y1;
        Bitmap output = Bitmap.createBitmap(bitmap,x1 ,y1 ,x2 ,y2 );
        Paint nP = new Paint();
        nP.setStyle(Style.STROKE );
        nP.setColor(Color.BLACK);
        nP.setStrokeWidth(2);
        Canvas oCan = new Canvas(output);
        float sx0 = output.getWidth()/(2);
        float sy0 = output.getHeight()/(2);
        float sx1 = 4*output.getWidth()/(32);
        float sx2 = 15*output.getWidth()/(32);
        float sx3 = 17*output.getWidth()/(32);
        float sx4 = 28*output.getWidth()/(32);
        float sy1 = 8*output.getHeight()/(32);
        float sy2 = 15*output.getHeight()/(32);
        float sy3 = 17*output.getHeight()/(32);
        float sy4 = 24*output.getHeight()/(32);
        oCan.drawLine(sx1,sy0,sx2,sy0, nP);
        oCan.drawLine(sx3,sy0,sx4,sy0, nP);
        oCan.drawLine(sx0,sy1,sx0,sy2, nP);
        oCan.drawLine(sx0,sy3,sx0,sy4, nP);
        zoomView.setImageDrawable(new BitmapDrawable(getResources(),output));

    }
    // returns string of calculated dimensions
    public String calculateDimensions() {

        // get lengths of all sides
        float[] lf = MatrixFunctions.calculateSides(MatrixFunctions.transformPoints(ScaleSize, scalePoints, objectPoints));

        for(float f : lf)System.out.println(f);

        // average the width and height
        float avg_width = (lf[0] + lf[2]) / 2;
        float avg_height = (lf[1] + lf[3]) / 2;

        // return string representing the results
        return "Object Width: " + String.format("%.2f",avg_width) + "\nObject Height: " + String.format("%.2f", avg_height);
    }

    // switches touch selected between scale and object
    public void switchSelection(){
        isScale = !isScale;

        if(isScale){
            points = scalePoints;
        }
        else points = objectPoints;

        invalidate();

    }

    // enables or disables touch interface
    public void switchState(){
        isEnabled = !isEnabled;

        invalidate();
    }

}