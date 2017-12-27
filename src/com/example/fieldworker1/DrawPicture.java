package com.example.fieldworker1;

import java.io.File;
import java.io.FileOutputStream;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class DrawPicture extends Activity {
	private Button btn_save, btn_resume;
	private ImageView iv_canvas;
	private Bitmap baseBitmap;
	private Canvas canvas;
	private Paint paint;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.draw_picture);

		// initialize a paint with width 5, red color
		paint = new Paint();
		paint.setStrokeWidth(5);
		paint.setColor(Color.RED);

		iv_canvas = (ImageView) findViewById(R.id.iv_canvas);
		btn_save = (Button) findViewById(R.id.btn_save);
		btn_resume = (Button) findViewById(R.id.btn_resume);

		btn_save.setOnClickListener(click);
		btn_resume.setOnClickListener(click);
		iv_canvas.setOnTouchListener(touch);
	}

	private View.OnTouchListener touch = new OnTouchListener() {
		//define the coordinate of finger starting 
		float startX;
		float startY;

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			// user start touch
			case MotionEvent.ACTION_DOWN:
				// initialize memory picture of first painting, set the background white
				if (baseBitmap == null) {
					baseBitmap = Bitmap.createBitmap(iv_canvas.getWidth(),
							iv_canvas.getHeight(), Bitmap.Config.ARGB_8888);
					canvas = new Canvas(baseBitmap);
					canvas.drawColor(Color.WHITE);
				}
				//record the start touch point 
				startX = event.getX();
				startY = event.getY();
				break;
			// touch move
			case MotionEvent.ACTION_MOVE:
				//record touch moved point coordinate 
				float stopX = event.getX();
				float stopY = event.getY();
				
				//drawLine according to the two coordinate
				canvas.drawLine(startX, startY, stopX, stopY, paint);
				
				// update start point
				startX = event.getX();
				startY = event.getY();
				
				// display picture to ImageView
				iv_canvas.setImageBitmap(baseBitmap);
				break;
			case MotionEvent.ACTION_UP:

				break;
			default:
				break;
			}
			return true;
		}
	};
	private View.OnClickListener click = new OnClickListener() {

		@Override
		public void onClick(View v) {

			switch (v.getId()) {
			case R.id.btn_save:
				saveBitmap();
				break;
			case R.id.btn_resume:
				resumeCanvas();
				break;
			default:
				break;
			}
			//finish();
		}
	};

	/**
	 * save the pictures in sd card
	 */
	protected void saveBitmap() {
		try {
			// save the pictures in sd card
			File file = new File(Environment.getExternalStorageDirectory(),
					System.currentTimeMillis() + ".png");
			FileOutputStream stream = new FileOutputStream(file);
			baseBitmap.compress(CompressFormat.PNG, 100, stream);
			Toast.makeText(DrawPicture.this, "Save picture successfully", 0).show();
			
			// Gallery application of android device only scan file folder when launched 
			// simulate a broadcast loaded by media used for that the saved pictures can be looked up in Gallery
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_MEDIA_MOUNTED);
			intent.setData(Uri.fromFile(Environment
					.getExternalStorageDirectory()));
			sendBroadcast(intent);
		} catch (Exception e) {
			Toast.makeText(DrawPicture.this, "Save picture failure", 0).show();
			e.printStackTrace();
		}
	}

	/**
	 * clear the canvas
	 */
	protected void resumeCanvas() {
		// clear the picture of the canvas, renew a canvas
		if (baseBitmap != null) {
			baseBitmap = Bitmap.createBitmap(iv_canvas.getWidth(),
					iv_canvas.getHeight(), Bitmap.Config.ARGB_8888);
			canvas = new Canvas(baseBitmap);
			canvas.drawColor(Color.WHITE);
			iv_canvas.setImageBitmap(baseBitmap);
			Toast.makeText(DrawPicture.this, "Clear canvas successfully", 0).show();
		}
	}
}
