package vector.widget.scrollable;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import logger.L;

public class ExpandableListViewEx extends ExpandableListView {

    private static final int[] EMPTY_STATE_SET = {};

    // State indicating the group is expanded
    private static final int[] GROUP_EXPANDED_STATE_SET = {android.R.attr.state_expanded};

    // State indicating the group is empty (has no children)
    private static final int[] GROUP_EMPTY_STATE_SET = {android.R.attr.state_empty};

    // State indicating the group is expanded and empty (has no children)
    private static final int[] GROUP_EXPANDED_EMPTY_STATE_SET = {android.R.attr.state_expanded, android.R.attr.state_empty};

    // States for the group where the 0th bit is expanded and 1st bit is empty.
    private static final int[][] GROUP_STATE_SETS = {EMPTY_STATE_SET, // 00
            GROUP_EXPANDED_STATE_SET, // 01
            GROUP_EMPTY_STATE_SET, // 10
            GROUP_EXPANDED_EMPTY_STATE_SET // 11
    };

    private BaseExpandableListAdapter mAdapter;
    //    private DataSetObserver mDataSetObserver;
    private OnScrollListener mOnScrollListener;

    // By default, the floating group is enabled
    private boolean mFloatingGroupEnabled = true;
    private View mFloatingGroupView;
    private int mFloatingGroupPosition;
    private int mFloatingGroupScrollY;
    private OnScrollFloatingGroupListener mOnScrollFloatingGroupListener;
    private OnGroupClickListener mOnGroupClickListener;

    private int mWidthMeasureSpec;

    // An AttachInfo instance is added to the FloatingGroupView in order to have
    // proper touch event handling
    private Object mViewAttachInfo;
    private boolean mHandledByOnInterceptTouchEvent;
    private boolean mHandledByOnTouchEvent;
    private Runnable mOnClickAction;
    private GestureDetector mGestureDetector;

    private boolean mSelectorEnabled;
    private boolean mShouldPositionSelector;
    private boolean mDrawSelectorOnTop;
    private Drawable mSelector;
    private int mSelectorPosition;
    private final Rect mSelectorRect = new Rect();
    private Runnable mPositionSelectorOnTapAction;
    private Runnable mClearSelectorOnTapAction;

    private final Rect mIndicatorRect = new Rect();

    public ExpandableListViewEx(Context context) {
        super(context);
        init();
    }

    public ExpandableListViewEx(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ExpandableListViewEx(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        if (isInEditMode()) {
            return;
        }

        super.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (mOnScrollListener != null) {
                    mOnScrollListener.onScrollStateChanged(view, scrollState);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (mOnScrollListener != null) {
                    mOnScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
                }

                if (mFloatingGroupEnabled && mAdapter != null && mAdapter.getGroupCount() > 0 && visibleItemCount > 0 && firstVisibleItem >= getHeaderViewsCount()) {
                    createFloatingGroupView(firstVisibleItem);
                } else {
                    if (mFloatingGroupView != null) {
                        mFloatingGroupView.setVisibility(View.GONE);
                    }
                }
            }
        });

        mOnClickAction = () -> {
            boolean allowSelection = true;

            if (mOnGroupClickListener != null) {
                allowSelection = !mOnGroupClickListener.onGroupClick(ExpandableListViewEx.this, mFloatingGroupView, mFloatingGroupPosition,
                        mAdapter.getGroupId(mFloatingGroupPosition));
            }

            if (allowSelection) {
                if (isGroupExpanded(mFloatingGroupPosition)) {
                    collapseGroup(mFloatingGroupPosition);
                } else {
                    expandGroup(mFloatingGroupPosition);
                }

                setSelectedGroup(mFloatingGroupPosition);
            }
        };

        mPositionSelectorOnTapAction = new Runnable() {

            @Override
            public void run() {
                positionSelectorOnFloatingGroup();
                setPressed(true);
                // 取消设置, 因为响应某个按钮click消息的时候应该是不响应整个floatingview的press消息的
                // if (mFloatingGroupView != null) {
                // mFloatingGroupView.setPressed(true);
                // }
            }
        };

