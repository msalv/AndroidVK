package org.kirillius.friendslist.ui;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.kirillius.friendslist.R;
import org.kirillius.friendslist.core.AndroidUtilities;

/**
 * Created by Kirill Bykov on 18.01.2016.
 */
public class DialogCellView extends ViewGroup {

    private static int COLOR_GRAY = 0xffe6e9f0;
    private static int COLOR_BLUE = 0xffd8e5f5;

    private GradientDrawable mBackgroundDrawable;

    private View mBackgroundView;
    public TextView textView;
    public ImageView image;

    private boolean mIsOutgoing = false;

    public DialogCellView(Context context) {
        super(context);
        init(context);
    }

    public DialogCellView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DialogCellView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {

        mBackgroundView = new View(context);

        mBackgroundDrawable = new GradientDrawable();
        mBackgroundDrawable.setColor(COLOR_GRAY);
        mBackgroundDrawable.setCornerRadius(dp(6));

        if (Build.VERSION.SDK_INT < 16) {
            mBackgroundView.setBackgroundDrawable(mBackgroundDrawable);
        }
        else {
            mBackgroundView.setBackground(mBackgroundDrawable);
        }

        addView(mBackgroundView);

        LayoutParams lp = (DialogCellView.LayoutParams) mBackgroundView.getLayoutParams();
        lp.width = LayoutParams.MATCH_PARENT;

        //lp.rightMargin = dp(48);
        //lp.leftMargin = 0;
        lp.topMargin = dp(8);

        mBackgroundView.setLayoutParams(lp);

        textView = new TextView(context);
        textView.setPadding(dp(16), dp(8), dp(16), dp(16));
        textView.setVisibility(GONE);
        textView.setTextColor(0xff2e3033);

        addView(textView);

        lp = (DialogCellView.LayoutParams) textView.getLayoutParams();
        lp.width = LayoutParams.MATCH_PARENT;
        lp.height = LayoutParams.WRAP_CONTENT;

        //lp.rightMargin = dp(48);
        //lp.leftMargin = 0;
        lp.topMargin = dp(16);

        textView.setLayoutParams(lp);

        image = new ImageView(context);

        image.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        image.setImageResource(R.drawable.ic_image);
        image.setBackgroundColor(0xffdfe6ee);
        image.setVisibility(View.GONE);

        addView(image);

        lp = (DialogCellView.LayoutParams) image.getLayoutParams();
        lp.width = dp(100);
        lp.height = dp(100);

        //lp.rightMargin = dp(56);
        //lp.leftMargin = dp(8);
        lp.topMargin = dp(16);
        lp.bottomMargin = dp(8);

        image.setLayoutParams(lp);

        stickToSide();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        // If parent would provide us a known width or height and if one of them is zero-size
        // than we should skip all measure stuff, because we will not show anything
        if (widthMode != MeasureSpec.UNSPECIFIED && widthSize == 0
                || heightMode != MeasureSpec.UNSPECIFIED && heightSize == 0) {
            setMeasuredDimension(widthSize, heightSize);
            return;
        }

        // Reserve any width or height for your own needs
        int widthUsedByParent = 0;
        int heightUsedByParent = 0;

        // Measure children
        int childrenMaxWidth = 0;
        int childrenAllHeight = 0;

        // measure text view
        measureChildWithMargins(textView, widthMeasureSpec, widthUsedByParent, heightMeasureSpec, heightUsedByParent);

        int childCount = getChildCount();
        for (int i = 1; i < childCount; i++) { // from 1 since we skipping background
            View v = getChildAt(i);
            if (v.getVisibility() != View.GONE) {
                // Implements all calculations w/ this view paddings & child layout_margin_*
                LayoutParams lp = (LayoutParams) v.getLayoutParams();
                measureChildWithMargins(
                        v,
                        widthMeasureSpec,
                        widthUsedByParent,
                        heightMeasureSpec,
                        heightUsedByParent);
                int childWidth = v.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
                int childHeight = v.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
                if (childWidth > childrenMaxWidth) {
                    childrenMaxWidth = childWidth;
                }
                childrenAllHeight += childHeight;
            }

            if (heightMode != MeasureSpec.UNSPECIFIED
                    && heightUsedByParent + childrenAllHeight > heightSize) {
                // Do not measure views, that do not fit into given height
                break;
            }
        }

        // We should re-check all resulting sizes,
        // because any buggy-view can return invalid results and we will go out of available size
        setMeasuredDimension(
                getMeasurement(
                        widthMeasureSpec,
                        childrenMaxWidth + widthUsedByParent),
                getMeasurement(
                        heightMeasureSpec,
                        childrenAllHeight + heightUsedByParent)
        );
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // For children all layout are starts from 0 and l,t,r,b may be ignored
        int offsetLeft = getPaddingLeft();
        int offsetTop = getPaddingTop();
        int offsetRight = getMeasuredWidth() - getPaddingRight();

        int left, top, right, bottom;

        int childCount = getChildCount();

        for (int i = 1; i < childCount; i++) { // skip background
            View v = getChildAt(i);
            if (v.getVisibility() != View.GONE) {
                LayoutParams lp = (LayoutParams) v.getLayoutParams();
                left = offsetLeft + lp.leftMargin;
                top = offsetTop + lp.topMargin;
                right = offsetRight - lp.rightMargin;
                bottom = top + v.getMeasuredHeight();

                v.layout(left, top, right, bottom);

                offsetTop = bottom + lp.bottomMargin;
            }
        }

        mBackgroundView.layout(
                offsetLeft + (mIsOutgoing ? dp(48) : 0),
                getPaddingTop() + dp(8),
                offsetRight + (mIsOutgoing ? 0 : -dp(48)),
                offsetTop
        );
    }

