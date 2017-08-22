package com.customview.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.MeasureSpec;

import com.customview.R;

public class DataChartView extends View {
    private static final String TAG = DataChartView.class.getSimpleName();

    private static boolean DEBUG = false;
    private float mYOffset1;// 标题与y坐标最顶端的文字距离
    private float mYOffset2;// 三条坐标指示的高度
    private float mYOffset3;// y坐标最低端文字与竖线距离
    private float mYOffset4;// 竖线与横坐标文字的距离
    private float mXOffset1;// 纵坐标文字与横坐标的距离

    private int mPositiveDataColor = Color.GREEN;
    private int mNegativeDataColor = Color.YELLOW;
    private int mDataShadeColor = 0x8800ff00;

    private float mTitleSize = 24f;
    private int mTitleColor = Color.GRAY;
    private String mTitleText;
    private float mAxisTextSize = 24f;
    private int mAxisTextColor = Color.GRAY;
    private float mAxisTextHeight;
    private float mTitleTextHeight;

    private int[] mXAxisData = new int[] { 200, 148, -67, 166, -60, -20 };
    private String[] mXAxisText = new String[] { "90 min", "75 min", "60 min", "45 min", "30 min", "15 min" };
    private int[] mXAxisTextWidth = new int[6];
    private String[] mYAxisText = new String[] { "200 kW", "100 kW", "0 kW", "-100 kW" };
    private int[] mYAxisTextWidth = new int[4];
    private int mYAxisTextMaxWidth;
    private float mXAxisTextHeight;
    private float mYAxisTextHeight;
    private float mYAxisTextRectHeight;
    private float mXAxisFlagHeight = 8f;

    private Paint mPaint;
    private Rect mRect;
    private RectF mRectF;
    private Typeface mFont;
    private DashPathEffect mPathEffect;
    private Path mPath;
    private int mPercent;
    private String mDistText = "230 km";
    private String mTimeText = "2h40min";
    private String mECOInfo = "";
    private int mWidth = 0;
    private int mHeight = 0;

    public DataChartView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DataChartView(Context context) {
        this(context, null);
    }

