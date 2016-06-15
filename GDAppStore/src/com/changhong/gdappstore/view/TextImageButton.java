package com.changhong.gdappstore.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.util.AttributeSet;
import android.widget.ImageButton;

import com.changhong.gdappstore.R;
import com.changhong.gdappstore.util.L;
/**
 * 带文字的imageButton
 * @author wangxiufeng
 *
 */
public class TextImageButton extends ImageButton {
	private String text = "";
	private int textSize = 24;
	private int textColor = Color.WHITE;
	private Context context;

	public TextImageButton(Context context) {
		super(context);
		this.context = context;
		init(null);
	}

	public TextImageButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init(attrs);
	}

	public TextImageButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		init(attrs);
	}

	private void init(AttributeSet attrs) {
		setFocusable(true);
		if (attrs != null) {
			TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TextImageButton);
			text = typedArray.getString(R.styleable.TextImageButton_text);
			text = text == null ? "" : text;
			textSize = (int) typedArray.getDimension(R.styleable.TextImageButton_textSize, 24);
			textColor = typedArray.getColor(R.styleable.TextImageButton_textColor, Color.WHITE);
		}
	}
	
	public void setText(String text) {
		this.text=text;
		invalidate();
	}
	public void setMyText(String text) {
		this.text=text;
		invalidate();
	}
	
	public void setTextSize(int textSize) {
		this.textSize=textSize;
		invalidate();
	}
	
	public void setTextColor(int textColor) {
		this.textColor=textColor;
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Paint paint = new Paint();
		paint.setTextAlign(Align.CENTER);
		paint.setColor(textColor);
		paint.setTextSize(textSize);
		L.d("textImageButton draw text "+text);
		canvas.drawText(text, canvas.getWidth() / 2, (canvas.getHeight() / 2)+textSize/2-5, paint);
	}

}
