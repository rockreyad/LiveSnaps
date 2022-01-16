package dev.narc.livesnap;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

public class Triangle extends ParticleView {
    private int w;
    private int h;
    private Paint paint;

    public Triangle(Context context) {
        super(context);
        init();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.w = w;
        this.h = h;
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Path path = new Path();
        path.moveTo(w / 2, 0);
        path.lineTo(w, h);
        path.lineTo(0, h);
        path.lineTo(w / 2, 0);

        canvas.drawPath(path, paint);
    }

    public void setPaintColor(int paintColor) {
        paint.setColor(paintColor);
        invalidate();
    }
}
