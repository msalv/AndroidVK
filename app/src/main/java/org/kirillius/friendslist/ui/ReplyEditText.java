package org.kirillius.friendslist.ui;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import org.kirillius.friendslist.R;
import org.kirillius.friendslist.core.AndroidUtilities;

/**
 * Created by Kirill on 17.01.2016.
 */
public class ReplyEditText extends FrameLayout {

    private EditText mEditText;
    private ImageView mSendButton;

    public ReplyEditText(Context context) {
        super(context);
        init(context);
    }

    public ReplyEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ReplyEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {

        mEditText = new EditText(context);

        mEditText.setHint(context.getString(R.string.enter_message));
        mEditText.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        mEditText.setInputType(mEditText.getInputType() | EditorInfo.TYPE_TEXT_FLAG_CAP_SENTENCES | EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE);
        mEditText.setSingleLine(false);
        mEditText.setMaxLines(4);
        mEditText.setPadding(0, dp(12), 0, dp(12));
        mEditText.setHintTextColor(0xffa2a2a2);

        if (Build.VERSION.SDK_INT < 16) {
            mEditText.setBackgroundDrawable(null);
        }
        else {
            mEditText.setBackground(null);
        }

        addView(mEditText);

        LayoutParams lp = (LayoutParams) mEditText.getLayoutParams();
        lp.width = LayoutParams.MATCH_PARENT;
        lp.height = LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        lp.leftMargin = dp(12);
        lp.rightMargin = dp(52);

        mEditText.setLayoutParams(lp);

        mSendButton = new ImageView(context);

        mSendButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        mSendButton.setImageResource(R.drawable.ic_send);
        mSendButton.setClickable(true);

        addView(mSendButton);

        lp = (LayoutParams) mSendButton.getLayoutParams();
        lp.width = dp(48);
        lp.height = dp(48);
        lp.gravity = Gravity.RIGHT | Gravity.BOTTOM;

        mSendButton.setLayoutParams(lp);

        View borderTop = new View(context);
        borderTop.setBackgroundColor(0xffdfdfdf);

        addView(borderTop);

        lp = (LayoutParams) borderTop.getLayoutParams();
        lp.width = LayoutParams.MATCH_PARENT;
        lp.height = dp(1);
    }

    /**
     * Sets onClickListener to the send button
     * @param listener
     */
    public void setOnSendClickListener(View.OnClickListener listener) {
        mSendButton.setOnClickListener(listener);
    }

    /**
     * Returns text from the input field
     * @return Message text
     */
    public String getText() {
        return mEditText.getText().toString();
    }

    /**
     * Clears input field
     */
    public void clearInput() {
        mEditText.setText(null);
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

}
