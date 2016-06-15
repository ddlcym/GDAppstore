package com.changhong.gdappstore.view;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.SeekBar;
import android.widget.TextView;

import com.changhong.gdappstore.R;

public class MyProgressDialog extends Dialog {
	private Context context;
	private TextView tv_title, tv_progress_hundred, tv_progress_total;
	// 进度条
	private SeekBar seekBar;
	private MyButton bt_backload;
	// 最大进度
	private int max = 100;
	// 当前进度
	private int curprogress = 0;
	// 创建一个数值格式化对象
	private NumberFormat numberFormat;
	/**是否需要将大小转换为M显示？（仅适用于将byte转换为M）*/
	private boolean isUpdateFileSizeName=false;

	public MyProgressDialog(Context context) {
		super(context, R.style.Dialog_nowindowbg);
		this.context = context;
		init();
	}

	private void init() {
		setCancelable(false);
		View view = LayoutInflater.from(context).inflate(R.layout.dialog_progress, null);
		setContentView(view);
		tv_title = (TextView) view.findViewById(R.id.tv_dialog_title);
		tv_progress_hundred = (TextView) view.findViewById(R.id.tv_progress_hundread);
		tv_progress_total = (TextView) view.findViewById(R.id.tv_progress_total);
		seekBar = (SeekBar) view.findViewById(R.id.progress);
		seekBar.setProgress(0);
		bt_backload = (MyButton) findViewById(R.id.bt_loadbackground);
		bt_backload.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		LayoutParams param = getWindow().getAttributes();
		param.gravity = Gravity.CENTER;
		param.width = (int) context.getResources().getDimension(R.dimen.dialog_width);
		param.height = (int) context.getResources().getDimension(R.dimen.dialog_height);
		getWindow().setAttributes(param);
		numberFormat = NumberFormat.getInstance();
		// 设置精确到小数点后0位
		numberFormat.setMaximumFractionDigits(0);
	}
	/**
	 * 最大进度
	 * @param max
	 */
	public void setMax(int max) {
		this.max = max;
		seekBar.setMax(max);
	}

	public void setProgress(int progress) {
		Message message = handler.obtainMessage(100);
		message.arg1 = progress;
		handler.sendMessage(message);
	}
	/**
	 * 进度更新handler，解决异步线程下载时候更新页面问题
	 */
	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			setmyProgress(msg.arg1);
		}
	};

	private void setmyProgress(int progress) {
//		L.d("setmyprogress--" + progress + "  max " + max);
		if (progress < 0) {
			curprogress = 0;
		} else if (progress > max) {
			curprogress = max;
		} else {
			curprogress = progress;
		}
		if (isUpdateFileSizeName) {
			tv_progress_total.setText(parseByteToM(curprogress) + "/" + parseByteToM(max));
		}else {
			tv_progress_total.setText(curprogress + "/" + max);
		}
		
		if (max == 0) {
			tv_progress_hundred.setText("0%");
		} else {
			tv_progress_hundred.setText(numberFormat.format(((float) curprogress / (float) max * 100)) + "%");
		}
		seekBar.setProgress(progress);
	}
	/**
	 * 将byte单位转换为M单位显示
	 * @param size
	 * @return
	 */
	private String parseByteToM(float size) {
		float returnSize=(float) 0.0;
		returnSize=(float)size/(float)(1024*1024);
		DecimalFormat decimalFormat=new DecimalFormat("0.0");//构造方法的字符格式这里如果小数不足2位,会以0补足.
		String p=decimalFormat.format(returnSize);
		return p+"M";
	}
	/**
	 * 设置对话框标题
	 * @param title
	 */
	public void setMyTitle(String title) {
		tv_title.setText(title);
	}

	public boolean isUpdateFileSizeName() {
		return isUpdateFileSizeName;
	}

	public void setUpdateFileSizeName(boolean isUpdateFileSizeName) {
		this.isUpdateFileSizeName = isUpdateFileSizeName;
	}

}
