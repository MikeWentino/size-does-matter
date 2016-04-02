package theperfectfit.sizedoesmatter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.*;

import java.util.ArrayList;
import java.util.List;

import Jama.*;

public class touchOverlay extends View {
    private final Paint pointPaint;
    private final Paint scaleLinePaint;
    private final Paint objectLinePaint;

    private List<FloatPoint> points;
    private List<FloatPoint> scalePoints;
    private List<FloatPoint> objectPoints;
    public boolean isScale;
    private boolean calcDim = false;
    private float width;
    private float height;

    private FloatPoint currentPoint;
    private FloatPoint touchDistance;

    public touchOverlay(Context context) {
        this(context, null);
    }

    public touchOverlay(Context context, AttributeSet attrs) {
        super(context, attrs);
        pointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pointPaint.setStyle(Style.FILL);
        pointPaint.setColor(Color.RED);

        scaleLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        scaleLinePaint.setStyle(Style.STROKE);
        scaleLinePaint.setColor(Color.GREEN);
        scaleLinePaint.setStrokeWidth(5);

        objectLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        objectLinePaint.setStyle(Style.STROKE);
        objectLinePaint.setColor(Color.BLUE);
        objectLinePaint.setStrokeWidth(5);

        points = new ArrayList<>();
        scalePoints = new ArrayList<>();
        objectPoints = new ArrayList<>();
        isScale = true;

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

            scalePoints.add(new FloatPoint(width/8,height/8));
            scalePoints.add(new FloatPoint(width/8*3,height/8));
            scalePoints.add(new FloatPoint(width/8*3,height/8*3));
            scalePoints.add(new FloatPoint(width/8,height/8*3));

            objectPoints.add(new FloatPoint(width/8*5,height/8*5));
            objectPoints.add(new FloatPoint(width/8*7,height/8*5));
            objectPoints.add(new FloatPoint(width/8*7,height/8*7));
            objectPoints.add(new FloatPoint(width/8*5,height/8*7));
        }

        float prev_x = scalePoints.get(3).x;
        float prev_y = scalePoints.get(3).y;
        for(FloatPoint fp : scalePoints){
            canvas.drawLine(prev_x,prev_y,fp.x,fp.y, scaleLinePaint);

            prev_x = fp.x;
            prev_y = fp.y;
        }

        prev_x = objectPoints.get(3).x;
        prev_y = objectPoints.get(3).y;
        for(FloatPoint fp : objectPoints){
            canvas.drawLine(prev_x,prev_y,fp.x,fp.y, objectLinePaint);

            prev_x = fp.x;
            prev_y = fp.y;
        }

        // draw circles ontop of lines
        for(FloatPoint fp : scalePoints){
            if(isScale) canvas.drawCircle(fp.x,fp.y,10,pointPaint);
        }

        for(FloatPoint fp : objectPoints){
            if(!isScale) canvas.drawCircle(fp.x,fp.y,10,pointPaint);
        }


    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {

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
                //BEGIN TRANSFORMATION ATTEMPT
                FloatPoint ScaleSize = new FloatPoint(337.0f,212.5f);
                FloatPoint[] SkewedScale = {scalePoints.get(0),
                        scalePoints.get(1),
                        scalePoints.get(2),
                        scalePoints.get(3)};

                FloatPoint[] NormalizedScale = {new FloatPoint(SkewedScale[0].x,SkewedScale[0].y),
                        new FloatPoint(SkewedScale[0].x+ScaleSize.y,SkewedScale[0].y),
                        new FloatPoint(SkewedScale[0].x+ScaleSize.y,SkewedScale[0].y+ScaleSize.x),
                        new FloatPoint(SkewedScale[0].x,SkewedScale[0].y+ScaleSize.x)};

                MatrixFunctions.estimate(SkewedScale, NormalizedScale);
                Matrix TransformMatrix = MatrixFunctions.findProjectiveMatrix(SkewedScale, NormalizedScale);

                //START VISUAL ATTEMPT
                double[][] p1Temp = {{scalePoints.get(0).x},{scalePoints.get(0).y},{1.0}};
                Jama.Matrix p1Matrix = TransformMatrix.times(new Jama.Matrix(p1Temp));
                FloatPoint newp1 = new FloatPoint(p1Matrix.get(0,0), p1Matrix.get(1,0));
                objectPoints.set(0, newp1);

                double[][] p2Temp = {{scalePoints.get(1).x},{scalePoints.get(1).y},{1.0}};
                Jama.Matrix p2Matrix = TransformMatrix.times(new Jama.Matrix(p2Temp));
                FloatPoint newp2 = new FloatPoint(p2Matrix.get(0,0), p2Matrix.get(1,0));
                objectPoints.set(1, newp2);

                double[][] p3Temp = {{scalePoints.get(2).x},{scalePoints.get(2).y},{1.0}};
                Jama.Matrix p3Matrix = TransformMatrix.times(new Jama.Matrix(p3Temp));
                FloatPoint newp3 = new FloatPoint(p3Matrix.get(0,0), p3Matrix.get(1,0));
                objectPoints.set(2, newp3);

                double[][] p4Temp = {{scalePoints.get(3).x},{scalePoints.get(3).y},{1.0}};
                Jama.Matrix p4Matrix = TransformMatrix.times(new Jama.Matrix(p4Temp));
                FloatPoint newp4 = new FloatPoint(p4Matrix.get(0,0), p4Matrix.get(1,0));
                objectPoints.set(3, newp4);

                invalidate();


                //END TRANSFORMATION ATTEMPT


                break;
        }
        return true;
    }

    public void switchSelection(){

        isScale = !isScale;

        if(isScale){
            points = scalePoints;
        }
        else points = objectPoints;

        invalidate();

    }

}