package me.xiaopan.android.imageloader.task;

import me.xiaopan.android.imageloader.Configuration;


public class TaskRequest {
	private String name;	//名称
	private Configuration configuration;	//配置

	/**
	 * 获取名称，用于在输出log时区分不同的请求
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * 设置名称，用于在输出log时区分不同的请求
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 获取配置
	 * @return
	 */
	public Configuration getConfiguration() {
		return configuration;
	}

	/**
	 * 设置配置
	 * @param configuration
	 */
	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}
}
