package com.bill.chart;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import com.bill.BuildConfig;
import com.bill.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BrokenLineView extends View implements Runnable {
    private static final float DEFAULT_MAX_ROW_VALUE = 100f;
    private static final int CIRCLE_COLOR = R.color.broken_line_view_circle;
    private static final int CIRCLE_RADIUS = 20;
    private static final int TITLE_COLUMN_COLOR = R.color.broken_line_view_value_title;
    private static final int TITLE_COLUMN_STROKE_WIDTH = 3;
    private static final int TITLE_COLUMN_TEXT_SIZE = 40;
    private static final int LINE_STROKE_WIDTH = 16;
    private static final int LINE_COLOR = R.color.broken_line_view_line;
    private static final int DISPLAY_ROW_COUNT = 6;
    private static final int ANIMATION_TOTAL_TIME = 500;
    private static final int ANIMATION_INTERVAL = 25;

    private Paint mCirclePaint;
    private Paint mTitlePaint;
    private Paint mLinePaint;

    private int mCircleColor = getResources().getColor(CIRCLE_COLOR);
    private float mCircleRadius = CIRCLE_RADIUS;

    private int mTitleColumnColor = getResources().getColor(TITLE_COLUMN_COLOR);
    private float mTitleColumnStrokeWidth = TITLE_COLUMN_STROKE_WIDTH;
    private float mTitleColumnTextSize = TITLE_COLUMN_TEXT_SIZE;

    private float mLineStrokeWidth = LINE_STROKE_WIDTH;
    private int mLineColor = getResources().getColor(LINE_COLOR);

    private int mDisplayRowCount = DISPLAY_ROW_COUNT;

    private int mAnimationTotalTime = ANIMATION_TOTAL_TIME;
    private int mAnimationInterval = ANIMATION_INTERVAL;

    private List<GeneralDrawInfo> mRowLineGeneralDrawInfoList;
    private List<GeneralDrawInfo> mRowTitleGeneralDrawInfoList;
    private List<GeneralDrawInfo> mColumnTitleGeneralDrawInfoList;
    private List<AnimationDrawInfo> mColumnLineAnimationDrawInfoList;
    private List<AnimationDrawInfo> mColumnCircleAnimationDrawInfoList;
    private final List<Float> mValueColumns = new ArrayList<>();
    private final List<Float> mOriginalValueColumns = new ArrayList<>();
    private List<String> mTitleColumns;
    private int mCurrentAnimationIntervalIndex;
    private int mWidth;
    private int mHeight;

    public BrokenLineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public BrokenLineView(Context context) {
        super(context);
        init(context, null);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        run();
    }

    @Override
    public void onDraw(Canvas canvas) {
        for (GeneralDrawInfo generalDrawInfo : mRowLineGeneralDrawInfoList) {
            canvas.drawLine(generalDrawInfo.getStartX(), generalDrawInfo.getStartY(), generalDrawInfo.getStopX(), generalDrawInfo.getStopY(), mTitlePaint);
        }

        for (GeneralDrawInfo generalDrawInfo : mRowTitleGeneralDrawInfoList) {
            canvas.drawText(generalDrawInfo.getText(), generalDrawInfo.getX(), generalDrawInfo.getY(), mTitlePaint);
        }

        for (GeneralDrawInfo generalDrawInfo : mColumnTitleGeneralDrawInfoList) {
            canvas.drawText(generalDrawInfo.getText(), generalDrawInfo.getX(), generalDrawInfo.getY(), mTitlePaint);
        }

        for (AnimationDrawInfo animationDrawInfo : mColumnLineAnimationDrawInfoList) {
            canvas.drawLine(animationDrawInfo.getStartX(), animationDrawInfo.getStartY(), animationDrawInfo.getStopX(), animationDrawInfo.getStopY(), mLinePaint);
        }

        for (AnimationDrawInfo animationDrawInfo : mColumnCircleAnimationDrawInfoList) {
            canvas.drawCircle(animationDrawInfo.getStopX(), animationDrawInfo.getStopY(), mCircleRadius, mCirclePaint);
        }
    }

    @Override
    public void run() {
        if (mCurrentAnimationIntervalIndex >= getAnimationTotalInterval()) {
            return;
        }

        if (mCurrentAnimationIntervalIndex == 0) {
            initData();
            setGeneralDrawInfo(mWidth, mHeight);
        }

        setColumnDrawInfo(mWidth, mHeight);
        mCurrentAnimationIntervalIndex++;
        invalidate();
        postDelayed(this, mAnimationInterval);
    }

    private void init(Context context, AttributeSet attrs) {
        // 解決4.1版本 以下canvas.drawTextOnPath()不顯示問題
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        initAttribute(context, attrs);

        mCirclePaint = new Paint();
        mCirclePaint.setColor(mCircleColor);
        mCirclePaint.setStyle(Paint.Style.FILL);
        mCirclePaint.setAntiAlias(true);

        mTitlePaint = new Paint();
        mTitlePaint.setColor(mTitleColumnColor);
        mTitlePaint.setStrokeWidth(mTitleColumnStrokeWidth);
        mTitlePaint.setTextSize(mTitleColumnTextSize);
        mTitlePaint.setTypeface(Typeface.DEFAULT_BOLD);
        mTitlePaint.setAntiAlias(true);

        mLinePaint = new Paint();
        mLinePaint.setColor(mLineColor);
        mLinePaint.setStyle(Paint.Style.FILL);
        mLinePaint.setStrokeWidth(mLineStrokeWidth);
        mLinePaint.setAntiAlias(true);

        initData();
        if (BuildConfig.DEBUG) {
            setDemoValue();
        }
    }

    private void initAttribute(Context context, AttributeSet attrs) {
        if (context == null || attrs == null) {
            return;
        }

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BrokenLineView, 0, 0);

        mCircleColor = typedArray.getColor(R.styleable.BrokenLineView_circleColor, getResources().getColor(CIRCLE_COLOR));
        mCircleRadius = typedArray.getDimension(R.styleable.BrokenLineView_circleRadius, CIRCLE_RADIUS);

        mTitleColumnColor = typedArray.getColor(R.styleable.BrokenLineView_titleColumnColor, getResources().getColor(TITLE_COLUMN_COLOR));

        mTitleColumnStrokeWidth = typedArray.getDimension(R.styleable.BrokenLineView_titleColumnStrokeWidth, TITLE_COLUMN_STROKE_WIDTH);
        mTitleColumnTextSize = typedArray.getDimension(R.styleable.BrokenLineView_titleColumnTextSize, TITLE_COLUMN_TEXT_SIZE);

        mLineStrokeWidth = typedArray.getDimension(R.styleable.BrokenLineView_lineStrokeWidth, LINE_STROKE_WIDTH);
        mLineColor = typedArray.getColor(R.styleable.BrokenLineView_lineColor, getResources().getColor(LINE_COLOR));

        mDisplayRowCount = typedArray.getInteger(R.styleable.BrokenLineView_displayRowCount, DISPLAY_ROW_COUNT);

        mAnimationTotalTime = typedArray.getInteger(R.styleable.BrokenLineView_animationTotalTime, ANIMATION_TOTAL_TIME);
        mAnimationInterval = typedArray.getInteger(R.styleable.BrokenLineView_animationInterval, ANIMATION_INTERVAL);

        typedArray.recycle();
    }

    // row標識線、名稱，column名稱
    private void setGeneralDrawInfo(int width, int height) {
        float maxRowValue = getMaxRowValue(mValueColumns);
        int rowSpace = getRowSpace(height);
        int columnLeftX = getColumnLeftX(width);
        int rowLineBottomY = getRowLineBottomY(height);

        for (int i = 0; i < mDisplayRowCount; i++) {
            float rowY = rowLineBottomY - i * rowSpace;
            mRowLineGeneralDrawInfoList.get(i)
                    .setStartX(columnLeftX)
                    .setStartY(rowY)
                    .setStopX(width)
                    .setStopY(rowY);

            mRowTitleGeneralDrawInfoList.get(i)
                    .setText(((int) (maxRowValue * i / (mDisplayRowCount - 1))) + "")
                    .setX(width * 0.02f)
                    .setY(rowY);
        }

        int columnSpace = getColumnSpace(width);
        for (int i = 0; i < mValueColumns.size(); i++) {
            mColumnTitleGeneralDrawInfoList.get(i)
                    .setText(mTitleColumns.get(i))
                    .setX(getTargetColumnX(width, columnLeftX, columnSpace, i))
                    .setY(height * 0.95f);
        }
    }

    // column折線、圓點
    private void setColumnDrawInfo(int width, int height) {
        if (mValueColumns.size() == 0) {
            return;
        }
        float stopColumnY, stopColumnX;
        float startColumnX = 0, startColumnY = 0;
        int rowLineTopY = getRowLineTopY(height);
        int rowLineBottomY = getRowLineBottomY(height);
        int columnLeftX = getColumnLeftX(width);
        int columnSpace = getColumnSpace(width);
        for (int i = 0; i < mValueColumns.size(); i++) {
            float originalY = rowLineTopY + (1 - (mOriginalValueColumns.get(i) / getMaxRowValue(mOriginalValueColumns))) * (rowLineBottomY - rowLineTopY);
            float targetY = rowLineTopY + (1 - (mValueColumns.get(i) / getMaxRowValue(mValueColumns))) * (rowLineBottomY - rowLineTopY);
            float distance = (targetY - originalY) / getAnimationTotalInterval() * (mCurrentAnimationIntervalIndex + 1);

            stopColumnX = getTargetColumnX(width, columnLeftX, columnSpace, i);
            stopColumnY = originalY + distance;

            if (i > 0) {
                mColumnLineAnimationDrawInfoList.get(i)
                        .setStartX(startColumnX)
                        .setStartY(startColumnY)
                        .setStopX(stopColumnX)
                        .setStopY(stopColumnY);
            }

            mColumnCircleAnimationDrawInfoList.get(i)
                    .setStopX(stopColumnX)
                    .setStopY(stopColumnY);

            startColumnX = stopColumnX;
            startColumnY = stopColumnY;
        }
    }

    public void setValue(List<Float> values) {
        removeCallbacks(this);
        mOriginalValueColumns.clear();
        for (Float valueColumn : mValueColumns) {
            mOriginalValueColumns.add(valueColumn / getAnimationTotalInterval() * mCurrentAnimationIntervalIndex);
        }

        mValueColumns.clear();
        mValueColumns.addAll(values);

        mCurrentAnimationIntervalIndex = 0;
        postDelayed(this, mAnimationInterval);
    }

    private float getTargetColumnX(int width, int columnLeftX, int columnSpace, int index) {
        return columnLeftX + width * 0.05f + index * columnSpace;
    }

    private void initData() {
        mRowLineGeneralDrawInfoList = createEmptyGeneralDrawInfo(mDisplayRowCount);
        mRowTitleGeneralDrawInfoList = createEmptyGeneralDrawInfo(mDisplayRowCount);
        mColumnLineAnimationDrawInfoList = createEmptyDrawInfo(mValueColumns.size());
        mColumnCircleAnimationDrawInfoList = createEmptyDrawInfo(mValueColumns.size());
        mColumnTitleGeneralDrawInfoList = createEmptyGeneralDrawInfo(mValueColumns.size());
        complementData();
        setColumnsTitle();
    }

    private int getAnimationTotalInterval() {
        return mAnimationTotalTime / mAnimationInterval;
    }

    private List<AnimationDrawInfo> createEmptyDrawInfo(int size) {
        List<AnimationDrawInfo> result = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            result.add(new AnimationDrawInfo());
        }
        return result;
    }

    private List<GeneralDrawInfo> createEmptyGeneralDrawInfo(int size) {
        List<GeneralDrawInfo> result = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            result.add(new GeneralDrawInfo());
        }
        return result;
    }

    private int getColumnSpace(int width) {
        return (int) ((width * 0.8) / mValueColumns.size());
    }

    private int getRowSpace(int height) {
        return (int) ((height * 0.85) / (mDisplayRowCount - 1));
    }

    private int getRowLineTopY(int height) {
        return getRowLineBottomY(height) - (mDisplayRowCount - 1) * getRowSpace(height);
    }

    private int getRowLineBottomY(int height) {
        return (int) (height * 0.9);
    }

    private int getColumnLeftX(int width) {
        return (int) (width * 0.1);
    }

    /**
     * 數值補位至圖表上限
     */
    private void complementData() {
        for (int i = 1; i <= mValueColumns.size(); i++) {
            if (mOriginalValueColumns.size() < i) {
                mOriginalValueColumns.add(0f);
            }
        }
    }

    private float getMaxRowValue(List<Float> rows) {
        if (rows.size() == 0) {
            return DEFAULT_MAX_ROW_VALUE;
        }

        float result = Collections.max(rows);
        return result == 0 ? DEFAULT_MAX_ROW_VALUE : Collections.max(rows);
    }

    private void setDemoValue() {
        post(new Runnable() {
            @Override
            public void run() {
                setValue(new ArrayList<>(Arrays.asList(20f, 26f, 40f, 62f, 82f, 0f, 22f, 100f, 2f, 72f, 12f, 45f)));
            }
        });
    }

    private void setColumnsTitle() {
        mTitleColumns = new ArrayList<>();
        for (int i = 0; i < mValueColumns.size(); i++) {
            mTitleColumns.add(i + 1 + "");
        }
    }

    private static class AnimationDrawInfo {
        private float mStartX;
        private float mStartY;
        private float mStopX;
        private float mStopY;
        private String mText = "";

        public float getStartX() {
            return mStartX;
        }

        public AnimationDrawInfo setStartX(float startX) {
            mStartX = startX;
            return this;
        }

        public float getStartY() {
            return mStartY;
        }

        public AnimationDrawInfo setStartY(float startY) {
            mStartY = startY;
            return this;
        }

        public float getStopX() {
            return mStopX;
        }

        public AnimationDrawInfo setStopX(float stopX) {
            mStopX = stopX;
            return this;
        }

        public float getStopY() {
            return mStopY;
        }

        public AnimationDrawInfo setStopY(float stopY) {
            mStopY = stopY;
            return this;
        }

        public String getText() {
            return mText;
        }

        public AnimationDrawInfo setText(String text) {
            mText = text;
            return this;
        }
    }

    private static class GeneralDrawInfo {
        private float mX;
        private float mY;
        private float mStartX;
        private float mStartY;
        private float mStopX;
        private float mStopY;
        private String mText = "";

        public float getX() {
            return mX;
        }

        public GeneralDrawInfo setX(float x) {
            mX = x;
            return this;
        }

        public float getY() {
            return mY;
        }

        public GeneralDrawInfo setY(float y) {
            mY = y;
            return this;
        }

        public float getStartX() {
            return mStartX;
        }

        public GeneralDrawInfo setStartX(float startX) {
            mStartX = startX;
            return this;
        }

        public float getStartY() {
            return mStartY;
        }

        public GeneralDrawInfo setStartY(float startY) {
            mStartY = startY;
            return this;
        }

        public float getStopX() {
            return mStopX;
        }

        public GeneralDrawInfo setStopX(float stopX) {
            mStopX = stopX;
            return this;
        }

        public float getStopY() {
            return mStopY;
        }

        public GeneralDrawInfo setStopY(float stopY) {
            mStopY = stopY;
            return this;
        }

        public String getText() {
            return mText;
        }

        public GeneralDrawInfo setText(String text) {
            mText = text;
            return this;
        }
    }
}