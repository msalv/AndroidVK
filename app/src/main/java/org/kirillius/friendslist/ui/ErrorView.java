package org.kirillius.friendslist.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import org.kirillius.friendslist.R;

/**
 * Created by Kirill on 27.01.2016.
 */
public class ErrorView extends FrameLayout {

    private Button mRetryButton;

    public ErrorView(Context context) {
        super(context);
        init(context);
    }

    public ErrorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ErrorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        FrameLayout.LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        this.setLayoutParams(lp);

        mRetryButton = new Button(context);
        mRetryButton.setText(R.string.try_again);

        this.addView(mRetryButton);

        lp = (FrameLayout.LayoutParams)mRetryButton.getLayoutParams();
        lp.width = LayoutParams.WRAP_CONTENT;
        lp.height = LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
    }

    public void setOnRetryClickListener(View.OnClickListener listener) {
        mRetryButton.setOnClickListener(listener);
    }
}