        mClearSelectorOnTapAction = new Runnable() {

            @Override
            public void run() {
                setPressed(false);
                if (mFloatingGroupView != null) {
                    mFloatingGroupView.setPressed(false);
                }
                invalidate();
            }
        };

        mGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {

            @Override
            public void onLongPress(MotionEvent e) {
                if (mFloatingGroupView != null && !mFloatingGroupView.isLongClickable()) {
                    final ContextMenuInfo contextMenuInfo = new ExpandableListContextMenuInfo(mFloatingGroupView, getPackedPositionForGroup(mFloatingGroupPosition),
                            mAdapter.getGroupId(mFloatingGroupPosition));
                    setFieldValue(AbsListView.class, "mContextMenuInfo", ExpandableListViewEx.this, contextMenuInfo);
                    showContextMenu();
                }
            }
        });
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

//        if (mAdapter != null && mDataSetObserver != null) {
//            mAdapter.unregisterDataSetObserver(mDataSetObserver);
//            mDataSetObserver = null;
//        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidthMeasureSpec = widthMeasureSpec;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        // Reflection is used here to obtain info about the selector
        Object position = getFieldValue(AbsListView.class, "mSelectorPosition", ExpandableListViewEx.this);
        if (position != null) {
            mSelectorPosition = (Integer) position;
        }

        Object rect = getFieldValue(AbsListView.class, "mSelectorRect", ExpandableListViewEx.this);
        if (rect != null) {
            mSelectorRect.set((Rect) rect);
        }

        if (!mDrawSelectorOnTop) {
            drawDefaultSelector(canvas);
        }

        super.dispatchDraw(canvas);

        if (mFloatingGroupEnabled && mFloatingGroupView != null) {
            if (!mDrawSelectorOnTop) {
                drawFloatingGroupSelector(canvas);
            }

            canvas.save();
            canvas.clipRect(getPaddingLeft(), getPaddingTop(), getWidth() - getPaddingRight(), getHeight() - getPaddingBottom());
            if (mFloatingGroupView.getVisibility() == View.VISIBLE) {
                drawChild(canvas, mFloatingGroupView, getDrawingTime());
            }
            drawFloatingGroupIndicator(canvas);
            canvas.restore();

            if (mDrawSelectorOnTop) {
                drawDefaultSelector(canvas);
                drawFloatingGroupSelector(canvas);
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (!mFloatingGroupEnabled || mFloatingGroupView == null || mFloatingGroupView.getVisibility() != View.VISIBLE) {
            return super.dispatchTouchEvent(ev);
        }

        final int action = ev.getAction() & MotionEvent.ACTION_MASK;

        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_CANCEL) {
            mHandledByOnInterceptTouchEvent = false;
            mHandledByOnTouchEvent = false;
            mShouldPositionSelector = false;
        }

        // If touch events are being handled by onInterceptTouchEvent() or
        // onTouchEvent() we shouldn't dispatch them to the floating group
        if (!mHandledByOnInterceptTouchEvent && !mHandledByOnTouchEvent) {
            final int screenCoords[] = new int[2];
            getLocationInWindow(screenCoords);
            final RectF floatingGroupRect = new RectF(screenCoords[0] + mFloatingGroupView.getLeft(), screenCoords[1] + mFloatingGroupView.getTop(), screenCoords[0]
                    + mFloatingGroupView.getRight(), screenCoords[1] + mFloatingGroupView.getBottom());

            if (floatingGroupRect.contains(ev.getRawX(), ev.getRawY())) {
                if (mSelectorEnabled) {
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            mShouldPositionSelector = true;
                            removeCallbacks(mPositionSelectorOnTapAction);
                            postDelayed(mPositionSelectorOnTapAction, ViewConfiguration.getTapTimeout());
                            break;
                        case MotionEvent.ACTION_UP:
                            positionSelectorOnFloatingGroup();
                            setPressed(true);
                            if (mFloatingGroupView != null) {
                                mFloatingGroupView.setPressed(true);
                            }

                            removeCallbacks(mClearSelectorOnTapAction);
                            postDelayed(mClearSelectorOnTapAction, ViewConfiguration.getPressedStateDuration());

                            break;
                        default:
                            // do nothing
                            break;
                    }
                }

                // 响应浮窗点击消息
                if (mFloatingGroupView.dispatchTouchEvent(ev)) {
                    mGestureDetector.onTouchEvent(ev);
                    onInterceptTouchEvent(ev);
                    return true;
                }
            }
        }

        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        mHandledByOnInterceptTouchEvent = super.onInterceptTouchEvent(ev);
        return mHandledByOnInterceptTouchEvent;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        mHandledByOnTouchEvent = super.onTouchEvent(ev);
        // 在这里把press背景响应还原
        if (mFloatingGroupView != null && mFloatingGroupView.getVisibility() == View.VISIBLE) {
            mFloatingGroupView.setPressed(false);
        }
        return mHandledByOnTouchEvent;
    }

    @Override
    public void setSelector(Drawable sel) {
        super.setSelector(new ColorDrawable(Color.TRANSPARENT));
        if (mSelector != null) {
            mSelector.setCallback(null);
            unscheduleDrawable(mSelector);
        }
        mSelector = sel;
        mSelector.setCallback(this);
    }

    @Override
    public void setDrawSelectorOnTop(boolean onTop) {
        super.setDrawSelectorOnTop(onTop);
        mDrawSelectorOnTop = onTop;
    }

    @Override
    public void setAdapter(ExpandableListAdapter adapter) {
        if (!(adapter instanceof BaseExpandableListAdapter)) {
            throw new IllegalArgumentException("The adapter must be an instance of BaseExpandableListAdapter");
        }
        setAdapter((BaseExpandableListAdapter) adapter);
    }

    public void setAdapter(BaseExpandableListAdapter adapter) {
        super.setAdapter(adapter);

//        if (mAdapter != null && mDataSetObserver != null) {
//            mAdapter.unregisterDataSetObserver(mDataSetObserver);
//            mDataSetObserver = null;
//        }
//
        mAdapter = adapter;
//
//        if (mAdapter != null && mDataSetObserver == null) {
//            mDataSetObserver = new DataSetObserver() {
//                @Override
//                public void onChanged() {
//                    mFloatingGroupView = null;
//                }
//
//                @Override
//                public void onInvalidated() {
//                    mFloatingGroupView = null;
//                }
//            };
//            mAdapter.registerDataSetObserver(mDataSetObserver);
//        }
    }

    @Override
    public void setOnScrollListener(OnScrollListener listener) {
        mOnScrollListener = listener;
    }

    @Override
    public void setOnGroupClickListener(OnGroupClickListener onGroupClickListener) {
        super.setOnGroupClickListener(onGroupClickListener);
        mOnGroupClickListener = onGroupClickListener;
    }

    public void setFloatingGroupEnabled(boolean floatingGroupEnabled) {
        mFloatingGroupEnabled = floatingGroupEnabled;
    }

    public void setOnScrollFloatingGroupListener(OnScrollFloatingGroupListener listener) {
        mOnScrollFloatingGroupListener = listener;
    }

    /**
     * 创建GroupView
     *
     * @param position
     */
    private void createFloatingGroupView(int position) {
        if (!mFloatingGroupEnabled) {
            return;
        }

        mFloatingGroupPosition = getPackedPositionGroup(getExpandableListPosition(position));

        if (mFloatingGroupPosition >= 0) {
            mFloatingGroupView = mAdapter.getGroupView(mFloatingGroupPosition, isGroupExpanded(mFloatingGroupPosition), mFloatingGroupView, this);
            if (mFloatingGroupView == null) {
                return;
            }

            if (isGroupExpanded(mFloatingGroupPosition)) {
                mFloatingGroupView.setVisibility(VISIBLE);
            } else {
                mFloatingGroupView.setVisibility(GONE);
                return;
            }

            LayoutParams params = (LayoutParams) mFloatingGroupView.getLayoutParams();
            if (params == null) {
                params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0);
                mFloatingGroupView.setLayoutParams(params);
            }

            final int childWidthSpec = ViewGroup.getChildMeasureSpec(mWidthMeasureSpec, getPaddingLeft() + getPaddingRight(), params.width);
            final int paramsHeight = params.height;
            int childHeightSpec;
            if (paramsHeight > 0) {
                childHeightSpec = MeasureSpec.makeMeasureSpec(paramsHeight, MeasureSpec.EXACTLY);
            } else {
                childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
            }

            mFloatingGroupView.measure(childWidthSpec, childHeightSpec);

            loadAttachInfo();
            setAttachInfo(mFloatingGroupView);

            if (!mFloatingGroupView.isClickable()) {
                mSelectorEnabled = true;
                mFloatingGroupView.setOnClickListener(v -> postDelayed(mOnClickAction, ViewConfiguration.getPressedStateDuration()));
            } else {
                mSelectorEnabled = false;
            }
        }

        if (mFloatingGroupView == null) {
            return;
        }

        int floatingGroupScrollY = 0;

        final int nextGroupFlatPosition = getFlatListPosition(getPackedPositionForGroup(mFloatingGroupPosition + 1));
        final int nextGroupListPosition = nextGroupFlatPosition - position;

        if (nextGroupListPosition >= 0 && nextGroupListPosition < getChildCount()) {
            final View nextGroupView = getChildAt(nextGroupListPosition);

            if (nextGroupView != null && nextGroupView.getTop() < getPaddingTop() + mFloatingGroupView.getMeasuredHeight() + getDividerHeight()) {
                floatingGroupScrollY = nextGroupView.getTop() - (getPaddingTop() + mFloatingGroupView.getMeasuredHeight() + getDividerHeight());
            }
        }

        final int start = getPaddingStart();
        final int top = getPaddingTop() + floatingGroupScrollY;
        final int end = start + mFloatingGroupView.getMeasuredWidth();
        final int bottom = top + mFloatingGroupView.getMeasuredHeight();
        mFloatingGroupView.layout(start, top, end, bottom);

        mFloatingGroupScrollY = floatingGroupScrollY;
        if (mOnScrollFloatingGroupListener != null) {
            mOnScrollFloatingGroupListener.onScrollFloatingGroupListener(mFloatingGroupView, mFloatingGroupScrollY);
        }
    }

    private void loadAttachInfo() {
        if (mViewAttachInfo == null) {
            mViewAttachInfo = getFieldValue(View.class, "mAttachInfo", ExpandableListViewEx.this);
        }
    }

    /**
     * 设置所有依附的信息, 包括点击事件的设置等
     *
     * @param v
     */
    private void setAttachInfo(View v) {
        if (v == null) {
            return;
        }
        if (mViewAttachInfo != null) {
            setFieldValue(View.class, "mAttachInfo", v, mViewAttachInfo);
        }
        if (v instanceof ViewGroup) {
            final ViewGroup viewGroup = (ViewGroup) v;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                setAttachInfo(viewGroup.getChildAt(i));
            }
        }
    }

    private void positionSelectorOnFloatingGroup() {
        if (mShouldPositionSelector && mFloatingGroupView != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                final int floatingGroupFlatPosition = getFlatListPosition(getPackedPositionForGroup(mFloatingGroupPosition));
                invokeMethod(AbsListView.class, "positionSelector", new Class<?>[]{int.class, View.class}, ExpandableListViewEx.this,
                        floatingGroupFlatPosition, mFloatingGroupView);
            } else {
                invokeMethod(AbsListView.class, "positionSelector", new Class<?>[]{View.class}, ExpandableListViewEx.this, mFloatingGroupView);
            }
            invalidate();
        }
        mShouldPositionSelector = false;
        removeCallbacks(mPositionSelectorOnTapAction);
    }

    private void drawDefaultSelector(Canvas canvas) {
        final int selectorListPosition = mSelectorPosition - getFirstVisiblePosition();

        if (selectorListPosition >= 0 && selectorListPosition < getChildCount() && mSelectorRect != null && !mSelectorRect.isEmpty()) {
            final int floatingGroupFlatPosition = getFlatListPosition(getPackedPositionForGroup(mFloatingGroupPosition));
            if (mFloatingGroupView == null || mSelectorPosition != floatingGroupFlatPosition) {
                drawSelector(canvas);
            }
        }
    }

    private void drawFloatingGroupSelector(Canvas canvas) {
        if (mSelectorRect != null && !mSelectorRect.isEmpty()) {
            final int floatingGroupFlatPosition = getFlatListPosition(getPackedPositionForGroup(mFloatingGroupPosition));
            if (mSelectorPosition == floatingGroupFlatPosition) {
                mSelectorRect.set(mFloatingGroupView.getLeft(), mFloatingGroupView.getTop(), mFloatingGroupView.getRight(), mFloatingGroupView.getBottom());
                drawSelector(canvas);
            }
        }
    }

    private void drawSelector(Canvas canvas) {
        canvas.save();
        canvas.clipRect(getPaddingLeft(), getPaddingTop(), getWidth() - getPaddingRight(), getHeight() - getPaddingBottom());
        if (isPressed()) {
            mSelector.setState(getDrawableState());
        } else {
            mSelector.setState(EMPTY_STATE_SET);
        }
        mSelector.setBounds(mSelectorRect);
        mSelector.draw(canvas);
        canvas.restore();
    }

    private void drawFloatingGroupIndicator(Canvas canvas) {
        final Drawable groupIndicator = (Drawable) getFieldValue(ExpandableListView.class, "mGroupIndicator", ExpandableListViewEx.this);
        if (groupIndicator != null) {
            final int stateSetIndex = (isGroupExpanded(mFloatingGroupPosition) ? 1 : 0) | // Expanded?
                    (mAdapter.getChildrenCount(mFloatingGroupPosition) > 0 ? 2 : 0); // Empty?
            groupIndicator.setState(GROUP_STATE_SETS[stateSetIndex]);

            final int indicatorLeft = (Integer) getFieldValue(ExpandableListView.class, "mIndicatorLeft", ExpandableListViewEx.this);
            final int indicatorRight = (Integer) getFieldValue(ExpandableListView.class, "mIndicatorRight", ExpandableListViewEx.this);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                mIndicatorRect.set(indicatorLeft + getPaddingLeft(), mFloatingGroupView.getTop(), indicatorRight + getPaddingLeft(), mFloatingGroupView.getBottom());
            } else {
                mIndicatorRect.set(indicatorLeft, mFloatingGroupView.getTop(), indicatorRight, mFloatingGroupView.getBottom());
            }

            groupIndicator.setBounds(mIndicatorRect);
            groupIndicator.draw(canvas);
        }
    }

    public interface OnScrollFloatingGroupListener {
        void onScrollFloatingGroupListener(View floatingGroupView, int scrollY);
    }

    public static Object getFieldValue(Class<?> fieldClass, String fieldName, Object instance) {
        try {
            final Field field = fieldClass.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(instance);
        } catch (Exception e) {
            L.INSTANCE.e(e);
        }
        return null;
    }

    public static void setFieldValue(Class<?> fieldClass, String fieldName, Object instance, Object value) {
        try {
            final Field field = fieldClass.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(instance, value);
        } catch (Exception e) {
//            LogMgr.d(TAG, e);
        }
    }

    public static Object invokeMethod(Class<?> methodClass, String methodName, Class<?>[] parameters, Object instance, Object... arguments) {
        try {
            final Method method = methodClass.getDeclaredMethod(methodName, parameters);
            method.setAccessible(true);
            return method.invoke(instance, arguments);
        } catch (Exception e) {
//            LogMgr.d(TAG, e);
        }
        return null;
    }
}
