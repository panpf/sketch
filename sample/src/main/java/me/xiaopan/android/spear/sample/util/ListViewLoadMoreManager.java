package me.xiaopan.android.spear.sample.util;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.lang.reflect.Field;

/**
 * ListView加载更多管理器
 * <br>
 * <br>首先你需要使用ListViewLoadMoreManager(ListView, OnLoadMoreListener)构造函数来创建一个ListViewLoadMoreManager
 * <br>
 * <br>然后在加载完数据后调用ListViewLoadMoreManager的setAdapter(ListAdapter, boolean)方法来设置Adapter，第二个参数的意思是是否已全部加载完毕，如果为true将切换到end状态，并且永远不会再触发加载
 * <br>
 * <br>最后你需要在OnLoadMoreListener的onLoadMore()方法里分页加载更多的数据并根据加载结果调用loadFinished(boolean)或loadFailed()方法来结束加载
 * <br>值的注意的是loadFinished(boolean)方法的参数，意思是是否已全部加载完毕，如果为true将切换到end状态，并且永远不会再触发加载
 * <br>
 * <br>如果你想要加载完毕之后不显示“已加载完毕”尾巴就调用setDisableEndFooter(true)
 * <br>
 * <br>另外
 * <br>
 * <br>如果你想自定义ListFooterView那么你需要自定义一个View并实现LoadMoreListFooter接口，然后在创建ListViewLoadMoreManager的时候使用ListViewLoadMoreManager(ListView, LoadMoreListFooter, OnLoadMoreListener)构造函数并传入你自定义的ListFooterView
 * <br>
 * <br>默认开启了滚动到底部自动加载的功能，你可以通过setScrollToBottomAutoLoad()方法关闭
 * <br>
 * <br>由于需要设置OnScrollListener才能实现功能，所以你也要设置OnScrollListener的话就需要调用ListViewLoadMoreManager的setOnScrollListener来设置，如果在之前已经设置了OnScrollListener的话也不用担心，ListViewLoadMoreManager会先拿到已存在的OnScrollListener并回调它
 */
public class ListViewLoadMoreManager implements OnScrollListener{
	private static final String NAME = ListViewLoadMoreManager.class.getSimpleName();
	
	private int lastTriggerItem;	// 最后一次触发时间
	private boolean end;	// 是否已经结束加载（已经结束的话就不再处理任何事件或操作）
	private boolean loading;	// 表示是否正在加载（正在加载的时候就不再处理滚动事件）
	private boolean debugMode;	// 开启DEBUG模式，开启后会在控制台输出追踪LOG
	private boolean needRollback;	// 表示是否需要回滚
	private boolean allowClickLoad;	// 是否允许通过点击Footer触发加载
	private boolean footerViewAdded;	// 尾巴视图是否已经添加到ListView上了
	private boolean scrollToBottomAutoLoad;	// 滚动到底部自动加载
	private boolean disableFooterOnLoadEnd;	// 加载完毕之后不再显示尾巴

	private View footerView;
	private ListView listView;
	private OnScrollListener onScrollListener;
	private LoadMoreListFooter loadMoreListFooter;
	private OnLoadMoreListener onLoadMoreListener;
	
