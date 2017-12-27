package com.example.fieldworker1;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;

public class GalleryAdapter extends BaseAdapter {

	int mGalleryItemBackground;
	private Context mContext;
	private String[] myImagePaths;
	private int[] myImageId;
	private boolean flag;

	public GalleryAdapter(Context context, boolean flag, String[] paths) {
		mContext = context;
		TypedArray typedArray = mContext
				.obtainStyledAttributes(R.styleable.Gallery);
		mGalleryItemBackground = typedArray.getResourceId(
				R.styleable.Gallery_android_galleryItemBackground, 0);
		typedArray.recycle();
		this.flag = flag;
		// true no image---false images
		if (this.flag) {
			myImageId = new int[1];
			myImageId[0] = R.drawable.emptyimage;
		} else {
			myImagePaths = paths;
		}

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return Integer.MAX_VALUE;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ImageView imageView = new ImageView(mContext);
		if (flag) {
			imageView.setImageResource(myImageId[position % myImageId.length]);
		} else {
			if (myImagePaths.length == 0) {
				imageView.setImageResource(R.drawable.emptyimage);
				return imageView;
			}

			File f = new File(myImagePaths[position % myImagePaths.length]);
			if (!f.exists()) {
				imageView.setImageResource(R.drawable.emptyimage);
				return imageView;
			}
			Bitmap bmp = decodeSampledBitmapFromResource(f.getAbsolutePath(),
					800, 800);
			int bmpW = bmp.getWidth();
			int bmpH = bmp.getHeight();
			double scale = 1;
			float scaleW = 1, scaleH = 1;
			float curDegrees = readPictureDegree(f.getAbsolutePath());
			scaleW = (float) (scaleW * scale);
			scaleH = (float) (scaleH * scale);
			Matrix mt = new Matrix();
			mt.postScale(scaleW, scaleH);
			mt.setRotate(curDegrees);
			Bitmap resizeBmp = Bitmap.createBitmap(bmp, 0, 0, bmpW, bmpH, mt,
					true);
			imageView.setImageBitmap(resizeBmp);
		}
		imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
		Gallery.LayoutParams layoutParams = new Gallery.LayoutParams(300, 300);
		imageView.setLayoutParams(layoutParams);
		imageView.setPadding(1, 0, 10, 0);
		imageView.setBackgroundResource(mGalleryItemBackground);
		return imageView;
	}

	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and
			// keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}
		return inSampleSize;
	}

	public static Bitmap decodeSampledBitmapFromResource(String strPath,
			int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(strPath, options);
		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);
		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(strPath, options);
	}

	public static int readPictureDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}
}
