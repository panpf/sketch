package me.xiaoapn.easy.imageloader.display;

import me.xiaoapn.easy.imageloader.Configuration;
import me.xiaoapn.easy.imageloader.task.Request;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.widget.ImageView;

/**
 * 圆角位图显示器，在显示位图之前会将位图处理成圆角的
 */
public class RounedFadeInBitmapDisplayer implements BitmapDisplayer {
	private int roundPixels;
	
	/**
	 * 创建一个圆角位图显示器
	 * @param roundPixels 圆角角度
	 * @param animationGenerator 动画生成器
	 */
	public RounedFadeInBitmapDisplayer(int roundPixels){
		this.roundPixels = roundPixels;
	}
	
	/**
	 * 创建一个圆角位图显示器，圆角角度默认为18并且动画生成器使用AlphaAnimationGenerator
	 */
	public RounedFadeInBitmapDisplayer(){
		this(18);
	}
	
	@Override
	public void display(ImageView imageView, BitmapDrawable bitmapDrawable, BitmapType bitmapType, boolean isFromMemoryCache, Configuration configuration, Request request) {
		switch(bitmapType){
			case FAILURE : 
				if(bitmapDrawable != null){
					fadeIn(imageView, bitmapDrawable);
				}else{
					imageView.setImageDrawable(bitmapDrawable);
				}
				break;
			case SUCCESS : 
				bitmapDrawable = new BitmapDrawable(configuration.getResources(), roundCorners(bitmapDrawable.getBitmap(), imageView, roundPixels));;
				if(!isFromMemoryCache && bitmapDrawable != null){
					fadeIn(imageView, bitmapDrawable);
				}else{
					imageView.setImageDrawable(bitmapDrawable);
				}
				break;
		}
	}
	
	/**
	 * 渐入
	 * @param imageView
	 * @param bitmapDrawable
	 */
	private void fadeIn(ImageView imageView, BitmapDrawable bitmapDrawable){
		TransitionDrawable transitionDrawable = new TransitionDrawable(new Drawable[]{new ColorDrawable(android.R.color.transparent), bitmapDrawable});
		imageView.setImageDrawable(transitionDrawable);
		transitionDrawable.startTransition(200);
	}
	
	/**
	 * Process incoming {@linkplain Bitmap} to make rounded corners according to target {@link ImageView}.<br />
	 * This method <b>doesn't display</b> result bitmap in {@link ImageView}
	 * 
	 * @param bitmap Incoming Bitmap to process
	 * @param imageView Target {@link ImageView} to display bitmap in
	 * @param roundPixels
	 * @return Result bitmap with rounded corners
	 */
	public Bitmap roundCorners(Bitmap bitmap, ImageView imageView, int roundPixels) {
		Bitmap roundBitmap;

		int bw = bitmap.getWidth();
		int bh = bitmap.getHeight();
		int vw = imageView.getWidth();
		int vh = imageView.getHeight();
		if (vw <= 0) vw = bw;
		if (vh <= 0) vh = bh;

		int width, height;
		Rect srcRect;
		Rect destRect;
		switch (imageView.getScaleType()) {
			case CENTER_INSIDE:
				float vRation = (float) vw / vh;
				float bRation = (float) bw / bh;
				int destWidth;
				int destHeight;
				if (vRation > bRation) {
					destHeight = Math.min(vh, bh);
					destWidth = (int) (bw / ((float) bh / destHeight));
				} else {
					destWidth = Math.min(vw, bw);
					destHeight = (int) (bh / ((float) bw / destWidth));
				}
				int x = (vw - destWidth) / 2;
				int y = (vh - destHeight) / 2;
				srcRect = new Rect(0, 0, bw, bh);
				destRect = new Rect(x, y, x + destWidth, y + destHeight);
				width = vw;
				height = vh;
				break;
			case FIT_CENTER:
			case FIT_START:
			case FIT_END:
			default:
				vRation = (float) vw / vh;
				bRation = (float) bw / bh;
				if (vRation > bRation) {
					width = (int) (bw / ((float) bh / vh));
					height = vh;
				} else {
					width = vw;
					height = (int) (bh / ((float) bw / vw));
				}
				srcRect = new Rect(0, 0, bw, bh);
				destRect = new Rect(0, 0, width, height);
				break;
			case CENTER_CROP:
				vRation = (float) vw / vh;
				bRation = (float) bw / bh;
				int srcWidth;
				int srcHeight;
				if (vRation > bRation) {
					srcWidth = bw;
					srcHeight = (int) (vh * ((float) bw / vw));
					x = 0;
					y = (bh - srcHeight) / 2;
				} else {
					srcWidth = (int) (vw * ((float) bh / vh));
					srcHeight = bh;
					x = (bw - srcWidth) / 2;
					y = 0;
				}
				width = Math.min(vw, bw);
				height = Math.min(vh, bh);
				srcRect = new Rect(x, y, x + srcWidth, y + srcHeight);
				destRect = new Rect(0, 0, width, height);
				break;
			case FIT_XY:
				width = vw;
				height = vh;
				srcRect = new Rect(0, 0, bw, bh);
				destRect = new Rect(0, 0, width, height);
				break;
			case CENTER:
			case MATRIX:
				width = Math.min(vw, bw);
				height = Math.min(vh, bh);
				x = (bw - width) / 2;
				y = (bh - height) / 2;
				srcRect = new Rect(x, y, x + width, y + height);
				destRect = new Rect(0, 0, width, height);
				break;
		}

		try {
			roundBitmap = getRoundedCornerBitmap(bitmap, roundPixels, srcRect, destRect, width, height);
		} catch (OutOfMemoryError e) {
			roundBitmap = bitmap;
		}

		return roundBitmap;
	}
	
	/**
	 * 处理圆角图片
	 * @param bitmap
	 * @param roundPixels
	 * @param srcRect
	 * @param destRect
	 * @param width
	 * @param height
	 * @return
	 */
	public Bitmap getRoundedCornerBitmap(Bitmap bitmap, int roundPixels, Rect srcRect, Rect destRect, int width, int height) {
		Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final Paint paint = new Paint();
		final RectF destRectF = new RectF(destRect);

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(0xFF000000);
		canvas.drawRoundRect(destRectF, roundPixels, roundPixels, paint);

		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(bitmap, srcRect, destRectF, paint);

		return output;
	}
	
	public int getRoundPixels() {
		return roundPixels;
	}

	public void setRoundPixels(int roundPixels) {
		this.roundPixels = roundPixels;
	}
}
