package theperfectfit.sizedoesmatter;

import android.content.Context;
import android.graphics.*;
import android.graphics.Paint.Style;
import android.location.Location;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.*;

import java.util.ArrayList;
import java.util.List;

public class touchOverlay extends View {
    private final Paint pointPaint;
    private final Paint linePaint;

    private List<FloatPoint> points;
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

        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setStyle(Style.STROKE);
        linePaint.setColor(Color.GREEN);

        points = new ArrayList<>();

        currentPoint = null;
        touchDistance = null;
    }



    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(!calcDim){
            calcDim = true;
            width = canvas.getWidth();
            height = canvas.getHeight();

            points.add(new FloatPoint(width/4,height/4));
            points.add(new FloatPoint(width/4*3,height/4));
            points.add(new FloatPoint(width/4*3,height/4*3));
            points.add(new FloatPoint(width/4,height/4*3));
        }

        float prev_x = points.get(3).x;
        float prev_y = points.get(3).y;
        for(FloatPoint fp : points){
            canvas.drawCircle(fp.x,fp.y,10,pointPaint);

            canvas.drawLine(prev_x,prev_y,fp.x,fp.y,linePaint);

            prev_x = fp.x;
            prev_y = fp.y;
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

                invalidate();
                break;
            case MotionEvent.ACTION_UP:

                currentPoint.x = touch_x - touchDistance.x;
                currentPoint.y = touch_y - touchDistance.y;

                invalidate();
                break;
        }
        return true;
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