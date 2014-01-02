package me.xiaoapn.easy.imageloader.download;

import java.io.File;

public interface OnCompleteListener {
	public void onComplete(File cacheFile);
	public void onComplete(byte[] data);
	public void onFailed();
}
