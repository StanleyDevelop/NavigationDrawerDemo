package stan.nvdr.demo;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class DrawerContainer
        extends FrameLayout
{
    private View drawerLayout;

    private float density;
    private boolean iosStyle = true;
    private boolean edge = true;
    private float pad = 0;
    private float edgePad = 0;
    private float speedFactor = 1;

    private AnimatorSet currentAnimation;
    private int durationAnimation = 150;
    private float drawerPosition;
    private int drawerWidth;

    private boolean drawerOpened;
    private boolean startedTouch;
    private boolean moveProcess;
    private float startedTrackingX;
    private float startedTrackingY;

    private Drawable rightDrawable;

    private Paint scrimPaint = new Paint();

    public DrawerContainer(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.DrawerContainer,
                0, 0);
        try
        {
            iosStyle = a.getBoolean(R.styleable.DrawerContainer_iosStyle, false);
            edge = a.getBoolean(R.styleable.DrawerContainer_edge, false);
            pad = a.getDimension(R.styleable.DrawerContainer_pad, 0);
            edgePad = a.getDimension(R.styleable.DrawerContainer_edgePad, 0);
            speedFactor = a.getFloat(R.styleable.DrawerContainer_speedFactor, 1);
        }
        finally
        {
            a.recycle();
        }
        density = context.getResources().getDisplayMetrics().density;
        post(new Runnable()
        {
            @Override
            public void run()
            {
                findDrawer();
            }
        });
    }

    private void findDrawer()
    {
        for(int i = 0; i < getChildCount(); i++)
        {
            View v = getChildAt(i);
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams)v.getLayoutParams();
            if(lp.gravity == Gravity.START)
            {
                setDrawerLayout(v);
                return;
            }
        }
    }

    public void setDrawerPosition(float value)
    {
        if(drawerLayout == null)
        {
            return;
        }
//        Log.e(this.getClass().getCanonicalName(), "value " + value);
        drawerPosition = value - drawerWidth;
        if(drawerPosition > 0)
        {
            drawerPosition = 0;
        }
        else if(drawerPosition < -drawerWidth)
        {
            drawerPosition = -drawerWidth;
        }
        if(iosStyle)
        {
            for(int i=0; i<getChildCount(); i++)
            {
                View v = getChildAt(i);
                if(v.equals(drawerLayout))
                {
                    continue;
                }
                v.setTranslationX(drawerPosition + drawerWidth);
            }
            drawerLayout.setTranslationX(drawerPosition/2);
        }
        else
        {
            drawerLayout.setTranslationX(drawerPosition);
        }
        invalidate();
    }

    public void cancelCurrentAnimation()
    {
        if(currentAnimation != null)
        {
            currentAnimation.cancel();
            currentAnimation = null;
        }
    }

    public void openDrawer()
    {
        if(drawerLayout == null)
        {
            return;
        }
        cancelCurrentAnimation();
        currentAnimation = new AnimatorSet();
        currentAnimation.play(ObjectAnimator.ofFloat(this, "drawerPosition", drawerPosition + drawerWidth, drawerWidth));
        currentAnimation.setDuration(durationAnimation);
        currentAnimation.addListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart(Animator animator)
            {

            }

            @Override
            public void onAnimationEnd(Animator animator)
            {
                drawerOpened = true;
            }

            @Override
            public void onAnimationCancel(Animator animator)
            {

            }

            @Override
            public void onAnimationRepeat(Animator animator)
            {

            }
        });
        currentAnimation.start();
    }

    public void closeDrawer(final AnimationEndListener listener)
    {
        if(drawerLayout == null)
        {
            return;
        }
        cancelCurrentAnimation();
        currentAnimation = new AnimatorSet();
        currentAnimation.play(ObjectAnimator.ofFloat(this, "drawerPosition", drawerPosition + drawerWidth, 0));
        currentAnimation.setDuration(durationAnimation);
        currentAnimation.addListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart(Animator animator)
            {

            }

            @Override
            public void onAnimationEnd(Animator animator)
            {
                drawerOpened = false;
                if(listener != null)
                {
                    listener.onAnimationEnd();
                }
            }

            @Override
            public void onAnimationCancel(Animator animator)
            {

            }

            @Override
            public void onAnimationRepeat(Animator animator)
            {

            }
        });
        currentAnimation.start();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev)
    {
        if(drawerOpened && ev.getX() > drawerPosition + drawerWidth)
        {
            if(ev.getAction() == MotionEvent.ACTION_UP && !moveProcess)
            {
                onTouchEvent(ev);
            }
            return true;
        }
        switch(ev.getAction())
        {
            case MotionEvent.ACTION_DOWN:
            {
                onTouchEvent(ev);
                return super.onInterceptTouchEvent(ev);
            }
            case MotionEvent.ACTION_MOVE:
            {
                boolean intercept = super.onInterceptTouchEvent(ev);
                if(intercept)
                {
                    return true;
                }
                else if(startedTouch && !moveProcess)
                {
                    float x = ev.getX() - startedTrackingX;
                    float y = ev.getY() - startedTrackingY;
                    double a = Math.sqrt(x*x + y*y);
//                    Log.e(this.getClass()
//                              .getCanonicalName(), "x " + x + " y " + y + " a " + a);
                    if(!drawerOpened && x < 0)
                    {
                        return super.onInterceptTouchEvent(ev);
                    }
                    if(drawerOpened && x > 0)
                    {
                        return super.onInterceptTouchEvent(ev);
                    }
                    if(Math.abs(x) < 2)
                    {
                    }
                    else if(Math.abs(y) < Math.abs(x)-1)
                    {
                        Log.e(this.getClass()
                                  .getCanonicalName(), "move");
                        onTouchEvent(ev);
                    }
                    else
                    {
                        startedTouch = false;
                        moveProcess = false;
                        if(!drawerOpened)
                        {
                            closeDrawer(null);
                        }
                    }
                }
                else if(moveProcess)
                {
                    onTouchEvent(ev);
                    return true;
                }
                return false;
            }
            case MotionEvent.ACTION_CANCEL:
            {
                return super.onInterceptTouchEvent(ev);
            }
            case MotionEvent.ACTION_UP:
            {
                onTouchEvent(ev);
                return super.onInterceptTouchEvent(ev);
            }
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev)
    {
        if(drawerOpened && ev.getX() > drawerPosition + drawerWidth && !moveProcess)
        {
            if(ev.getAction() == MotionEvent.ACTION_UP)
            {
                startedTouch = false;
                moveProcess = false;
                closeDrawer(null);
                return true;
            }
        }
        if(ev.getAction() == MotionEvent.ACTION_UP)
        {
            startedTouch = false;
            if(moveProcess)
            {
                moveProcess = false;
                float x = (ev.getX() - startedTrackingX)*speedFactor;
                if(drawerOpened)
                {
                    x += drawerWidth;
                }
                if(x > (drawerWidth / 2))
                {
                    openDrawer();
                }
                else
                {
                    closeDrawer(null);
                }
            }
            return false;
        }
        if(ev.getAction() == MotionEvent.ACTION_DOWN)
        {
            Log.e(getClass().getName(), "MotionEvent.ACTION_DOWN x " + ev.getX() + " y " + ev.getY());
            if(edge && !drawerOpened && ev.getX() > edgePad)
            {
                return false;
            }
            startedTouch = true;
            startedTrackingX = ev.getX();
            startedTrackingY = ev.getY();
            return true;
        }
        if(ev.getAction() == MotionEvent.ACTION_MOVE && startedTouch)
        {
            moveProcess = true;
            if(drawerOpened)
            {
                setDrawerPosition(drawerWidth + (ev.getX() - startedTrackingX)*speedFactor);
            }
            else
            {
                setDrawerPosition((ev.getX() - startedTrackingX)*speedFactor);
            }
            return true;
        }
        return false;
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime)
    {
        if(child == drawerLayout)
        {
            drawChilds(canvas);
        }
//        boolean result = super.drawChild(canvas, child, drawingTime);
//        float dpos = drawerPosition + drawerWidth;
//        if(child == drawerLayout)
//        {
//            float scrimOpacity = dpos / drawerWidth;
//            scrimPaint.setColor((int)(((0x99000000 & 0xff000000) >>> 24) * scrimOpacity) << 24);
//            canvas.drawRect(dpos, 0, getWidth(), getHeight(), scrimPaint);
//            if(rightDrawable != null)
//            {
//                float alpha = Math.max(0, Math.min(dpos / dp(20), 1.0f));
//                if(alpha != 0)
//                {
//                    rightDrawable.setBounds((int)dpos, child.getTop(), (int)dpos + rightDrawable.getIntrinsicWidth(), child.getBottom());
//                    rightDrawable.setAlpha((int)(0xff * alpha));
//                    rightDrawable.draw(canvas);
//                }
//            }
//        }
//        return result;
        return true;
    }

    private void drawChilds(Canvas canvas)
    {
        if(iosStyle)
        {
            super.drawChild(canvas, drawerLayout, 0);
        }
        for(int i=0; i<getChildCount(); i++)
        {
            if(getChildAt(i) == drawerLayout)
            {
                continue;
            }
            super.drawChild(canvas, getChildAt(i), 0);
        }
        if(!iosStyle)
        {
            super.drawChild(canvas, drawerLayout, 0);
        }
        float dpos = drawerPosition + drawerWidth;
        float scrimOpacity = dpos / drawerWidth;
        scrimPaint.setColor((int)(((0x99000000 & 0xff000000) >>> 24) * scrimOpacity) << 24);
        canvas.drawRect(dpos, 0, getWidth(), getHeight(), scrimPaint);
    }

    private void setDrawerLayout(View d)
    {
        this.drawerLayout = d;
        this.drawerLayout.post(new Runnable()
        {
            @Override
            public void run()
            {
                ViewGroup.LayoutParams lp = drawerLayout.getLayoutParams();
                drawerWidth = getMeasuredWidth() - (int)pad;
                lp.width = drawerWidth;
                drawerLayout.setLayoutParams(lp);
                setDrawerPosition(0);
            }
        });
    }

    public void setRightDrawable(Drawable s)
    {
        this.rightDrawable = s;
    }
    public void setIosStyle(boolean i)
    {
        iosStyle = i;
        setDrawerPosition(0);
    }
    public void setEdge(boolean e)
    {
        edge = e;
    }

    public void setSpeedFactor(float f)
    {
        speedFactor = f;
    }

    public interface AnimationEndListener
    {
        void onAnimationEnd();
    }
}