    /**
     * Sticks cell to right
     */
    public void stickToRight() {
        if ( mIsOutgoing == true ) {
            return;
        }

        mBackgroundDrawable.setColor(COLOR_BLUE);
        mIsOutgoing = true;

        stickToSide();
    }

    /**
     * Sticks cell to right
     */
    public void stickToLeft() {
        if ( mIsOutgoing == false ) {
            return;
        }

        mBackgroundDrawable.setColor(COLOR_GRAY);
        mIsOutgoing = false;

        stickToSide();
    }

    private void stickToSide() {

        LayoutParams lp = (DialogCellView.LayoutParams) mBackgroundView.getLayoutParams();
        lp.rightMargin = mIsOutgoing ? 0 : dp(48);
        lp.leftMargin = mIsOutgoing ? dp(48) : 0;

        mBackgroundView.setLayoutParams(lp);

        lp = (DialogCellView.LayoutParams) textView.getLayoutParams();
        lp.rightMargin = mIsOutgoing ? 0 : dp(48);
        lp.leftMargin = mIsOutgoing ? dp(48) : 0;

        textView.setLayoutParams(lp);

        lp = (DialogCellView.LayoutParams) image.getLayoutParams();
        lp.rightMargin = mIsOutgoing ? dp(8) : dp(56);
        lp.leftMargin = mIsOutgoing ? dp(56) : dp(8);

        image.setLayoutParams(lp);
    }

    /**
     * Sets message text
     * @param text
     */
    public void setText(String text) {
        textView.setText(text);

        LayoutParams lp = (DialogCellView.LayoutParams) image.getLayoutParams();

        if (!TextUtils.isEmpty(text)) {
            textView.setVisibility(VISIBLE);
            lp.topMargin = 0;
        }
        else {
            textView.setVisibility(GONE);
            lp.topMargin = dp(16);
        }

        image.setLayoutParams(lp);
    }

    // ==========

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new DialogCellView.LayoutParams(getContext(), attrs);
    }

    // Override to allow type-checking of LayoutParams.
    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams layoutParams) {
        return layoutParams instanceof DialogCellView.LayoutParams;
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams layoutParams) {
        return new LayoutParams(layoutParams);
    }

    /**
     * Custom layout params
     */
    public static class LayoutParams extends ViewGroup.MarginLayoutParams {

        /**
         * Creates a new set of layout parameters.
         * @param c The application environment
         * @param attrs The set of attributes fom which to extract the layout  parameters values
         */
        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        /**
         * {@inheritDoc}
         */
        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }

    // ========

    /**
     * Helper method that useful in edit mode
     * @param value amount of dip
     * @return value converted to pixels
     */
    private int dp(float value) {
        if ( !isInEditMode() ) {
            return AndroidUtilities.dp(value);
        }
        return (int)Math.ceil(value * getResources().getDisplayMetrics().density);
    }

    /**
     * Utility to return a view's standard measurement. Uses the
     * supplied size when constraints are given. Attempts to
     * hold to the desired size unless it conflicts with provided
     * constraints.
     *
     * @param measureSpec Constraints imposed by the parent
     * @param contentSize Desired size for the view
     * @return The size the view should be.
     */
    protected static int getMeasurement(int measureSpec, int contentSize) {
        int specMode = View.MeasureSpec.getMode(measureSpec);
        int specSize = View.MeasureSpec.getSize(measureSpec);
        switch (specMode) {
            case View.MeasureSpec.UNSPECIFIED:
                //Big as we want to be
                return contentSize;
            case View.MeasureSpec.AT_MOST:
                //Big as we want to be, up to the spec
                return Math.min(contentSize, specSize);
            case View.MeasureSpec.EXACTLY:
                //Must be the spec size
                return specSize;
            default:
                return 0;
        }
    }
}
