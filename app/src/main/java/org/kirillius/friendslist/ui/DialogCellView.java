package org.kirillius.friendslist.ui;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.kirillius.friendslist.core.AndroidUtilities;

/**
 * Created by Kirill Bykov on 18.01.2016.
 */
public class DialogCellView extends FrameLayout {

    private static final int COLOR_TEXT = 0xff2e3033;
    private static final int COLOR_GRAY = 0xffe6e9f0;
    private static final int COLOR_BLUE = 0xffd8e5f5;
    private static final int COLOR_LIGHT_BLUE = 0xffdfe6ee;

    private GradientDrawable mBackgroundDrawable;
    private InnerDialogCellView mBubbleContainer;
    private TextView textView;
    private ImageView imageView;

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

    public ImageView getImageView() {
        return imageView;
    }

    private void init(Context context) {

        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        this.setLayoutParams(lp);

        this.setPadding(dp(8), dp(8), dp(48), dp(8)); // incoming by default (right padding: 48dp)

        mBackgroundDrawable = new GradientDrawable();
        mBackgroundDrawable.setColor(COLOR_GRAY);  // incoming by default (bg color: gray)
        mBackgroundDrawable.setCornerRadius(dp(6));

        mBubbleContainer = new InnerDialogCellView(context);

        mBubbleContainer.setPadding(dp(8), dp(8), dp(8), dp(8));

        if (Build.VERSION.SDK_INT < 16) {
            mBubbleContainer.setBackgroundDrawable(mBackgroundDrawable);
        }
        else {
            mBubbleContainer.setBackground(mBackgroundDrawable);
        }

        addView(mBubbleContainer);

        lp = (FrameLayout.LayoutParams) mBubbleContainer.getLayoutParams();
        lp.width = LayoutParams.WRAP_CONTENT;
        lp.height = LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.LEFT;   // incoming by default

        textView = new TextView(context);
        textView.setVisibility(GONE);
        textView.setTextColor(COLOR_TEXT);

        mBubbleContainer.addView(textView);

        lp = (FrameLayout.LayoutParams) textView.getLayoutParams();
        lp.width = LayoutParams.WRAP_CONTENT;
        lp.height = LayoutParams.WRAP_CONTENT;
        lp.leftMargin = dp(8);
        lp.rightMargin = dp(8);

        imageView = new ImageView(context);

        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        imageView.setBackgroundColor(COLOR_LIGHT_BLUE);
        imageView.setVisibility(View.GONE);

        mBubbleContainer.addView(imageView);
    }

    /**
     * Sets gravity of inner dialog cell, changes its background and sets paddings
     * @param gravity
     */
    public void setGravity(int gravity) {

        LayoutParams lp = (FrameLayout.LayoutParams) mBubbleContainer.getLayoutParams();
        lp.gravity = gravity;

        mBackgroundDrawable.setColor(gravity == Gravity.LEFT ? COLOR_GRAY : COLOR_BLUE);

        int left_padding = (gravity == Gravity.LEFT ? dp(8) : dp(48));
        int right_padding = (gravity == Gravity.LEFT ? dp(48) : dp(8));
        this.setPadding(left_padding, dp(8), right_padding, dp(8));
    }

    /**
     * Sets message text
     * @param text
     */
    public void setText(String text) {
        textView.setText(text);

        LayoutParams lp = (DialogCellView.LayoutParams) imageView.getLayoutParams();

        if (!TextUtils.isEmpty(text)) {
            textView.setVisibility(VISIBLE);
            lp.topMargin = dp(8);
        }
        else {
            textView.setVisibility(GONE);
            lp.topMargin = 0;
        }
    }

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
     * Some kind of vertical frame layout
     */
    private class InnerDialogCellView extends FrameLayout {
        public InnerDialogCellView(Context context) {
            super(context);
        }

        @Override
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
            // For children all layout are starts from 0 and l,t,r,b may be ignored
            int offsetLeft = getPaddingLeft();
            int offsetTop = getPaddingTop();
            //int offsetRight = getMeasuredWidth() - getPaddingRight();
            int left, top, right, bottom;

            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                View v = getChildAt(i);
                if (v.getVisibility() != View.GONE) {
                    LayoutParams lp = (LayoutParams) v.getLayoutParams();

                    left = offsetLeft + lp.leftMargin;
                    top = offsetTop + lp.topMargin;
                    right = left + v.getMeasuredWidth();//offsetRight - lp.rightMargin;
                    bottom = top + v.getMeasuredHeight();

                    v.layout(left, top, right, bottom);

                    offsetTop = bottom + lp.bottomMargin;
                }
            }
        }
    }
}
