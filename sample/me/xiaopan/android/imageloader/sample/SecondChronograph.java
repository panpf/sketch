package me.xiaopan.android.imageloader.sample;

/**
 * 秒表
 */
public class SecondChronograph {
	private long firstMillis;	//第一次的时间
	private long lastFirstMillis;	//上一次的时间
	private Count count;
	private long newMillis;
	
	public SecondChronograph(){
		firstMillis = System.currentTimeMillis();
		lastFirstMillis = firstMillis;
	}
	
	/**
	 * 计次，返回上一次计次到当前的间隔时间
	 * @return
	 */
	public Count count(){
		newMillis = System.currentTimeMillis();
		count = new Count(newMillis - firstMillis, newMillis - lastFirstMillis);
		lastFirstMillis = newMillis;
		return count;
	}
	
	/**
	 * 重置
	 */
	public void reset(){
		firstMillis = System.currentTimeMillis();
		lastFirstMillis = firstMillis;
	}
	
	/**
	 * 计次
	 */
	public class Count{
		private long intervalMillis;
		private long useMillis;
		
		public Count(long useMillis, long intervalMillis){
			this.useMillis = useMillis;
			this.intervalMillis = intervalMillis;
		}
		
		/**
		 * 获取距离上一次计次的间隔时间
		 * @return
		 */
		public long getIntervalMillis() {
			return intervalMillis;
		}
		
		/**
		 * 获取当前总用时
		 * @return
		 */
		public long getUseMillis() {
			return useMillis;
		}
	}
}