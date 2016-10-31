package stan.nvdr.demo;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
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
    private boolean jellyStyle = false;
    private boolean scaleStyle = true;
    private boolean edge = true;
    private float pad = 0;
    private float edgePad = 0;
    private float speedFactor = 1;
    private float iosOffset = 2;
    private float tweaking = 2;

    private AnimatorSet currentAnimation;
    private int durationAnimation = 150;
    private float drawerPosition;
    private float oldPosition = 0;
    private int drawerWidth;

    private boolean drawerOpened;
    private boolean startedTouch;
    private boolean moveProcess;
    private float startedTrackingX;
    private float startedTrackingY;

    private float jellyTouchX;
    private float jellyTouchY;

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
            setIosStyle(a.getBoolean(R.styleable.DrawerContainer_iosStyle, false));
            setIosOffset(a.getFloat(R.styleable.DrawerContainer_speedFactor, 2));
            edge = a.getBoolean(R.styleable.DrawerContainer_edge, false);
            pad = a.getDimension(R.styleable.DrawerContainer_pad, 0);
            edgePad = a.getDimension(R.styleable.DrawerContainer_edgePad, 0);
            setSpeedFactor(a.getFloat(R.styleable.DrawerContainer_speedFactor, 1));
            setTweaking(a.getFloat(R.styleable.DrawerContainer_tweaking, 2));
            scaleStyle = a.getBoolean(R.styleable.DrawerContainer_scaleStyle, false);
            setScaleStyle(scaleStyle);
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
            float mainPosition = drawerPosition + drawerWidth;
            for(int i=0; i<getChildCount(); i++)
            {
                View v = getChildAt(i);
                if(v.equals(drawerLayout))
                {
                    continue;
                }
                if(scaleStyle)
                {
                    float scalingFactor = 1 - ((drawerPosition + drawerWidth)/drawerWidth)/2;
                    Log.e(getClass().getName(), "scalingFactor " + scalingFactor);
                    v.setScaleX(scalingFactor);
                    v.setScaleY(scalingFactor);
                    v.setTranslationX(mainPosition - (getWidth()*(1-scalingFactor))/2);
                }
                else
                {
                    v.setTranslationX(mainPosition);
                }
            }
            drawerLayout.setTranslationX(drawerPosition/getIosOffset());
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
                oldPosition = drawerWidth;
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
    public void moveDrawer(float start, float end, int duration, Animator.AnimatorListener listener)
    {
        if(drawerLayout == null)
        {
            return;
        }
        cancelCurrentAnimation();
        currentAnimation = new AnimatorSet();
        currentAnimation.play(ObjectAnimator.ofFloat(this, "drawerPosition", start, end));
        currentAnimation.setDuration(duration);
        currentAnimation.addListener(listener);
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
                oldPosition = 0;
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
                float x = (ev.getX() - startedTrackingX)*getSpeedFactor();
                if(drawerOpened)
                {
//                    x = drawerWidth - Math.abs(x);
//                    x += drawerWidth;
                    x += (drawerWidth / tweaking)*2;
                }
                Log.e(getClass().getName(), "x " + x + " t " + (drawerWidth / tweaking));
                if(x > (drawerWidth / tweaking))
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
            float newPosition = (ev.getX() - startedTrackingX)*getSpeedFactor();
            if(drawerOpened)
            {
                newPosition += drawerWidth;
            }
            float diff = newPosition - oldPosition;
            if(Math.abs(diff) < drawerWidth/2)
            {
                setDrawerPosition(newPosition);
            }
            else
            {
                moveDrawer(oldPosition, newPosition, (int)Math.abs(diff)/3, new Animator.AnimatorListener()
                {
                    @Override
                    public void onAnimationStart(Animator animation)
                    {
                    }
                    @Override
                    public void onAnimationEnd(Animator animation)
                    {
                    }
                    @Override
                    public void onAnimationCancel(Animator animation)
                    {
                    }
                    @Override
                    public void onAnimationRepeat(Animator animation)
                    {
                    }
                });
            }
            jellyTouchX = ev.getX();
            jellyTouchY = ev.getY();
            oldPosition = newPosition;
            return true;
        }
        return false;
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime)
    {
        if(drawerLayout == null)
        {
            return super.drawChild(canvas, child, drawingTime);
        }
        if(child == getChildAt(0))
        {
            drawChilds(canvas);
        }
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
        float dpos = drawerPosition + drawerWidth;
        float scrimOpacity = dpos / drawerWidth;
        if(jellyStyle)
        {
            scrimOpacity *= 4;
        }
        scrimPaint.setColor((int)(((0x99000000 & 0xff000000) >>> 24) * scrimOpacity) << 24);
        if(scaleStyle)
        {
            float scalingFactor = 1 - ((drawerPosition + drawerWidth)/drawerWidth)/2;
            canvas.drawRect(dpos, (getHeight() - getHeight()*scalingFactor)/2, getWidth(), getHeight() - (getHeight() - getHeight()*scalingFactor)/2, scrimPaint);
        }
        else
        {
            canvas.drawRect(dpos, 0, getWidth(), getHeight(), scrimPaint);
        }
        if(jellyStyle)
        {
            Paint mPaint = new Paint();
            try
            {
                ColorDrawable color = (ColorDrawable) drawerLayout.getBackground();
                mPaint.setColor(color.getColor());
            }
            catch(Exception e)
            {
                mPaint.setColor(Color.BLUE);
            }
            float dx = (drawerPosition + drawerWidth)*8;
            RectF oval3 = new RectF((drawerPosition + drawerWidth)*2 - dx, 0, dx, getHeight());
            canvas.drawOval(oval3, mPaint);
        }
        if(!iosStyle)
        {
            super.drawChild(canvas, drawerLayout, 0);
        }
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
                Log.e(getClass().getName(), "drawerWidth " + drawerWidth);
                Log.e(getClass().getName(), "drawerLayout " + drawerLayout);
                drawerLayout.setLayoutParams(lp);
                setDrawerPosition(0);
            }
        });
        try
        {
            ColorDrawable color = (ColorDrawable) drawerLayout.getBackground();
            setBackgroundColor(color.getColor());
        }
        catch(Exception e)
        {
            Log.e(getClass().getName(), "setDrawerLayout " + e.getMessage());
        }
    }

    public void setRightDrawable(Drawable s)
    {
        this.rightDrawable = s;
    }
    public void setIosStyle(boolean i)
    {
        iosStyle = i;
        if(!iosStyle)
        {
            scaleStyle = false;
        }
        setDrawerPosition(0);
    }
    public void setScaleStyle(boolean s)
    {
        scaleStyle = s;
        if(s)
        {
            setIosStyle(true);
        }
    }
    public void setEdge(boolean e)
    {
        edge = e;
    }

    public void setSpeedFactor(float f)
    {
        speedFactor = f;
    }
    private float getSpeedFactor()
    {
        if(jellyStyle)
        {
            return 0.1f;
        }
        return speedFactor;
    }

    public void setIosOffset(float o)
    {
        iosOffset = o;
        if(iosOffset < 1)
        {
            iosOffset = 1;
        }
        else if(iosOffset > 5)
        {
            iosOffset = 5;
        }
    }
    private float getIosOffset()
    {
        if(scaleStyle)
        {
            return 1f;
        }
        return iosOffset;
    }
    public void setTweaking(float t)
    {
        tweaking = t;
        if(tweaking < 1)
        {
            tweaking = 1;
        }
        else if(tweaking > 10)
        {
            tweaking = 10;
        }
    }

    public interface AnimationEndListener
    {
        void onAnimationEnd();
    }
}