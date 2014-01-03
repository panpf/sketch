package me.xiaoapn.easy.imageloader.cache;

/**
 * 缓存配置
 */
public class CacheConfig {
	private boolean isCacheInMemory;	//是否每次加载图片的时候先从内存中去找，并且加载完成后将图片缓存在内存中
	private boolean isCacheInDisk;	//是否需要将图片缓存到磁盘
	private int diskCachePeriodOfValidity;	//磁盘缓存有效期，单位毫秒
	private String diskCacheDirectory;	//磁盘缓存目录
	
	private CacheConfig(){};

	/**
	 * 是否将Bitmap缓存到内存中
	 * @return
	 */
	public boolean isCacheInMemory() {
		return isCacheInMemory;
	}

	/**
	 * 设置是否将Bitmap缓存到内存中
	 * @param isCacheInMemory
	 */
	public void setCacheInMemory(boolean isCacheInMemory) {
		this.isCacheInMemory = isCacheInMemory;
	}

	/**
	 * 是否将网络上的图片缓存到本地，缓存到本地后当内存中的Bitmap被回收就可以从本地读取，而不必再从网络上下载
	 * @return
	 */
	public boolean isCacheInDisk() {
		return isCacheInDisk;
	}

	/**
	 * 设置是否将网络上的图片缓存到本地，缓存到本地后当内存中的Bitmap被回收就可以从本地读取，而不必再从网络上下载
	 * @param isCacheInDisk
	 */
	public void setCacheInDisk(boolean isCacheInDisk) {
		this.isCacheInDisk = isCacheInDisk;
	}

	/**
	 * 获取本地缓存文件的有效时间，单位毫秒
	 * @return
	 */
	public int getDiskCachePeriodOfValidity() {
		return diskCachePeriodOfValidity;
	}

	/**
	 * 设置本地缓存文件的有效时间，单位毫秒
	 * @param diskCachePeriodOfValidity
	 */
	public void setDiskCachePeriodOfValidity(int diskCachePeriodOfValidity) {
		this.diskCachePeriodOfValidity = diskCachePeriodOfValidity;
	}

	/**
	 * 获取本地缓存目录
	 * @return
	 */
	public String getDiskCacheDirectory() {
		return diskCacheDirectory;
	}

	/**
	 * 设置本地缓存目录
	 * @param diskCacheDirectory
	 */
	public void setDiskCacheDirectory(String diskCacheDirectory) {
		this.diskCacheDirectory = diskCacheDirectory;
	}

	/**
	 * 拷贝
	 * @return
	 */
	public CacheConfig copy(){
		return new Builder().setCacheInMemory(isCacheInMemory).setCacheInDisk(isCacheInDisk).setDiskCachePeriodOfValidity(diskCachePeriodOfValidity).setDiskCacheDirectory(diskCacheDirectory).build();
	}
	
	public static class Builder{
		private CacheConfig cacheConfig;

		public Builder() {
			this.cacheConfig = new CacheConfig();
		}
		
		/**
		 * 设置是否将Bitmap缓存到内存中
		 * @param isCacheInMemory
		 */
		public Builder setCacheInMemory(boolean isCacheInMemory) {
			cacheConfig.setCacheInMemory(isCacheInMemory);
			return this;
		}

		/**
		 * 设置是否将网络上的图片缓存到本地，缓存到本地后当内存中的Bitmap被回收就可以从本地读取，而不必再从网络上下载
		 * @param isCacheInDisk
		 */
		public Builder setCacheInDisk(boolean isCacheInDisk) {
			cacheConfig.setCacheInDisk(isCacheInDisk);
			return this;
		}

		/**
		 * 设置本地缓存文件的有效时间，单位毫秒
		 * @param diskCachePeriodOfValidity
		 */
		public Builder setDiskCachePeriodOfValidity(int diskCachePeriodOfValidity) {
			cacheConfig.setDiskCachePeriodOfValidity(diskCachePeriodOfValidity);
			return this;
		}

		/**
		 * 设置本地缓存目录
		 * @param diskCacheDirectory
		 */
		public Builder setDiskCacheDirectory(String diskCacheDirectory) {
			cacheConfig.setDiskCacheDirectory(diskCacheDirectory);
			return this;
		}
		
		public CacheConfig build(){
			return cacheConfig;
		}
	}
}
