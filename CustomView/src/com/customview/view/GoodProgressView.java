package com.customview.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.Paint.Cap;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import com.customview.R;

public class GoodProgressView extends View {
    private int[] mColors = { Color.RED, Color.MAGENTA };// 进度条颜色（渐变色的2个点）
    private int backgroundColor = Color.GRAY;// 进度条默认颜色
    private int textColor = Color.GRAY;// 文本颜色

    private Paint mPaint;// 画笔
    private int progressValue = 0;// 进度值ֵ

    // private RectF rect;//绘制范围

    public GoodProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GoodProgressView(Context context) {
        this(context, null);
    }

    // 获得自定义的样式属性
    public GoodProgressView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.GoodProgressView, defStyle, 0);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.GoodProgressView_startColor:
                    // 渐变色之起始颜色，默认设置为红色
                    mColors[0] = a.getColor(attr, Color.RED);
                    break;
                case R.styleable.GoodProgressView_endColor:
                    // 渐变色之结束颜色，默认设置为品红
                    mColors[1] = a.getColor(attr, Color.MAGENTA);
                    break;
                case R.styleable.GoodProgressView_backgroundColor:
                    // 进度条默认颜色，默认设置为灰色
                    backgroundColor = a.getColor(attr, Color.GRAY);
                    break;
                case R.styleable.GoodProgressView_textColor:
                    // 文字颜色，默认设置为灰色
                    textColor = a.getColor(attr, Color.GRAY);
                    break;
            }
        }
        a.recycle();

        mPaint = new Paint();
        progressValue = 0;
    }

    public void setProgressValue(int progressValue) {

        if (progressValue > 100) {
            progressValue = 100;
        }
        this.progressValue = progressValue;
        Log.i("customView", "log: progressValue=" + progressValue);
    }

    public void setColors(int[] colors) {
        mColors = colors;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int width = 0;
        int height = 0;
        /**
         * 设置宽度
         */
        int specMode = MeasureSpec.getMode(widthMeasureSpec);
        int specSize = MeasureSpec.getSize(widthMeasureSpec);
        switch (specMode) {
            case MeasureSpec.EXACTLY:// 明确指定了
                width = specSize;
                break;
            case MeasureSpec.AT_MOST:// 一般为WRAP_CONTENT
                Log.i("customView", "AT_MOST: " + getPaddingLeft() + "; " + getPaddingRight());
                width = getPaddingLeft() + getPaddingRight();
                break;
        }

        /**
         * 设置高度
         */
        specMode = MeasureSpec.getMode(heightMeasureSpec);
        specSize = MeasureSpec.getSize(heightMeasureSpec);
        switch (specMode) {
            case MeasureSpec.EXACTLY:
                Log.i("customView", "EXACTLY: " + specSize);
                height = specSize;
                break;
            case MeasureSpec.AT_MOST:
                height = width / 10;
                break;
        }

        Log.i("customView", "log: w=" + width + " h=" + height);
        setMeasuredDimension(width, height);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int mWidth = getMeasuredWidth();
        int mHeight = getMeasuredHeight();

        //按比例计算进度条各部分的值
        float unit = Math.min(((float) mWidth) / 300, ((float) mHeight) / 30);
        float lineWidth = 5 * unit;//线粗
        float innerCircleDiameter = 6 * unit;//内圆直径
        float outerCircleDiameter = 10 * unit;//外圆直径
        float wordHeight = 12 * unit;// 字高
        // float wordWidth = 26*unit;//字长
        float offsetLength = 5 * unit;// 留空
        // float width = 300*unit;//绘画区域的长度
        float height = 30 * unit;// 绘画区域的高度
        float progressWidth = 258 * unit;// 绘画区域的长度

      //设置抗锯齿，如果不设置，加载位图的时候可能会出现锯齿状的边界，如果设置，边界就会变的稍微有点模糊，锯齿就看不到了
        mPaint.setAntiAlias(true);
      //画笔样式为空心时，设置空心画笔的宽度
        mPaint.setStrokeWidth((float) lineWidth);
        mPaint.setStyle(Style.STROKE);
        mPaint.setStrokeCap(Cap.ROUND);

        mPaint.setColor(Color.TRANSPARENT);

        float offsetHeight = height / 2;
        float offsetWidth = offsetLength;

        float section = ((float) progressValue) / 100;
        if (section > 1)
            section = 1;

        int count = mColors.length;
        int[] colors = new int[count];
        System.arraycopy(mColors, 0, colors, 0, count);

      //底部灰色背景，指示进度条总长度
        mPaint.setShader(null);
        mPaint.setColor(backgroundColor);
        canvas.drawLine(offsetWidth + section * progressWidth, offsetHeight, offsetWidth + progressWidth, offsetHeight,
                mPaint);

      //设置渐变色区域
        LinearGradient shader = new LinearGradient(0, 0, offsetWidth * 2 + progressWidth, 0, colors, null,
                Shader.TileMode.CLAMP);
        mPaint.setShader(shader);

      //画出渐变色进度条
        canvas.drawLine(offsetWidth, offsetHeight, offsetWidth + section * progressWidth, offsetHeight, mPaint);

      //渐变色外圆
        mPaint.setStrokeWidth(1);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(offsetWidth + section * progressWidth, offsetHeight, outerCircleDiameter / 2, mPaint);

      //绘制两条斜线，使外圆到进度条的连接更自然
        if (section * 100 > 1.8) {

            mPaint.setStrokeWidth(2 * unit);
            canvas.drawLine(offsetWidth + section * progressWidth - 6 * unit, offsetHeight - (float) 1.5 * unit,
                    offsetWidth + section * progressWidth - 1 * unit, offsetHeight - (float) 3.8 * unit, mPaint);

            canvas.drawLine(offsetWidth + section * progressWidth - 6 * unit, offsetHeight + (float) 1.5 * unit,
                    offsetWidth + section * progressWidth - 1 * unit, offsetHeight + (float) 3.8 * unit, mPaint);
        }

      //白色内圆
        mPaint.setShader(null);
        mPaint.setColor(Color.WHITE);
        canvas.drawCircle(offsetWidth + section * progressWidth, offsetHeight, innerCircleDiameter / 2, mPaint);// ��ɫ��Բ

      //绘制文字--百分比
        mPaint.setStrokeWidth(2 * unit);
        mPaint.setColor(textColor);
        mPaint.setTextSize(wordHeight);
      //计算坐标使文字居中
        FontMetrics fontMetrics = mPaint.getFontMetrics();
        float fontHeight = fontMetrics.bottom - fontMetrics.top;
        float baseY = height / 2 + fontHeight / 2 - fontMetrics.bottom;
        canvas.drawText("" + progressValue + "%", progressWidth + 2 * offsetWidth, baseY, mPaint);// ��΢ƫ�£�baseline

    }
}