    // 获得自定义的样式属性
    public DataChartView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.DataChartView, defStyle, 0);
        mYOffset1 = a.getFloat(R.styleable.DataChartView_YOffset1, 30);// 标题与y坐标最顶端的文字距离
        mYOffset2 = a.getFloat(R.styleable.DataChartView_YOffset2, 60);// y坐标文字间的距离间隔
        mYOffset3 = a.getFloat(R.styleable.DataChartView_YOffset3, 8);// y坐标最低端文字与竖线距离
        mYOffset4 = a.getFloat(R.styleable.DataChartView_YOffset4, 0);// 竖线与横坐标文字的距离
        mXOffset1 = a.getFloat(R.styleable.DataChartView_XOffset1, 30);// 纵坐标文字与横坐标的距离

        mTitleSize = a.getFloat(R.styleable.DataChartView_TitleSize, 24f);
        mAxisTextSize = a.getFloat(R.styleable.DataChartView_AxisTextSize, 24f);
        mTitleText = a.getString(R.styleable.DataChartView_TitleText);
        if (mTimeText == null) {
            mTitleText = getResources().getString(R.string.eco_efficciency_text);
        }
        a.recycle();

        mPaint = new Paint();
        mRect = new Rect();
        mPathEffect = new DashPathEffect(new float[] { 2, 2 }, 0);
        mPath = new Path();

        // 获取title的高度
        setTitleAttribute();
        mPaint.getTextBounds(mTitleText, 0, mTitleText.length(), mRect);
        FontMetrics fontMetrics = mPaint.getFontMetrics();
        mTitleTextHeight = fontMetrics.bottom - fontMetrics.top;
        Log.d("uidq0506", "mTitleTextHeight: " + mTitleTextHeight);
        // 获取y轴标识高度及最大宽度
        setYAxisTextAttribute();
        int i = 0;
        for (i = 0; i < mYAxisText.length; i++) {
            mPaint.getTextBounds(mYAxisText[i], 0, mYAxisText[i].length(), mRect);
            mYAxisTextWidth[i] = mRect.right - mRect.left;
            if (mYAxisTextMaxWidth < mYAxisTextWidth[i]) {
                mYAxisTextMaxWidth = mYAxisTextWidth[i];
            }
        }
        fontMetrics = mPaint.getFontMetrics();
        mYAxisTextHeight = fontMetrics.bottom - fontMetrics.top;
        mYAxisTextRectHeight = mRect.bottom - mRect.top;
        Log.d("uidq0506", "mYAxisTextMaxWidth: " + mYAxisTextMaxWidth + "; mYAxisTextHeight: " + mYAxisTextHeight
                + "; mYAxisTextRectHeight: " + mYAxisTextRectHeight);
        // 获取x轴标识相关数据
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setTextSkewX(0f);
        for (i = 0; i < mXAxisText.length; i++) {
            mPaint.getTextBounds(mXAxisText[i], 0, mXAxisText[i].length(), mRect);
            mXAxisTextWidth[i] = mRect.right - mRect.left;
        }
        fontMetrics = mPaint.getFontMetrics();
        mAxisTextHeight = fontMetrics.bottom - fontMetrics.top;
        mXAxisTextHeight = (int) mAxisTextHeight;
        Log.d("uidq0506", "mAxisTextHeight: " + mAxisTextHeight);
        Log.d("uidq0506", "mYOffset1: " + mYOffset1 + "; mYOffset2: " + mYOffset2 + "; mYOffset3: " + mYOffset3
                + "; mYOffset4: " + mYOffset4 + "; mXOffset1: " + mXOffset1);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mHeight = (int) (mTitleTextHeight + mYOffset1 + mYOffset2 * 3 + mYOffset3 + mYOffset4 + mXAxisTextHeight + mXAxisFlagHeight);
        mWidth = measureHeight(widthMeasureSpec);
        Log.d(TAG, "onMeasure#mWidth: " + mWidth + "; mHeight: " + mHeight);
        Log.d("uidq0506", "onMeasure#mWidth: " + mWidth + "; mHeight: " + mHeight);
        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d(TAG, "onDraw, mAxisTextSize: " + mAxisTextSize + "; mYAxisText[0]: " + mYAxisText[0]);
        int i = 0;
        float x, y, x1, y1;

        // step1: 画标题
        setTitleAttribute();
        x = mWidth / 2;
        y = mTitleTextHeight;
        canvas.drawText(mTitleText, x, y, mPaint);// 注意TextAlign
        if (DEBUG) {
            canvas.drawRect(x, 0, x + 30, y, mPaint);
        }
        // step2: 画纵坐标文本, 从上往下 画,由于高度计算很难处理，此处后续需要微调
        // TODO:
        setYAxisTextAttribute();
        x = mYAxisTextMaxWidth;
        float temp = mYOffset2 * 3 / 4;
        for (i = 0; i < mYAxisText.length; i++) {
            y = mTitleTextHeight + mYOffset1 + mYAxisTextRectHeight/2 + mYOffset2 * i - 0;//最后的0用来设定偏移数据
            canvas.drawText(mYAxisText[i], x, y, mPaint);// 注意TextAlign
            if (DEBUG) {
                if (i == 0)
                    canvas.drawRect(0, mTitleTextHeight + mYOffset1, 800, y, mPaint);
                if (i == 1)
                    canvas.drawRect(300, y - (mYOffset2 + mYAxisTextHeight), 600, y, mPaint);
            }
        }
        Log.d("uidq0506", "11111111111111y: " + y);
        // step3: 画横坐标文本
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setTextSkewX(0f);
        float xAxisStep = (mWidth - mXOffset1 - mYAxisTextMaxWidth) / 6;
        y = mHeight;
        float y2, ytemp;
        int value;
        y1 = mTitleTextHeight + mYOffset1 + mYOffset2 * 2; // "0 kW"的位置
        for (i = 0; i < mXAxisText.length; i++) {
            x = mXOffset1 + mYAxisTextMaxWidth + xAxisStep * i + xAxisStep / 2;
            mPaint.setColor(mAxisTextColor);
            canvas.drawText(mXAxisText[i], x, y, mPaint);// 注意TextAlign
            mPaint.setColor(Color.WHITE);
            mPaint.setStrokeWidth(2f);
            y2 = y - mXAxisTextHeight - mYOffset4;
            canvas.drawLine(x, y2, x, y2 - mXAxisFlagHeight, mPaint);
            value = mXAxisData[i];
            ytemp = y1 - value * mYOffset2 / 100;  //因为一格对应的是100kM
            Log.d("uidq0506", "value: " + value + "; ytemp: " + ytemp);
            if (value < 0) {
                mPaint.setColor(Color.YELLOW);
            } else {
                mPaint.setColor(Color.GREEN);
            }
            canvas.drawLine(x - xAxisStep / 4, ytemp, x + xAxisStep / 4, ytemp, mPaint);  //画绿色或者黄色的数值
            mPaint.setColor(/*Color.GREEN*/0x2200ff00);
            mPaint.setStyle(Paint.Style.FILL);
            if (value < 0) {// 画Rect的效果
                canvas.drawRect(x - xAxisStep / 4, y1, x + xAxisStep / 4, ytemp, mPaint);
            } else {
                canvas.drawRect(x - xAxisStep / 4, ytemp, x + xAxisStep / 4, y1, mPaint);
            }

        }
        // step4: 画水平方向的5条横线
        mPaint.reset();
        mPaint.setColor(/*Color.GRAY*/0x22888888);
        mPaint.setStrokeWidth(1f);
        x = mXOffset1 + mYAxisTextMaxWidth;
        x1 = mWidth;
        for (i = 0; i < mYAxisText.length; i++) {
            y = mTitleTextHeight + mYOffset1 + mYOffset2 * i;
            y1 = y;
            canvas.drawLine(x, y, x1, y1, mPaint);
        }
        mPaint.setStyle(Paint.Style.STROKE);// 设置画笔为空心
        mPaint.setStrokeWidth(2);// 设置线宽
        mPaint.setColor(Color.GREEN);
        mPaint.setAntiAlias(true);// 抗锯齿
        mPaint.setPathEffect(mPathEffect);
        y = mTitleTextHeight + mYOffset1 + mYOffset2 * 3 / 2;
        mPath.moveTo(x, y);
        mPath.lineTo(x1, y);
        canvas.drawPath(mPath, mPaint);
    }

    private int measureHeight(int heightMeasureSpec) {
        // TODO Auto-generated method stub
        int result = 0;
        int specMode = MeasureSpec.getMode(heightMeasureSpec);
        int specSize = MeasureSpec.getSize(heightMeasureSpec);
        switch (specMode) {
            case MeasureSpec.AT_MOST: // 子容器可以是声明大小内的任意大小
                Log.d(TAG, "子容器可以是声明大小内的任意大小, specSize: " + specSize);
                result = specSize;
                break;
            case MeasureSpec.EXACTLY: // 父容器已经为子容器设置了尺寸,子容器应当服从这些边界,不论子容器想要多大的空间. 比如EditTextView中的DrawLeft
                Log.e(TAG, "父容器已经为子容器设置了尺寸,子容器应当服从这些边界,不论子容器想要多大的空间, specSize: " + specSize);
                result = specSize;
                break;
            case MeasureSpec.UNSPECIFIED: // 父容器对于子容器没有任何限制,子容器想要多大就多大. 所以完全取决于子view的大小
                Log.e(TAG, "父容器对于子容器没有任何限制,子容器想要多大就多大, specSize: " + specSize);
                result = 600;
                break;
            default:
                break;
        }
        return result;
    }

    private void setTitleAttribute() {
        mPaint.reset();
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setTextSize(mTitleSize);
        mPaint.setColor(mTitleColor);
        mFont = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD);
        mPaint.setTypeface(mFont);
    }

    private void setYAxisTextAttribute() {
        mPaint.setTextAlign(Paint.Align.RIGHT);
        mPaint.setTextSize(mAxisTextSize);
        mPaint.setColor(mAxisTextColor);
        mFont = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL);
        mPaint.setTypeface(mFont);
        mPaint.setTextSkewX(-0.1f);
    }

    public void setXAxisString(String[] str) {
        if (str == null || str.length != mXAxisText.length || str.length != mXAxisData.length) {
            return;
        }
        for (int i = 0; i < str.length; i++) {
            mXAxisText[i] = str[i];
        }
        invalidate();
    }

    public void setXAxisValue(int[] data) {
        if (data == null || data.length != mXAxisText.length || data.length != mXAxisData.length) {
            return;
        }
        int i = 0;
        for (i = 0; i < data.length; i++) {
            if (data[i] > 200 || data[i] < -100) {
                return;
            }
        }
        for (i = 0; i < data.length; i++) {
            mXAxisData[i] = data[i];
        }
        invalidate();
    }
}
