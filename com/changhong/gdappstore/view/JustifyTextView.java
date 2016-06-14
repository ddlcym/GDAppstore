package com.changhong.gdappstore.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * TextView自适应两边对齐
 * 
 * @author wangxiufeng
 * 
 */
public class JustifyTextView extends TextView {
	/** 每行字符的Y坐标 */
	private int mLineY;
	/** The raw measured width of this view */
	private int mViewWidth;
	/** 字行直接高度增加量 */
	private static final int HEIGHT_ADD = 8;

	public static final String TWO_CHINESE_BLANK = "  ";

	public JustifyTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		TextPaint paint = getPaint();
		paint.setColor(getCurrentTextColor());
		paint.drawableState = getDrawableState();
		mViewWidth = getMeasuredWidth();
		String text = getText().toString();
		mLineY = 0;
		mLineY += getTextSize();
		Layout layout = getLayout();

		// layout.getLayout()在4.4.3出现NullPointerException
		if (layout == null) {
			return;
		}

		Paint.FontMetrics fm = paint.getFontMetrics();

		int textHeight = (int) (Math.ceil(fm.descent - fm.ascent));// 字体高度
		textHeight += HEIGHT_ADD;
		textHeight = (int) (textHeight * layout.getSpacingMultiplier() + layout.getSpacingAdd());

		for (int i = 0; i < layout.getLineCount(); i++) {
			int lineStart = layout.getLineStart(i);
			int lineEnd = layout.getLineEnd(i);
			float width = StaticLayout.getDesiredWidth(text, lineStart, lineEnd, getPaint());
			String line = text.substring(lineStart, lineEnd);
			if (needScale(line) && i != layout.getLineCount() - 1) {
				drawScaledText(canvas, lineStart, line, width);
			} else {
				canvas.drawText(line, 0, mLineY, paint);
			}
			mLineY += textHeight;
		}
		
	}

	private void drawScaledText(Canvas canvas, int lineStart, String line, float lineWidth) {
		float x = 0;
		if (isFirstLineOfParagraph(lineStart, line)) {
			String blanks = "  ";
			canvas.drawText(blanks, x, mLineY, getPaint());
			float bw = StaticLayout.getDesiredWidth(blanks, getPaint());
			x += bw;

			line = line.substring(3);
		}

		int gapCount = line.length() - 1;
		int i = 0;
		if (line.length() > 2 && line.charAt(0) == 12288 && line.charAt(1) == 12288) {
			// unicode为12288字符为全角空格，trim()无法去除，去除方法如下：str = str.replace((char)
			// 12288, ' ');str=str.trim();
			String substring = line.substring(0, 2);
			float cw = StaticLayout.getDesiredWidth(substring, getPaint());
			canvas.drawText(substring, x, mLineY, getPaint());
			x += cw;
			i += 2;
		}

		float d = (mViewWidth - lineWidth) / gapCount;// 每个字符增加空白长度，已填充末尾空白满足左右对齐
		for (; i < line.length(); i++) {
			String c = String.valueOf(line.charAt(i));
			float cw = StaticLayout.getDesiredWidth(c, getPaint());
			canvas.drawText(c, x, mLineY, getPaint());
			x += cw + d;
		}
	}

	private boolean isFirstLineOfParagraph(int lineStart, String line) {
		return line.length() > 3 && line.charAt(0) == ' ' && line.charAt(1) == ' ';
	}

	private boolean needScale(String line) {
		if (line == null || line.length() == 0) {
			return false;
		} else {
			// L.d("line.lastchar=="+line.charAt(line.length() - 1));
			return line.charAt(line.length() - 1) != '\n';
		}
	}

}
