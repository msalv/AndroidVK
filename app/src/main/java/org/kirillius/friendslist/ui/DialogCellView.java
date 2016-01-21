package org.kirillius.friendslist.ui;

import android.content.Context;
import android.graphics.Point;
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

import com.squareup.picasso.Picasso;
import com.vk.sdk.api.model.VKApiPhoto;
import com.vk.sdk.api.model.VKAttachments;

import org.kirillius.friendslist.R;
import org.kirillius.friendslist.core.AndroidUtilities;

/**
 * Created by Kirill Bykov on 18.01.2016.
 */
public class DialogCellView extends FrameLayout {

    private static final int COLOR_TEXT = 0xff2e3033;
    private static final int COLOR_GRAY = 0xffe6e9f0;
    private static final int COLOR_BLUE = 0xffd8e5f5;

    private GradientDrawable mBackgroundDrawable;
    private InnerDialogCellView mBubbleContainer;
    private TextView textView;
    public ImageView imageView;

    private Picasso mImageLoader;

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

    public void setImageLoader(Picasso imageLoader) {
        this.mImageLoader = imageLoader;
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
     * Sets images view's width and height to fit to the screen
     * @return Data structure with calculated width and height
     */
    public void setAttachments(VKAttachments attachments) {

        for (VKAttachments.VKApiAttachment attachment : attachments) {
            if ( attachment instanceof VKApiPhoto) {
                this.attachPhoto((VKApiPhoto) attachment);
                break; // support only one photo for now
            }
        }
    }

    /**
     * Attaches photo to the message
     * @param photo
     */
    private void attachPhoto(VKApiPhoto photo) {

        // 320 — required width, 72 — all margins/paddings
        int width = Math.min(AndroidUtilities.displaySize.x, dp(320)) - dp(72);
        float scale = photo.width != 0 ? (float)width / (float)photo.width : 1.0f;
        int height = (int)(photo.height * scale);

        if ( height == 0 ) {
            height = width + dp(100);
        }

        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) imageView.getLayoutParams();
        lp.width = width;
        lp.height = height;

        String photo_url = null;

        while ( width > 64 ) {
            photo_url = photo.src.getImageForDimension(width, height);
            if (photo_url != null) {
                break;
            }
            width = (int) (width * 0.75); // scale down to 75%
            height = (int) (height * 0.75); // scale down to 75%
        }

        imageView.setVisibility(View.VISIBLE);

        if ( this.mImageLoader != null ) {
            this.mImageLoader.load(photo_url)
                    .placeholder(R.drawable.ic_image)
                    .into(imageView);
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
    private static class InnerDialogCellView extends FrameLayout {
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
