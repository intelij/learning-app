package org.stepic.droid.ui.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import org.stepic.droid.ui.listeners.OnRootTouchedListener;

public class TouchDispatchableFrameLayout extends FrameLayout {

    private OnRootTouchedListener listener = null;

    public TouchDispatchableFrameLayout(Context context) {
        super(context);
    }

    public TouchDispatchableFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TouchDispatchableFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (listener != null) {
            listener.makeBeforeChildren();
        }
        return super.dispatchTouchEvent(ev);
    }

    public void setParentTouchEvent(OnRootTouchedListener listener) {
        this.listener = listener;
    }

}
