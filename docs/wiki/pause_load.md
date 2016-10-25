Sketch支持全局暂停从本地加载图片，暂停后Sketch将只会从内存中去找图片

``暂停加载功能只对display请求有效``

你只需执行如下代码设置即可：
```java
// 全局暂停从本地加载载图片
Sketch.with(context).getConfiguration().setGlobalPauseLoad(true);

// 全局恢复从本地加载载图片
Sketch.with(context).getConfiguration().setGlobalPauseLoad(false);
```

#### 滑动时停止加载
你可以利用这个功能实现列表滑动中不加载图片，进一步提升列表的滑动流畅度

首先你需要添加ScrollingPauseLoadManager.java到你的项目中，支持RecyclerView和AbsListView
```java
/**
 * 滚动中暂停暂停加载新图片管理器支持RecyclerView和AbsListView
 */
public class ScrollingPauseLoadManager extends RecyclerView.OnScrollListener implements AbsListView.OnScrollListener{
    private Sketch sketch;
    private Settings settings;
    private AbsListView.OnScrollListener absListScrollListener;
    private RecyclerView.OnScrollListener recyclerScrollListener;

    public ScrollingPauseLoadManager(Context context) {
        this.sketch = Sketch.with(context);
        this.settings = Settings.with(context);
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        if(recyclerScrollListener != null){
            recyclerScrollListener.onScrolled(recyclerView, dx, dy);
        }
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);

        if(settings.isScrollingPauseLoad() && recyclerView.getAdapter() != null){
            if(newState == RecyclerView.SCROLL_STATE_DRAGGING){
                sketch.getConfiguration().setGlobalPauseLoad(true);
            } else if(newState == RecyclerView.SCROLL_STATE_IDLE){
                if(sketch.getConfiguration().isGlobalPauseLoad()){
                    sketch.getConfiguration().setGlobalPauseLoad(false);
                    recyclerView.getAdapter().notifyDataSetChanged();
                }
            }
        }

        if(recyclerScrollListener != null){
            recyclerScrollListener.onScrollStateChanged(recyclerView, newState);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if(settings.isScrollingPauseLoad() && view.getAdapter() != null){
            ListAdapter listAdapter = view.getAdapter();
            if(listAdapter instanceof WrapperListAdapter){
                listAdapter = ((WrapperListAdapter)listAdapter).getWrappedAdapter();
            }
            if(listAdapter instanceof BaseAdapter){
                if(scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                    if(!sketch.getConfiguration().isGlobalPauseLoad()){
                        sketch.getConfiguration().setGlobalPauseLoad(true);
                    }
                } else if(scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE){
                    if(sketch.getConfiguration().isGlobalPauseLoad()){
                        sketch.getConfiguration().setGlobalPauseLoad(false);
                        ((BaseAdapter)listAdapter).notifyDataSetChanged();
                    }
                }
            }
        }

        if(absListScrollListener != null){
            absListScrollListener.onScrollStateChanged(view, scrollState);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if(absListScrollListener != null){
            absListScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
    }

    public void setOnScrollListener(AbsListView.OnScrollListener absListViewScrollListener) {
        this.absListScrollListener = absListViewScrollListener;
    }

    public void setOnScrollListener(RecyclerView.OnScrollListener recyclerScrollListener) {
        this.recyclerScrollListener = recyclerScrollListener;
    }
}
```

然后应用即可
```java
RecyclerView recyclerView = ...;
recyclerView.setOnScrollListener(new ScrollingPauseLoadManager(context));

ListView listView = ...;
listView.setOnScrollListener(new ScrollingPauseLoadManager(context));
```

注意：
>* 在配置较高的设备上不建议使用此功能，因为实时显示图片的体验要远高于滑动时暂停加载新图片的体验。特别是在列表页点击进入一个新页面又返回的时候，由于新页面加载了新的图片把列表页中图片的缓存挤掉了，回到列表后就会刷新一下重新加载图片
>* 那么此功能我是做着玩的嘛？当然不是，在一些比较老性能很差劲的设备上，开启此功能还是很有必要的。你可以通过Android版本号进行判断并开启此功能。
