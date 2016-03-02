package cn.org.octopus.lib_widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * Created by octopus on 2016/3/1.
 */
public class WaveButton extends RelativeLayout {


    /**
     * 日志打印标志位
     */
    public static final String TAG = "octopus.WaveButton";
    private static final int OFFSET = 600;  //每个动画的播放时间间隔
    private static final int MSG_WAVE2_ANIMATION = 2;
    private static final int MSG_WAVE3_ANIMATION = 3;
    private ImageView mNormal, mWave1, mWave2, mWave3;
    private AnimationSet mAnimationSet1, mAnimationSet2, mAnimationSet3;
    private boolean isTouchWave;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_WAVE2_ANIMATION:
                    mWave2.startAnimation(mAnimationSet2);
                    break;
                case MSG_WAVE3_ANIMATION:
                    mWave3.startAnimation(mAnimationSet3);
                    break;
            }
        }
    };

    private Context context;

    public WaveButton(Context context) {
        super(context);


    }

    public WaveButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.context = context;
        //加载布局
        LayoutInflater.from(context).inflate(R.layout.octopus_wave, this);

        mNormal = (ImageView) findViewById(R.id.normal);
        mWave1 = (ImageView) findViewById(R.id.wave1);
        mWave2 = (ImageView) findViewById(R.id.wave2);
        mWave3 = (ImageView) findViewById(R.id.wave3);

        mAnimationSet1 = initAnimationSet();
        mAnimationSet2 = initAnimationSet();
        mAnimationSet3 = initAnimationSet();

        //是否触摸波纹生效
        if (isTouchWave) {
            mNormal.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            showWaveAnimation();
                            break;
                        case MotionEvent.ACTION_UP:
                            clearWaveAnimation();
                            break;
                        case MotionEvent.ACTION_CANCEL:
                            clearWaveAnimation();
                    }
                    return true;
                }
            });
        }


        showWaveAnimation();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);


    }

    private AnimationSet initAnimationSet() {
        AnimationSet as = new AnimationSet(true);
        ScaleAnimation sa = new ScaleAnimation(1f, 2.3f, 1f, 2.3f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        sa.setDuration(OFFSET * 3);
        sa.setRepeatCount(Animation.INFINITE);// 设置循环
        AlphaAnimation aa = new AlphaAnimation(1, 0.1f);
        aa.setDuration(OFFSET * 3);
        aa.setRepeatCount(Animation.INFINITE);//设置循环
        as.addAnimation(sa);
        as.addAnimation(aa);
        return as;
    }

    private void showWaveAnimation() {
        mWave1.startAnimation(mAnimationSet1);
        mHandler.sendEmptyMessageDelayed(MSG_WAVE2_ANIMATION, OFFSET);
        mHandler.sendEmptyMessageDelayed(MSG_WAVE3_ANIMATION, OFFSET * 2);
    }

    private void clearWaveAnimation() {
        mWave1.clearAnimation();
        mWave2.clearAnimation();
        mWave3.clearAnimation();
    }


}
