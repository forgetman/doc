package dsb.view;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.TextSwitcher;
import android.widget.TextView;

import androidx.annotation.ColorInt;

import java.util.concurrent.TimeUnit;

import vector.os.DimensionKt;
import vector.os.weak.WeakHandler;

/**
 * 自动垂直滚动的TextView
 */
public class AutoVerticalScrollTextView extends TextSwitcher {

    private final Context mContext;

    //mInUp,mOutUp分别构成向下翻页的进出动画
    private Rotate3dAnimation mInUp;
    private Rotate3dAnimation mOutUp;

    private String[] mTexts;
    private final long mInterval = TimeUnit.SECONDS.toMillis(3);
    private WeakHandler<View> mHandler;
    private int mIndex;
    private int mMax;

    /**
     * text view attr
     * FIXME: 暂时只有一个地方使用, 先设置死, 看情况改成自定义属性
     */
    @ColorInt
    private final int mColor = Color.parseColor("#2b2b2b");
    private float mTextSize;

    public AutoVerticalScrollTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mContext = context;
        init();
    }

    private void init() {
        mHandler = new WeakHandler<>(this, message -> {
            next();
            mIndex++;
            if (mIndex >= mMax) {
                mIndex = 0;
            }
            setText(mTexts[mIndex]);
            mHandler.sendEmptyMessageDelayed(0, mInterval);
            return null;
        });

        mTextSize = DimensionKt.getDp(12).toPx(mContext);

        mInUp = createAnim(true, true);
        mOutUp = createAnim(false, true);

        setInAnimation(mInUp);//当View显示时动画资源ID
        setOutAnimation(mOutUp);//当View隐藏是动画资源ID。

        setFactory(() -> {
            TextView tv = new TextView(mContext);
            tv.setSingleLine(true);
            tv.setGravity(Gravity.CENTER_VERTICAL);
            tv.setEllipsize(TextUtils.TruncateAt.END);
            tv.setTextColor(mColor);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
            return tv;
        });
    }

    private Rotate3dAnimation createAnim(boolean turnIn, boolean turnUp) {

        Rotate3dAnimation rotation = new Rotate3dAnimation(turnIn, turnUp);
        rotation.setDuration(1200);//执行动画的时间
        rotation.setFillAfter(false);//是否保持动画完毕之后的状态
        rotation.setInterpolator(new AccelerateInterpolator());//设置加速模式

        return rotation;
    }

    public void setTexts(String[] texts) {
        if (texts == null) {
            return;
        }
        mTexts = texts;
        mIndex = 0;
        mMax = texts.length;
        setText(mTexts[mIndex]);
    }

    //定义动作，向上滚动翻页
    private void next() {
        //显示动画
        if (getInAnimation() != mInUp) {
            setInAnimation(mInUp);
        }
        //隐藏动画
        if (getOutAnimation() != mOutUp) {
            setOutAnimation(mOutUp);
        }
    }

    public void start() {
        if (mTexts == null || mTexts.length == 0) {
            return;
        }
        mHandler.removeMessages(0);
        mHandler.sendEmptyMessage(0);
    }

    public void stop() {
        mHandler.removeMessages(0);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stop();
    }

    class Rotate3dAnimation extends Animation {
        private float mCenterX;
        private float mCenterY;
        private final boolean mTurnIn;
        private final boolean mTurnUp;
        private Camera mCamera;

        public Rotate3dAnimation(boolean turnIn, boolean turnUp) {
            mTurnIn = turnIn;
            mTurnUp = turnUp;
        }

        @Override
        public void initialize(int width, int height, int parentWidth, int parentHeight) {
            super.initialize(width, height, parentWidth, parentHeight);
            mCamera = new Camera();
            mCenterY = getHeight();
            mCenterX = getWidth();
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {

            final float centerX = mCenterX;
            final float centerY = mCenterY;
            final Camera camera = mCamera;
            final int direction = mTurnUp ? 1 : -1;

            final Matrix matrix = t.getMatrix();

            camera.save();
            if (mTurnIn) {
                camera.translate(0.0f, direction * mCenterY * (interpolatedTime - 1.0f), 0.0f);
            } else {
                camera.translate(0.0f, direction * mCenterY * (interpolatedTime), 0.0f);
            }
            camera.getMatrix(matrix);
            camera.restore();

            matrix.preTranslate(-centerX, -centerY);
            matrix.postTranslate(centerX, centerY);
        }
    }

    private class SwitchHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            next();
            mIndex++;
            if (mIndex >= mMax) {
                mIndex = 0;
            }
            setText(mTexts[mIndex]);
            sendEmptyMessageDelayed(0, mInterval);
        }
    }
}
