package theperfectfit.sizedoesmatter;

import android.content.Context;
import android.graphics.*;
import android.graphics.Paint.Style;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.*;

import java.util.ArrayList;
import java.util.List;

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

class FloatPoint {
        float x;
        float y;

        public FloatPoint(float x, float y){
            this.x = x;
            this.y = y;
        }

        }