package me.xiaopan.android.imageloader.task.load;

import java.util.concurrent.Callable;

import me.xiaopan.android.imageloader.task.Task;

public class FileBitmapLoadTask extends Task {

	public FileBitmapLoadTask(LoadRequest loadRequest, Callable<Object> callable) {
		super(loadRequest, new FileBitmapLoadCallable(loadRequest));
	}
	
	private static class FileBitmapLoadCallable implements Callable<Object>{
		private LoadRequest loadRequest;
		
		private FileBitmapLoadCallable(LoadRequest loadRequest) {
			this.loadRequest = loadRequest;
		}

		@Override
		public Object call() throws Exception {
			return null;
		}
	}
}
