package me.xiaoapn.easy.imageloader;

/**
 * Options工厂类，提供适合各种场景的Options
 */
public class OptionsFactory {
	/**
	 * 获取列表用的Options，其同ImageLoader默认的Options的不同之处在于其会将Bitmap缓存到内存中
	 * @return
	 */
	public static final Options getListOptions(){
		return ListOptionsHolder.listOptions;
	}
	
	private static class ListOptionsHolder{
		private static final Options listOptions = new Options.Builder()
			.setCachedInMemory(true)
			.setCacheInLocal(true)
			.setShowAnimationListener(new AlphaShowAnimationListener())
			.setBitmapLoader(new PixelsBitmapLoader()).create();
	}
}