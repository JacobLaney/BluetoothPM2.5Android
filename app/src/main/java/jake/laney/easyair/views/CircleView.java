package jake.laney.easyair.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by JakeL on 10/15/17.
 */

/*
 * View that displays a colored circle that changes color according to pm value
 */
public class CircleView extends View {

    private RectF circleRect;
    private Paint circlePaint;
    private final int STROKE_WIDTH = 10;
    private int bgColor;

    public CircleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        circleRect = new RectF(0, 0, 2000, 2000);

        circlePaint = new Paint();
        circlePaint.setAntiAlias(true);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(STROKE_WIDTH);
        bgColor = Color.rgb(0, 200, 0);
        circlePaint.setColor(bgColor);
    }

    public void setColor(int color) {
        circlePaint.setColor(color);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawArc(circleRect, 0, 360, true, circlePaint);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        circleRect.set(STROKE_WIDTH, STROKE_WIDTH, right - left - STROKE_WIDTH, bottom - top - STROKE_WIDTH);
    }

    // get the color for the pm value
    private int getColor(int value) {
        int c;
        if (value <= 50) {
            c = Color.rgb(0, 255, 0);
        }
        else if (value <= 100) {
            c = Color.rgb(255, 255, 0);
        }
        else if (value <= 150) {
           c = Color.rgb(255, 170, 0);
        }
        else if (value <= 200) {
            c = Color.rgb(255, 0, 0);
        }
        else if (value <= 300) {
            c = Color.rgb(255, 0, 255);
        }
        else {
            c = Color.rgb(120, 0, 0);
        }
        return c;
    }

    public void	adjustBackground(int value) {;
        bgColor = getColor(value);
        this.setColor(bgColor);
        this.invalidate();
    }
}
