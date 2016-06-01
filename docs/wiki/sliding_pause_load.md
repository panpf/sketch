Sketch支持暂停加载，暂停之后Sketch将只会从内存中去找图片，如果结束了，你可以利用这个功能进一步提升列表的滑动流畅度

首先你需要添加ScrollingPauseLoadManager.java到你的项目中，支持RecyclerView和AbsListView
```java
/**
 * 滚动中暂停暂停加载新图片管理器支持RecyclerView和AbsListView
 */
public class ScrollingPauseLoadManager extends RecyclerView.OnScrollListener implements AbsListView.OnScrollListener{
    private Sketch sketch;
    private AbsListView.OnScrollListener absListScrollListener;
    private RecyclerView.OnScrollListener recyclerScrollListener;

    public ScrollingPauseLoadManager(Context context) {
        this.sketch = Sketch.with(context);
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

        if(recyclerView.getAdapter() != null){
            if(newState == RecyclerView.SCROLL_STATE_DRAGGING){
                sketch.getConfiguration().setPauseLoad(true);
            } else if(newState == RecyclerView.SCROLL_STATE_IDLE){
                if(sketch.getConfiguration().isPauseLoad()){
                    sketch.getConfiguration().setPauseLoad(false);
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
        if(view.getAdapter() != null){
            ListAdapter listAdapter = view.getAdapter();
            if(listAdapter instanceof WrapperListAdapter){
                listAdapter = ((WrapperListAdapter)listAdapter).getWrappedAdapter();
            }
            if(listAdapter instanceof BaseAdapter){
                if(scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                    if(!sketch.getConfiguration().isPauseLoad()){
                        sketch.getConfiguration().setPauseLoad(true);
                    }
                } else if(scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE){
                    if(sketch.getConfiguration().isPauseLoad()){
                        sketch.getConfiguration().setPauseLoad(false);
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
>* 在配置较高的设备上不建议使用此功能，因为实时显示图片的体验要远高于滑动时暂停加载新图片的体验。特别是在列表页点击进入一个新页面又返回的时候，由于新页面加载了新的图片把列表页中图片的缓存挤掉了，回到列表后就会刷新一下重新加载图片，这个体验很不好。Sketch已经对此做了非常多的优化，可以保证滑动列表时实时加载图片的流畅度，除非你的列表特别复杂，否则不会有明显的卡顿。
>* 那么此功能我是做着玩的嘛？当然不是，在一些比较老性能很差劲的设备上，开启此功能还是很有必要的。你可以通过Androd版本号进行判断并开启此功能。