package com.example.design;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RouletteView extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder surfaceHolder;
    private Paint paint;
    private RectF rectF;
    private float sweepAngle;
    private List<String> items = new ArrayList<>();
    private int itemCount = 8;
    private int radius;
    private int centerX;
    private int centerY;
    private boolean isSpinning = false;
    private float currentRotation = 0;
    private float targetRotation = 0;
    private long animationStartTime;
    private long animationDuration = 3000;
    private OnRouletteResultListener mListener;

    public RouletteView(Context context) {
        super(context);
        init();
    }

    public RouletteView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RouletteView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        getHolder().addCallback(this);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        items.addAll(List.of("전주", "강릉", "서울", "부산", "대구", "여수", "인천", "천안"));
        sweepAngle = 360f / itemCount;
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        surfaceHolder = holder;
        drawRoulette();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        radius = Math.min(width, height) / 2 - 50;
        centerX = width / 2;
        centerY = height / 2;
        rectF = new RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
        drawRoulette();
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        surfaceHolder = null;
    }

    private void drawRoulette() {
        if (surfaceHolder == null || rectF == null) return;
        Canvas canvas = surfaceHolder.lockCanvas();
        if (canvas == null) return;

        canvas.drawColor(Color.WHITE);

        // 캔버스를 회전시켜 룰렛 전체를 돌린다
        canvas.save();
        canvas.rotate(currentRotation, centerX, centerY);

        float currentDrawAngle = 0;

        for (int i = 0; i < itemCount; i++) {
            paint.setColor(getColorByIndex(i));
            canvas.drawArc(rectF, currentDrawAngle, sweepAngle, true, paint);

            // 텍스트 그리기
            paint.setColor(Color.BLACK);
            paint.setTextSize(40);
            float textAngle = currentDrawAngle + sweepAngle / 2;
            float textRadius = radius * 0.7f;
            float x = centerX + textRadius * (float) Math.cos(Math.toRadians(textAngle));
            float y = centerY + textRadius * (float) Math.sin(Math.toRadians(textAngle)) + getTextHeight(paint) / 2;

            canvas.save();
            canvas.rotate(textAngle, x, y);
            canvas.drawText(items.get(i), x - paint.measureText(items.get(i)) / 2, y, paint);
            canvas.restore();

            currentDrawAngle += sweepAngle;
        }

        canvas.restore(); // 회전 해제

        // 고정된 화살표 그리기
        drawArrow(canvas);

        surfaceHolder.unlockCanvasAndPost(canvas);
    }


    private void drawArrow(Canvas canvas) {
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);

        float arrowWidth = 40f;
        float arrowHeight = 60f;

        float x = centerX;
        float y = centerY - radius - 20;
        float halfWidth = arrowWidth / 2;

        Path path = new Path();
        path.moveTo(x, y);
        path.lineTo(x - halfWidth, y - arrowHeight);
        path.lineTo(x + halfWidth, y - arrowHeight);
        path.close();

        canvas.drawPath(path, paint);
    }

    private int getColorByIndex(int index) {
        int[] colors = {Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.CYAN, Color.MAGENTA, Color.LTGRAY, Color.DKGRAY};
        return colors[index % colors.length];
    }

    private float getTextHeight(Paint paint) {
        Paint.FontMetrics fm = paint.getFontMetrics();
        return fm.descent - fm.ascent;
    }

    public void spin() {
        if (isSpinning) return;
        isSpinning = true;

        float startAngle = currentRotation % 360;
        Random random = new Random();
        targetRotation = currentRotation + 360 * 5 + random.nextInt(360); // 5바퀴 + 랜덤
        animationStartTime = System.currentTimeMillis();

        new Thread(() -> {
            while (System.currentTimeMillis() - animationStartTime < animationDuration) {
                long elapsed = System.currentTimeMillis() - animationStartTime;
                float progress = Math.min(1f, (float) elapsed / animationDuration);
                float interpolated = (float) (1 - Math.pow(1 - progress, 3));
                currentRotation = startAngle + (targetRotation - startAngle) * interpolated;

                post(this::drawRoulette);

                try {
                    Thread.sleep(16); // 약 60fps
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            currentRotation = targetRotation;
            post(() -> {
                drawRoulette();
                isSpinning = false;
                if (mListener != null) {
                    int resultIndex = getResultIndex(currentRotation);
                    mListener.onRouletteResult(items.get(resultIndex));
                }
            });
        }).start();
    }

    private int getResultIndex(float rotation) {
        float normalizedRotation = (rotation % 360 + 360) % 360;
        float degreePerItem = 360f / itemCount;
        float pointerAngle = 270f; // 12시 방향 (0도가 3시 방향이므로)

        // 룰렛의 멈춘 각도를 기준으로 12시 방향이 가리키는 아이템 계산
        float effectiveAngle = (pointerAngle - normalizedRotation + 360) % 360;
        int index = (int) (effectiveAngle / degreePerItem);
        return index;
    }


    public interface OnRouletteResultListener {
        void onRouletteResult(String result);
    }

    public void setOnRouletteResultListener(OnRouletteResultListener listener) {
        mListener = listener;
    }
}
