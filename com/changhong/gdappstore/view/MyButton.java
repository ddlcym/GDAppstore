package com.changhong.gdappstore.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Button;

import com.changhong.gdappstore.R;
import com.changhong.gdappstore.util.Util;

public class MyButton extends Button {

	private Context context;

	private String text;

	private int textSize, textColor, textColor_selected, radius, strokeWidth, stokeColor, solidColor,
			solidColor_selected;

	private boolean enable = true;

	public MyButton(Context context) {
		super(context);
		this.context = context;
		initView(null);
	}

	public MyButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		initView(attrs);
	}

	public MyButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		initView(attrs);
	}

	private void initView(AttributeSet attrs) {
		setAttrs(attrs);
		drawButton(isSelected());
	}

	private void setAttrs(AttributeSet attrs) {
		if (attrs != null) {
			TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MyButton);
			String text = typedArray.getString(R.styleable.MyButton_mybtn_text);
			this.text = text == null ? "" : text;
			this.textSize = (int) typedArray.getDimension(R.styleable.MyButton_mybtn_textSize,
					Util.dipTopx(context, 16));
			this.textSize = Util.pxTodip(context, textSize);// 像素转换成dp

			this.textColor = typedArray.getColor(R.styleable.MyButton_mybtn_textColor, Color.BLACK);
			this.textColor_selected = typedArray.getColor(R.styleable.MyButton_mybtn_textColor_selected, Color.BLACK);
			this.radius = (int) typedArray.getDimension(R.styleable.MyButton_mybtn_radius, Util.dipTopx(context, 60));
			this.radius = Util.pxTodip(context, radius);// 像素转换成dp

			this.strokeWidth = (int) typedArray.getDimension(R.styleable.MyButton_mybtn_strokeWidth,
					Util.dipTopx(context, 1));
			this.strokeWidth = Util.pxTodip(context, strokeWidth);// 像素转换成dp

			this.stokeColor = typedArray.getColor(R.styleable.MyButton_mybtn_stokeColor, Color.BLACK);
			this.solidColor = typedArray.getColor(R.styleable.MyButton_mybtn_solidColor, Color.WHITE);
			this.solidColor_selected = typedArray.getColor(R.styleable.MyButton_mybtn_solidColor_selected, Color.WHITE);
			this.enable = typedArray.getBoolean(R.styleable.MyButton_mybtn_enable, true);
			typedArray.recycle();
		} else {
			this.text = "";
			this.strokeWidth = 1;
			this.stokeColor = Color.BLACK;
			this.solidColor = Color.TRANSPARENT;
			this.solidColor_selected = Color.YELLOW;
			this.enable = true;
		}
	}

	@Override
	public void setText(CharSequence text, BufferType type) {
		this.text = text.toString();
		super.setText(text, type);
	}

	public void setMyText(String text) {
		this.text = text;
		drawButton(isSelected());
	}

	private void drawButton(boolean touched) {

		GradientDrawable gradientDrawable = new GradientDrawable();
		gradientDrawable.setShape(GradientDrawable.RECTANGLE);

		gradientDrawable.setCornerRadius(radius);
		gradientDrawable.setStroke(strokeWidth, stokeColor);
		gradientDrawable.setColor(touched ? solidColor_selected : solidColor);
		setBackgroundDrawable(gradientDrawable);
		setText(text);
		setTextSize(textSize);
		setTextColor(touched ? textColor_selected : textColor);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		if (!enable) {
			return true;
		}
		return super.dispatchTouchEvent(event);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			drawButton(true);
		} else {
			drawButton(false);
		}
		return super.onTouchEvent(event);
	}

	@Override
	protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
		super.onFocusChanged(focused, direction, previouslyFocusedRect);
		drawButton(focused);
	}
}
