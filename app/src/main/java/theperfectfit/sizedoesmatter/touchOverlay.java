package theperfectfit.sizedoesmatter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.*;

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

    public TouchOverlay(Context context) {
        this(context, null);
    }

    public TouchOverlay(Context context, AttributeSet attrs) {
        super(context, attrs);

        // create colors for lines and circles
        pointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pointPaint.setStyle(Style.STROKE);
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
                touchDistance = new FloatPoint(touch_x-currentPoint.x,touch_y-currentPoint.y);

                invalidate();
                break;

            // when the touch interface is dragged
            case MotionEvent.ACTION_MOVE:
                currentPoint.x = touch_x - touchDistance.x;
                currentPoint.y = touch_y - touchDistance.y;

                // prevent points from going out of bounds
                if(currentPoint.x < 0) currentPoint.x = 0;
                if(currentPoint.x > width) currentPoint.x = width;
                if(currentPoint.y < 0) currentPoint.y = 0;
                if(currentPoint.y > height) currentPoint.y = height;

                invalidate();
                break;
        }
        return true;
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