	/**
	 * @param listView 需要添加加载更多功能的列表
	 * @param footer 列表尾视图
	 * @param onLoadMoreListener 加载监听器
	 */
	public ListViewLoadMoreManager(ListView listView, LoadMoreListFooter footer, OnLoadMoreListener onLoadMoreListener) {
		this.listView = listView;
		this.loadMoreListFooter = footer; 
		this.footerView = (View) footer;
		this.onLoadMoreListener = onLoadMoreListener;
		
		this.footerView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(allowClickLoad){
					load();
				}
			}
		});
		try {
			Field field = ListView.class.getSuperclass().getDeclaredField("mOnScrollListener");
			field.setAccessible(true);
			Object object = field.get(listView);
			if(object != null && object instanceof OnScrollListener){
				this.onScrollListener = (OnScrollListener) object;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.listView.setOnScrollListener(this);
		this.scrollToBottomAutoLoad = true;
	}
	
	/**
	 * 将会采用默认的列表尾视图
	 * @param listView 需要添加加载更多功能的列表
	 * @param onLoadMoreListener 加载监听器
	 */
	public ListViewLoadMoreManager(ListView listView, OnLoadMoreListener onLoadMoreListener){
		this(listView, new DefaultLoadMoreListFooterView(listView.getContext()), onLoadMoreListener);
	}
	
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if(onScrollListener != null){
			onScrollListener.onScrollStateChanged(view, scrollState);
		}
	}
	
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		if(onScrollListener != null){
			onScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
		}
		
		if(!footerViewAdded){
			if(debugMode){
				Log.i(NAME, "onScroll：没有添加Footer到ListView");
			}
			return;
		}
		
		if(!scrollToBottomAutoLoad){
			if(debugMode){
				Log.i(NAME, "onScroll：滚动到底部自动加载功能已关闭，你可以通过setScrollToBottomAutoLoad()方法开启");
			}
			return;
		}
		
		int lastVisibleItem = firstVisibleItem + visibleItemCount;	// 当前最后一个显示的Item
		
		// 如果已经结束加载就直接结束不再处理
		if(end){
			if(debugMode){
				Log.i(NAME, "onScroll：已加载完毕："+lastVisibleItem);
			}
			return;
		}
		
		// 如果正在加载中就直接结束不再处理
		if(loading){
			if(debugMode){
				Log.i(NAME, "onScroll：正在加载中："+lastVisibleItem);
			}
			return;
		}
		
		// 如果内容尚未充满就直接结束不再处理
		if(visibleItemCount == totalItemCount){
			if(debugMode){
				Log.i(NAME, "onScroll：内容尚未充满："+lastVisibleItem);
			}
			return;
		}
 
		int triggerItem = totalItemCount - 1;
		
		// 如果触发项已经改变或者需要回滚
		if(lastTriggerItem == triggerItem && needRollback){
			// 如果当前滚动位置依然大于等于触发位置就直接结束不再处理
			if(lastVisibleItem >= triggerItem){
				if(debugMode){
					Log.d(NAME, "onScroll：需要回滚："+lastVisibleItem);
				}
				return;
			// 如果已经回滚完毕了就设置不需要回滚
			}else{
				if(debugMode){
					Log.w(NAME, "onScroll：回滚完毕："+lastVisibleItem);
				}
				needRollback = false;
			}
		}
		
		// 如果滚动到了最后一项，就启动加载
		if(lastVisibleItem >= triggerItem){
			if(onLoadMoreListener != null){
				lastTriggerItem = triggerItem;
				load();
			}
		}
	}
	
	/**
	 * 设置适配器
	 * @param listAdapter 适配器
	 * @param end true：已全部加载完毕，将回调LoadMoreListFooter.end()方法显示加载完毕；false：尚未加载完毕，将回调LoadMoreListFooter.clockLoad()方法显示点击加载
	 */
	public void setAdapter(ListAdapter listAdapter, boolean end){
		if(listView.getFooterViewsCount() > 0){
			listView.removeFooterView(footerView);
			footerViewAdded = false;
		}
		if(listAdapter != null && !(end && disableFooterOnLoadEnd)){
			listView.addFooterView((View) footerView);
			footerViewAdded = true;
		}
		listView.setAdapter(listAdapter);
		if(footerViewAdded){
			if(listAdapter != null){
				if(end){
					allowClickLoad = false;
					loadMoreListFooter.end();
				}else{
					allowClickLoad = true;
					loadMoreListFooter.clickLoad();
				}
				this.end = end;
			}else{
				allowClickLoad = true;
				loadMoreListFooter.clickLoad();
			}
		}
	}
	
	/**
	 * 开始加载
	 */
	private void load(){
		if(!footerViewAdded){
			if(debugMode){
				Log.i(NAME, "load：没有添加Footer到ListView");
			}
			return;
		}
		if(end){
			if(debugMode){
				Log.i(NAME, "load：已加载完毕");
			}
			return;
		}
		if(loading){
			if(debugMode){
				Log.i(NAME, "load：正在加载中");
			}
			return;
		}
 
		if(debugMode){
			Log.w(NAME, "load：开始加载");
		}
		loading = true;
		needRollback = true;
		allowClickLoad = false;
		loadMoreListFooter.loading();
		onLoadMoreListener.onLoadMore(this);
	}
	
	/**
	 * 加载完成
	 */
	public void loadFinished(boolean end){
		if(!footerViewAdded){
			if(debugMode){
				Log.i(NAME, "loadFinished：没有添加Footer到ListView");
			}
			return;
		}
		if(this.end){
			if(debugMode){
				Log.i(NAME, "loadFinished：已加载完毕");
			}
			return;
		}
		if(!loading){
			if(debugMode){
				Log.i(NAME, "loadFinished：尚未加载");
			}
			return;
		}
		
		if(end){
			if(debugMode){
				Log.w(NAME, "loadFinished：已全部加载完毕");
			}
			this.end = true;
			loading = false;
			allowClickLoad = false;
			listView.removeFooterView(footerView);
			footerViewAdded = false;
			loadMoreListFooter.end();
		}else{
			if(debugMode){
				Log.w(NAME, "loadFinished：加载完成");
			}
			loading = false;
			allowClickLoad = true;
			loadMoreListFooter.clickLoad();
		}
	}
	
	/**
	 * 加载失败
	 */
	public void loadFail(){
		if(!footerViewAdded){
			if(debugMode){
				Log.i(NAME, "loadFail：没有添加Footer到ListView");
			}
			return;
		}
		if(end){
			if(debugMode){
				Log.i(NAME, "loadFail：已加载完毕");
			}
			return;
		}
		if(!loading){
			if(debugMode){
				Log.i(NAME, "loadFail：尚未加载");
			}
			return;
		}
 
		if(debugMode){
			Log.e(NAME, "loadFail：加载失败");
		}
		loading = false;
		allowClickLoad = true;
		loadMoreListFooter.failed();
	}
	
	/**
	 * 设置加载完毕之后不再显示尾巴
	 * @param disableFooterOnLoadEnd
	 */
	public void setDisableEndFooter(boolean disableFooterOnLoadEnd) {
		this.disableFooterOnLoadEnd = disableFooterOnLoadEnd;
	}

	/**
	 * 设置滚动监听器
	 * @param onScrollListener
	 */
	public void setOnScrollListener(OnScrollListener onScrollListener) {
		this.onScrollListener = onScrollListener;
		listView.setOnScrollListener(this);
	}
 
	/**
	 * 设置DEBUG模式
	 * @param debugMode
	 */
	public void setDebugMode(boolean debugMode) {
		this.debugMode = debugMode;
	}
 
	/**
	 * 设置加载更多监听器
	 * @param onLoadMoreListener
	 */
	public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
		this.onLoadMoreListener = onLoadMoreListener;
	}
 
	/**
	 * 设置是否滚动到底部自动加载，默认true
	 * @param scrollToBottomAutoLoad
	 */
	public void setScrollToBottomAutoLoad(boolean scrollToBottomAutoLoad) {
		this.scrollToBottomAutoLoad = scrollToBottomAutoLoad;
	}
 
	/**
	 * 加载更多监听器
	 */
	public interface OnLoadMoreListener{
		/**
		 * 加载更多
		 * @param loadMore
		 */
		public void onLoadMore(ListViewLoadMoreManager loadMore);
	}
	
	/**
	 * 加载更多列表尾接口
	 */
	public interface LoadMoreListFooter{
		/**
		 * 点击加载
		 */
		public void clickLoad();
		
		/**
		 * 加载中
		 */
		public void loading();
		
		/**
		 * 加载失败
		 */
		public void failed();
		
		/**
		 * 已全部加载完毕
		 */
		public void end();
	}
	
	/**
	 * 默认的加载更多列表尾视图
	 */
	public static class DefaultLoadMoreListFooterView extends LinearLayout implements LoadMoreListFooter{
		private ProgressBar progressBar;
		private TextView textView;
		
		public DefaultLoadMoreListFooterView(Context context) {
			super(context);
			setGravity(Gravity.CENTER);
			setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp2px(getContext(), 50)));
			
			progressBar = new ProgressBar(getContext());
			int progressWidth = dp2px(getContext(), 16);
			addView(progressBar, new LayoutParams(progressWidth, progressWidth));
			
			textView = new TextView(getContext());
			textView.setTextColor(Color.GRAY);
			LayoutParams textViewParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			textViewParams.leftMargin = dp2px(getContext(), 8);
			addView(textView, textViewParams);
		}
		
		/**
		 * dp单位转换为px
		 * @param context 上下文，需要通过上下文获取到当前屏幕的像素密度
		 * @param dpValue dp值
		 * @return px值
		 */
		private int dp2px(Context context, float dpValue){
			return (int)(dpValue * (context.getResources().getDisplayMetrics().density) + 0.5f);
		}
 
		@Override
		public void loading() {
			progressBar.setVisibility(View.VISIBLE);
			textView.setText("正在加载，请稍后…");
		}
 
		@Override
		public void failed() {
			progressBar.setVisibility(View.GONE);
			textView.setText("加载更多失败，点击重新加载");
		}
 
		@Override
		public void end() {
			progressBar.setVisibility(View.GONE);
			textView.setText("THE END");
		}
 
		@Override
		public void clickLoad() {
			progressBar.setVisibility(View.GONE);
			textView.setText("点击加载更多");
		}
	}
}