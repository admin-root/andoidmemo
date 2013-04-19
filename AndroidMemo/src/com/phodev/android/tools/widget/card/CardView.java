package com.phodev.android.tools.widget.card;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.phodev.android.tools.R;

public class CardView extends LinearLayout {

	public CardView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public CardView(Context context) {
		super(context);
		init();
	}

	private void init() {
		setOrientation(VERTICAL);
	}

	public View getTitleView() {
		return findViewById(R.id.card_title);
	}

	private boolean isExpanding = false;

	public boolean isExpanding() {
		return isExpanding;
	}

	public void setExpanding(boolean expanding) {
		isExpanding = expanding;
	}
}
