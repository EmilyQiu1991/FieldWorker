package com.example.fieldworker1;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader.TileMode;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class ImageAdapter extends BaseAdapter {
	private ImageView[] mImages;
	private Context mContext;
	public List<Map<String, Object>> list;
	public String[] imgs;

	public ImageAdapter(Context c, String paths) {
		this.mContext = c;
		imgs = paths.split(",");
		list = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < imgs.length; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("image", imgs[i]);
			list.add(map);
		}
		mImages = new ImageView[list.size()];
	}

	public boolean createReflectedImages() {
		final int reflectionGap = 4;
		final int Height = 200;
		int index = 0;
		for (Map<String, Object> map : list) {
			String path = (String) map.get("image");
			if (!(new File(path).exists())) {
				Resources res = mContext.getResources();
				Bitmap originalImage = BitmapFactory.decodeResource(res,
						R.drawable.emptyimage);
				int width = originalImage.getWidth();
				int height = originalImage.getHeight();
				float scale = Height / (float) height;

				Matrix sMatrix = new Matrix();
				sMatrix.postScale(scale, scale);
				Bitmap miniBitmap = Bitmap.createBitmap(originalImage, 0, 0,
						originalImage.getWidth(), originalImage.getHeight(),
						sMatrix, true);

				int mwidth = miniBitmap.getWidth();
				int mheight = miniBitmap.getHeight();
				Matrix matrix = new Matrix();
				matrix.preScale(1, -1);
				Bitmap reflectionImage = Bitmap.createBitmap(miniBitmap, 0,
						mheight / 2, mwidth, mheight / 2, matrix, false);
				Bitmap bitmapWithReflection = Bitmap.createBitmap(mwidth,
						(mheight + mheight / 2), Config.ARGB_8888);

				Canvas canvas = new Canvas(bitmapWithReflection);

				canvas.drawBitmap(miniBitmap, 0, 0, null);
				Paint paint = new Paint();
				canvas.drawRect(0, mheight, mwidth, mheight + reflectionGap,
						paint);
				canvas.drawBitmap(reflectionImage, 0, mheight + reflectionGap,
						null);

				paint = new Paint();
				LinearGradient shader = new LinearGradient(0,
						miniBitmap.getHeight(), 0,
						bitmapWithReflection.getHeight() + reflectionGap,
						0x70ffffff, 0x00ffffff, TileMode.CLAMP);
				paint.setShader(shader);
				paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
				canvas.drawRect(0, mheight, mwidth,
						bitmapWithReflection.getHeight() + reflectionGap, paint);

				ImageView imageView = new ImageView(mContext);
				imageView.setImageBitmap(bitmapWithReflection);
				imageView
						.setLayoutParams(new GalleryView.LayoutParams(203, 400));
				imageView.setScaleType(ScaleType.FIT_XY);
				mImages[index++] = imageView;
				return true;
			}
			Bitmap originalImage = BitmapFactory.decodeFile(path);
			int width = originalImage.getWidth();
			int height = originalImage.getHeight();
			float scale = Height / (float) height;

			Matrix sMatrix = new Matrix();
			sMatrix.postScale(scale, scale);
			Bitmap miniBitmap = Bitmap.createBitmap(originalImage, 0, 0,
					originalImage.getWidth(), originalImage.getHeight(),
					sMatrix, true);

			int mwidth = miniBitmap.getWidth();
			int mheight = miniBitmap.getHeight();
			Matrix matrix = new Matrix();
			matrix.preScale(1, -1);
			Bitmap reflectionImage = Bitmap.createBitmap(miniBitmap, 0,
					mheight / 2, mwidth, mheight / 2, matrix, false);
			Bitmap bitmapWithReflection = Bitmap.createBitmap(mwidth,
					(mheight + mheight / 2), Config.ARGB_8888);

			Canvas canvas = new Canvas(bitmapWithReflection);

			canvas.drawBitmap(miniBitmap, 0, 0, null);
			Paint paint = new Paint();
			canvas.drawRect(0, mheight, mwidth, mheight + reflectionGap, paint);
			canvas.drawBitmap(reflectionImage, 0, mheight + reflectionGap, null);

			paint = new Paint();
			LinearGradient shader = new LinearGradient(0,
					miniBitmap.getHeight(), 0, bitmapWithReflection.getHeight()
							+ reflectionGap, 0x70ffffff, 0x00ffffff,
					TileMode.CLAMP);
			paint.setShader(shader);
			paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
			canvas.drawRect(0, mheight, mwidth,
					bitmapWithReflection.getHeight() + reflectionGap, paint);

			ImageView imageView = new ImageView(mContext);
			imageView.setImageBitmap(bitmapWithReflection);
			imageView.setLayoutParams(new GalleryView.LayoutParams(203, 400));
			imageView.setScaleType(ScaleType.FIT_XY);
			mImages[index++] = imageView;
		}
		return true;
	}

	@Override
	public int getCount() {
		return Integer.MAX_VALUE;
	}

	@Override
	public Object getItem(int position) {
		return imgs[position % imgs.length];
	}

	@Override
	public long getItemId(int position) {
		return position % imgs.length;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		// return mImages[position];
		return mImages[position % imgs.length];
	}

	public float getScale(boolean focused, int offset) {
		return Math.max(0, 1.0f / (float) Math.pow(2, Math.abs(offset)));
	}

}
