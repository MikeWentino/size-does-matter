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

        points = new FloatPoint[4];
        scalePoints = new FloatPoint[4];
        objectPoints = new FloatPoint[4];

        isScale = true;
        isEnabled = true;

        //TransformationMatrix stuff

        ScaleSize = new FloatPoint(3.370,2.125);


        ScaleSize = new FloatPoint(3.370,2.125);
        isScale = true;
        isEnabled = true;


        points = scalePoints;

        currentPoint = null;
        touchDistance = null;
    }



    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(!calcDim){
            calcDim = true;
            width = canvas.getWidth();
            height = canvas.getHeight();

            scalePoints[0] = new FloatPoint(width/8,height/8);
            scalePoints[1] = new FloatPoint(width/8*3,height/8);
            scalePoints[2] = new FloatPoint(width/8*3,height/8*3);
            scalePoints[3] = new FloatPoint(width/8,height/8*3);

            objectPoints[0] = new FloatPoint(width/8*5,height/8*5);
            objectPoints[1] = new FloatPoint(width/8*7,height/8*5);
            objectPoints[2] = new FloatPoint(width/8*7,height/8*7);
            objectPoints[3] = new FloatPoint(width/8*5,height/8*7);
        }

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

        // draw circles ontop of lines
        if(isEnabled) {
            if (isScale)
                for (FloatPoint fp : scalePoints) canvas.drawCircle(fp.x, fp.y, 10, pointPaint);
            else for (FloatPoint fp : objectPoints) canvas.drawCircle(fp.x, fp.y, 10, pointPaint);
        }


    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {

        // ignore touch events when not enabled
        if(!isEnabled) return true;

        float touch_x = event.getX();
        float touch_y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

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
                touchDistance = new FloatPoint(touch_x-currentPoint.x,touch_y-currentPoint.y);

                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:

                currentPoint.x = touch_x - touchDistance.x;
                currentPoint.y = touch_y - touchDistance.y;

                if(currentPoint.x < 0) currentPoint.x = 0;
                if(currentPoint.x > width) currentPoint.x = width;
                if(currentPoint.y < 0) currentPoint.y = 0;
                if(currentPoint.y > height) currentPoint.y = height;

                invalidate();

                break;
        }
        return true;
    }


//    public FloatPoint[] calculateDimensions() {
//        FloatPoint[] transformedPoints = MatrixFunctions.transformPoints(ScaleSize, scalePoints, objectPoints);
//
//        //Print out estimated dimensions
//        String dimensionPrint = "THE DIMENSIONS: " + MatrixFunctions.distance(transformedPoints[3], transformedPoints[0]);
//        FloatPoint beginPoint = transformedPoints[0];
//        for (int i = 1; i < 4; i++) {
//            FloatPoint endPoint = transformedPoints[i];
//            dimensionPrint = dimensionPrint + " x " + MatrixFunctions.distance(beginPoint, endPoint);
//            beginPoint = endPoint;
//        }
//        System.out.println(dimensionPrint);
//
//        return transformedPoints;
//
//    }

    public String calculateDimensions() {

        float[] lf = MatrixFunctions.calculateSides(MatrixFunctions.transformPoints(ScaleSize, scalePoints, objectPoints));

        return "Object Width: " + String.format("%.2f",lf[0]) + "\nObject Height: " + String.format("%.2f", lf[1]);
        //END TRANSFORMATION ATTEMPT

    }

    public void switchSelection(){
        isScale = !isScale;

        if(isScale){
            points = scalePoints;
        }
        else points = objectPoints;

        invalidate();

    }

    public void switchState(){
        isEnabled = !isEnabled;

        invalidate();
    }

}