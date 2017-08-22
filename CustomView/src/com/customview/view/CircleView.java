package com.customview.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.customview.R;

public class CircleView extends View {
    private static final String TAG = CircleView.class.getSimpleName();

    private static boolean DEBUG = false;
    private float mRadiusLen = 0f;
    private int mDashCircleColor = Color.GREEN;
    private int mSolidCircleColor = Color.BLUE;
    private String mAttribute;
    private float mPercentTextSize;
    private int mPercentTextColor = Color.WHITE;
    private float mInfoTextSize;
    private int mInfoTextColor = Color.GRAY;
    private float mInfoTextOffset = 10;
    private boolean m2InfoLine = false;
    private float mPercentWidth;
    private float mPercentOffset = 12f;

    private Paint mPaint;
    private Rect mRect;
    private RectF mRectF;
    private DashPathEffect mPathEffect;
    private int mPercent;
    private String mDistText = "230 km";
    private String mTimeText = "2h40min";
    private String mECOInfo = "";
    private int mWidth = 0;
    private int mHeight = 0;

    public CircleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleView(Context context) {
        this(context, null);
    }

    // 获得自定义的样式属性
    public CircleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CircleView, defStyle, 0);
        mRadiusLen = a.getFloat(R.styleable.CircleView_radiusLen, 100);
        mDashCircleColor = a.getColor(R.styleable.CircleView_dottedCircleColor, Color.GREEN);
        mSolidCircleColor = a.getColor(R.styleable.CircleView_solidCircleColor, Color.BLUE);
        mAttribute = a.getString(R.styleable.CircleView_circleAttribute);
        if (mAttribute != null) {
            m2InfoLine = mAttribute.equals("battery");
        }
        mPercentTextSize = a.getFloat(R.styleable.CircleView_percentTextSize, 72);
        mPercentTextColor = a.getColor(R.styleable.CircleView_percentTextColor, Color.WHITE);
        mInfoTextSize = a.getFloat(R.styleable.CircleView_infoTextSize, 24);
        mInfoTextColor = a.getColor(R.styleable.CircleView_infoTextColor, Color.GRAY);
        a.recycle();

        mECOInfo = getResources().getString(R.string.eco_efficciency_text);
        mPaint = new Paint();
        mRect = new Rect();
        mRectF = new RectF(0, 0, mRadiusLen * 2, mRadiusLen * 2);
        mPathEffect = new DashPathEffect(new float[] { 2, 12 }, 0);

        mPaint.setTextSize(mPercentTextSize / 3);
        mPaint.setTypeface(Typeface.DEFAULT_BOLD);
        mPaint.getTextBounds("%", 0, "%".length(), mRect);
        mPercentWidth = mRect.right - mRect.left;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mHeight = mWidth = (int) (mRadiusLen * 2);
        Log.d(TAG, "onMeasure#mWidth: " + mWidth + "; mHeight: " + mHeight);
        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d(TAG, "onDraw");
        // 计算各个高度宽度以及对应坐标
        int angle = mPercent == 0 ? 52 : mPercent; // 这个处理是demo效果的
        mPaint.reset();
        // mPaint.setColor(mPercentTextColor);
        mPaint.setTextSize(mPercentTextSize);
        // mPaint.setTypeface(Typeface.DEFAULT_BOLD);// 设置字体样式
        // mPaint.setStyle(Paint.Style.FILL);
        String text = String.valueOf(angle);
        mPaint.getTextBounds(text, 0, text.length(), mRect);
        // 数字的高度，需要注意，这块后续亦有可能继续改进
        // FontMetrics fontMetrics = mPaint.getFontMetrics();
        float titleHeight = /*fontMetrics.bottom - fontMetrics.top*/mRect.bottom - mRect.top;
        float titleWidth = mRect.right - mRect.left;
        Log.d(TAG, "titleHeight: " + titleHeight + "; titleWidth: " + titleWidth + "; " + mRect.height());
        mPaint.setTextSize(mInfoTextSize);
        mPaint.getTextBounds(mECOInfo, 0, mECOInfo.length(), mRect);
        FontMetrics fontMetrics = mPaint.getFontMetrics();
        float infoHeight = fontMetrics.bottom - fontMetrics.top;
        float infoWidth = mRect.right - mRect.left;
        Log.d(TAG, "infoHeight: " + infoHeight + "; infoWidth: " + infoWidth);

        // part1： 画虚线圆
        mPaint.reset();
        mPaint.setStyle(Paint.Style.STROKE);// 设置画笔为空心
        mPaint.setStrokeWidth(2);// 设置线宽
        mPaint.setColor(mDashCircleColor);
        mPaint.setAntiAlias(true);// 抗锯齿
        mPaint.setPathEffect(mPathEffect);
        float cx = mRadiusLen;
        float cy = mRadiusLen;
        canvas.drawCircle(cx, cy, mRadiusLen, mPaint);

        // part2： 画实线圆
        mPaint.setColor(mSolidCircleColor);
        mPaint.setPathEffect(null);
        canvas.drawArc(mRectF, 270, angle * 360 / 100, false, mPaint);

        // part3:画百分比数字
        mPaint.setColor(mPercentTextColor);
        mPaint.setTextSize(mPercentTextSize);
        mPaint.setStyle(Paint.Style.FILL);
        // x = (mWidth - titleWidth - mPercentWidth)/2
        float x = mRadiusLen - (titleWidth + mPercentWidth + mPercentOffset) / 2;
        // y = (mHeight - titleHeight - infoHeight(或者*2)- mInfoTextOffset(或者*2))/2 + titleHeight
        // = mRadiusLen + titleHeight/2 - （infoHeight(或者*2) + mInfoTextOffset(或者*2))/2
        float y = mRadiusLen + titleHeight / 2
                - (m2InfoLine ? (infoHeight + mInfoTextOffset) : (infoHeight + mInfoTextOffset) / 2);
        Log.d(TAG, "drawPercentValue, x: " + x + "; y: " + y);
        canvas.drawText(text, x, y, mPaint);// 绘制文字从左下角开始
        if (DEBUG) {
            mPaint.setColor(0x66ff0000);
            canvas.drawRect(x, y - titleHeight, x + titleWidth, y, mPaint);
        }

        // part4:画百分号
        mPaint.setColor(Color.GRAY);
        mPaint.setTextSize(mPercentTextSize / 3);
        x = x + titleWidth + mPercentOffset;
        Log.d(TAG, "drawPercentSymbol, x: " + x + "; y: " + y);
        canvas.drawText("%", x, y, mPaint);

        // part5:画INFO信息
        mPaint.setColor(mInfoTextColor);
        mPaint.setTextSize(mInfoTextSize);
        if (!m2InfoLine) {
            mPaint.getTextBounds(mECOInfo, 0, mECOInfo.length(), mRect);
            x = mRadiusLen - (mRect.right - mRect.left) / 2;
            y = y + infoHeight + mInfoTextOffset;
            Log.d(TAG, "oneline#drawInfo, x: " + x + "; y: " + y);
            canvas.drawText(mECOInfo, x, y, mPaint);
            if (DEBUG) {
                mPaint.setColor(0x660000ff);
                canvas.drawRect(x, y - infoHeight, x + infoWidth, y, mPaint);
            }
        } else {
            mPaint.getTextBounds(mDistText, 0, mDistText.length(), mRect);
            Log.d(TAG, "twolines#drawInfo1,mDistText: " + mDistText + "; mRect.right: " + mRect.right
                    + "; mRect.left: " + mRect.left + "; " + mRect.width());
            x = mRadiusLen - (mRect.right - mRect.left) / 2;
            y = y + infoHeight + mInfoTextOffset;
            infoWidth = mRect.right - mRect.left;
            Log.d(TAG, "twolines#drawInfo1, x: " + x + "; y: " + y);
            canvas.drawText(mDistText, x, y, mPaint);
            if (DEBUG) {
                mPaint.setColor(0x660000ff);
                canvas.drawRect(x, y - infoHeight, x + infoWidth, y, mPaint);
            }
            mPaint.getTextBounds(mTimeText, 0, mTimeText.length(), mRect);
            x = mRadiusLen - (mRect.right - mRect.left) / 2;
            y = y + infoHeight + mInfoTextOffset;
            infoWidth = mRect.right - mRect.left;
            Log.d(TAG, "twolines#drawInfo2, x: " + x + "; y: " + y);
            canvas.drawText(mTimeText, x, y, mPaint);
            if (DEBUG) {
                mPaint.setColor(0x660000ff);
                canvas.drawRect(x, y - infoHeight, x + infoWidth, y, mPaint);
            }
        }
    }

    public void setPercent(int percent) {
        mPercent = percent;
        invalidate();
    }

    public void setDistText(String distText) {
        mDistText = distText;
        invalidate();
    }

    public void setTimeText(String timeText) {
        mTimeText = timeText;
        invalidate();
    }
}
