package com.customview.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.customview.R;

public class EnergyFlowView extends View {
    private static final String TAG = EnergyFlowView.class.getSimpleName();
    private int mBackgroundColor = Color.GRAY;// 进度条默认颜色
    private int mTextColor = Color.GRAY;// 文本颜色
    private float mCenterPos = 0;// "0 kW的位置"
    private float mTextSize = 0;// "0 kW的位置"
    private int mBackgroundHeight = 30; // 背景高度
    private int mCenterLineHeight = 40; // 中间线的高度
    private int mCenterTextLineOffset = 10;
    private Paint mPaint;
    private Rect mRect;
    LinearGradient shader;

    private String mLeftText = "";
    private String mMiddleText = "";
    private String mRightText = "";
    private int mViewHeight = 113;

    private float mGenFactor = 1.0f;
    private float mConsumeFactor = 1.0f;
    private int mCurrentGen = 0;
    private int mCurrentConsume = 0;
    private int mWidth = 0;
    private int mHeight = 0;

    public EnergyFlowView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EnergyFlowView(Context context) {
        this(context, null);
    }

    // 获得自定义的样式属性
    public EnergyFlowView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.EnergyFlowView, defStyle, 0);
        mCenterPos = a.getFloat(R.styleable.EnergyFlowView_centerPosition, 100);
        mBackgroundColor = a.getColor(R.styleable.EnergyFlowView_backgroundColor, Color.GRAY);
        mTextColor = a.getColor(R.styleable.EnergyFlowView_textColor, Color.GRAY);
        mTextSize = a.getFloat(R.styleable.EnergyFlowView_textSize, 24);
        a.recycle();
        Log.d(TAG, "mCenterPos: " + mCenterPos + "; mBackgroundColor: " + mBackgroundColor + "; mTextColor: "
                + mTextColor);
        mPaint = new Paint();
        mRect = new Rect();
        mLeftText = getResources().getString(R.string.energy_flow_text_left);
        mMiddleText = getResources().getString(R.string.energy_flow_text_middle);
        mRightText = getResources().getString(R.string.energy_flow_text_right);
        mPaint.setColor(mTextColor);
        mPaint.setTextSize(mTextSize);
        mPaint.setTypeface(Typeface.DEFAULT_BOLD);// 设置字体样式

        mPaint.getTextBounds("TEST", 0, "TEST".length(), mRect);
        FontMetrics fontMetrics = mPaint.getFontMetrics();
        float fontHeight = fontMetrics.bottom - fontMetrics.top;
        mViewHeight = (int) (fontHeight*2 + mCenterLineHeight + mCenterTextLineOffset);
        Log.d(TAG, "mLeftText: " + mLeftText + "; mMiddleText: " + mMiddleText + "; mRightText: " + mRightText + "; mViewHeight: " + mViewHeight);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = measureHeight(widthMeasureSpec);
        mHeight = measureHeight(heightMeasureSpec);
        Log.d(TAG, "onMeasure#mWidth: " + mWidth + "; mHeight: " + mHeight);
        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d(TAG, "onDraw");
        mPaint.reset();
        mPaint.setColor(mTextColor);
        mPaint.setTextSize(mTextSize);
        mPaint.setTypeface(Typeface.DEFAULT_BOLD);// 设置字体样式

        // part1：左边的字
        mPaint.getTextBounds(mLeftText, 0, mLeftText.length(), mRect);
        FontMetrics fontMetrics = mPaint.getFontMetrics();
        float fontHeight = fontMetrics.bottom - fontMetrics.top;
        float x = 0;
        float y = mHeight;
        canvas.drawText(mLeftText, x, y, mPaint);// 绘制文字从左下角开始

        // part2：右边的字
        mPaint.getTextBounds(mRightText, 0, mRightText.length(), mRect);
        x = mWidth - (mRect.right - mRect.left);
        y = mHeight;
        canvas.drawText(mRightText, x, y, mPaint);

        // part3：背景部分
        mPaint.setShader(null);
        mPaint.setColor(mBackgroundColor);
        float startX = 0;
        float startY = mHeight - fontHeight - mBackgroundHeight;
        float stopX = mWidth;
        float stopY = mHeight - fontHeight + 0;
        canvas.drawRect(startX, startY, stopX, stopY, mPaint);

        // part4：竖着的标线
        mPaint.setColor(Color.RED);
        mPaint.setStrokeWidth(2);
        canvas.drawLine(mCenterPos, stopY - mCenterLineHeight, mCenterPos, stopY, mPaint);

        // part5：中间的文字
        mPaint.setColor(mTextColor);
        mPaint.setTextSize(mTextSize);
        mPaint.setTypeface(Typeface.DEFAULT_BOLD);// 设置字体样式
        mPaint.getTextBounds(mMiddleText, 0, mMiddleText.length(), mRect);
        x = mCenterPos - (mRect.right - mRect.left) / 2;
        y = stopY - mCenterLineHeight - mCenterTextLineOffset;
        canvas.drawText(mMiddleText, x, y, mPaint);

        // part6：左侧的进度
        float x0 = mCenterPos - mCurrentGen == 0 ? 300 : mGenFactor * mCurrentGen; // this value(300) is just for demo
        float y0 = startY;
        float x1 = mCenterPos;
        float y1 = stopY;
        shader = new LinearGradient(x0, y0, x1, y1, Color.GREEN, Color.GRAY, Shader.TileMode.CLAMP);
        mPaint.setShader(shader);
        canvas.drawRect(x0, y0, x1, y1, mPaint);

        // part7：右侧的进度
        x0 = mCenterPos;
        y0 = startY;
        x1 = mCenterPos + mCurrentConsume == 0 ? 500 : mConsumeFactor * mCurrentConsume; // this value(500) is just for demo
        y1 = stopY;
        shader = new LinearGradient(x0, y0, x1, y1, Color.GRAY, Color.YELLOW, Shader.TileMode.CLAMP);
        mPaint.setShader(shader);
        canvas.drawRect(x0, y0, x1, y1, mPaint);
    }

    private int measureHeight(int heightMeasureSpec) {
        // TODO Auto-generated method stub
        int result = 0;
        int specMode = MeasureSpec.getMode(heightMeasureSpec);
        int specSize = MeasureSpec.getSize(heightMeasureSpec);
        switch (specMode) {
            case MeasureSpec.AT_MOST: // 子容器可以是声明大小内的任意大小
                Log.d(TAG, "子容器可以是声明大小内的任意大小, specSize: " + specSize);
                result = /*specSize*/mViewHeight;
                break;
            case MeasureSpec.EXACTLY: // 父容器已经为子容器设置了尺寸,子容器应当服从这些边界,不论子容器想要多大的空间. 比如EditTextView中的DrawLeft
                Log.e(TAG, "父容器已经为子容器设置了尺寸,子容器应当服从这些边界,不论子容器想要多大的空间, specSize: " + specSize);
                result = specSize;
                break;
            case MeasureSpec.UNSPECIFIED: // 父容器对于子容器没有任何限制,子容器想要多大就多大. 所以完全取决于子view的大小
                Log.e(TAG, "父容器对于子容器没有任何限制,子容器想要多大就多大, specSize: " + specSize);
                result = mViewHeight;
                break;
            default:
                break;
        }
        return result;
    }

    public void setGenFract(int maxGen) {
        if (maxGen != 0) {
            mGenFactor = mCenterPos / maxGen;
        }
    }

    public void setmMaxConsume(int maxConsume) {
        if (maxConsume != 0) {
            mConsumeFactor = (mWidth - mCenterPos) / maxConsume;
        }
    }

    // any time, if call this function, one of the two parameters must be 0
    public void setEnergyFlowData(int gen, int consume) {
        if (gen != 0 && consume != 0) {
            return;
        }
        mCurrentGen = gen;
        mCurrentConsume = consume;
        invalidate();
    }
}
