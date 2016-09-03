package me.xiaopan.sketchsample.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class DoubleFinsEmptyView extends LinearLayout{
    public DoubleFinsEmptyView(Context context) {
        super(context);
        init(context);
    }

    public DoubleFinsEmptyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        Rect boundsRect = new Rect(0, 0, 100, 100);

        ArrayList<Rect> fullRectList = new ArrayList<Rect>();

        fullRectList.add(new Rect(0, 0, 25, 25));
        fullRectList.add(new Rect(25, 0, 50, 25));
        fullRectList.add(new Rect(50, 0, 75, 25));
        fullRectList.add(new Rect(75, 0, 100, 25));

        fullRectList.add(new Rect(0, 25, 25, 50));
        fullRectList.add(new Rect(25, 25, 50, 50));
        fullRectList.add(new Rect(50, 25, 75, 50));
        fullRectList.add(new Rect(75, 25, 100, 50));

        fullRectList.add(new Rect(0, 50, 25, 75));
        fullRectList.add(new Rect(25, 50, 50, 75));
        fullRectList.add(new Rect(50, 50, 75, 75));
        fullRectList.add(new Rect(75, 50, 100, 75));

        fullRectList.add(new Rect(0, 75, 25, 100));
        fullRectList.add(new Rect(25, 75, 50, 100));
        fullRectList.add(new Rect(50, 75, 75, 100));
        fullRectList.add(new Rect(75, 75, 100, 100));

        setOrientation(VERTICAL);

        FindEmptyView originView = new FindEmptyView(context);
        originView.setBoundsRect(boundsRect);
        originView.setFullRectList(fullRectList);

        FindEmptyView findEmptyView = new FindEmptyView(context);
        findEmptyView.setBoundsRect(boundsRect);
        ArrayList<Rect> newFullRectList = new ArrayList<Rect>(fullRectList.size());
        for (Rect fullRect : fullRectList) {
            newFullRectList.add(fullRect);
        }
        List<Rect> emptyRectList = findEmptyRect(boundsRect, newFullRectList);
        findEmptyView.setFullRectList(newFullRectList);
        findEmptyView.setEmptyRectList(emptyRectList);

        addView(originView, new LayoutParams(500, 500));
        addView(findEmptyView, new LayoutParams(500, 500));
    }

    public static List<Rect> findEmptyRect(Rect rect, List<Rect> childRectList) {
        if (rect.isEmpty()) {
            return null;
        }

        List<Rect> emptyRectList = null;
        if (childRectList == null || childRectList.size() == 0) {
            emptyRectList = new LinkedList<Rect>();
            emptyRectList.add(rect);
            return emptyRectList;
        }

        // 按离左上角的距离排序
        Collections.sort(childRectList, new Comparator<Rect>() {
            @Override
            public int compare(Rect o1, Rect o2) {
                if (o1.top >= o2.bottom || o2.top >= o1.bottom) {
                    return o1.top - o2.top;
                } else {
                    return o1.left - o2.left;
                }
            }
        });

        int left = rect.left, top = rect.top, right = 0, bottom = -1;
        Rect lastRect = null;
        Rect childRect;
        Iterator<Rect> rectIterator = childRectList.iterator();
        while (rectIterator.hasNext()) {
            childRect = rectIterator.next();

            boolean newLine = lastRect == null || (childRect.top >= bottom);
            if (newLine) {
                // 首先要处理上一行的最后一个
                if (lastRect != null) {
                    if (lastRect.right < rect.right) {
                        Rect rightEmptyRect = new Rect(lastRect.right, top, rect.right, bottom);
                        if (emptyRectList == null) {
                            emptyRectList = new LinkedList<Rect>();
                        }
                        emptyRectList.add(rightEmptyRect);
                    }
                }

                // 然后要更新top和bottom
                top = bottom != -1 ? bottom : top;
                bottom = childRect.bottom;

                // 左边有空隙
                if (childRect.left > left) {
                    Rect leftEmptyRect = new Rect(left, childRect.top, childRect.left, childRect.bottom);
                    if (emptyRectList == null) {
                        emptyRectList = new LinkedList<Rect>();
                    }
                    emptyRectList.add(leftEmptyRect);
                }

                // 顶部有空隙
                if (childRect.top > top) {
                    Rect topEmptyRect = new Rect(left, top, childRect.right, childRect.top);
                    if (emptyRectList == null) {
                        emptyRectList = new LinkedList<Rect>();
                    }
                    emptyRectList.add(topEmptyRect);
                }

                right = childRect.right;
                lastRect = childRect;
            } else {
                boolean available = childRect.bottom == lastRect.bottom;
                if (available) {
                    // 左边有空隙
                    if (childRect.left > right) {
                        Rect leftEmptyRect = new Rect(right, top, childRect.left, bottom);
                        if (emptyRectList == null) {
                            emptyRectList = new LinkedList<Rect>();
                        }
                        emptyRectList.add(leftEmptyRect);
                    }

                    // 顶部有空隙
                    if (childRect.top > top) {
                        Rect topEmptyRect = new Rect(childRect.left, top, childRect.right, childRect.top);
                        if (emptyRectList == null) {
                            emptyRectList = new LinkedList<Rect>();
                        }
                        emptyRectList.add(topEmptyRect);
                    }

                    right = childRect.right;
                    lastRect = childRect;
                } else {
                    rectIterator.remove();
                }
            }
        }

        // 最后的结尾处理
        if (right < rect.right) {
            Rect rightEmptyRect = new Rect(right, top, rect.right, bottom);
            if (emptyRectList == null) {
                emptyRectList = new LinkedList<Rect>();
            }
            emptyRectList.add(rightEmptyRect);
        }

        if (bottom < rect.bottom) {
            Rect bottomEmptyRect = new Rect(rect.left, bottom, rect.right, rect.bottom);
            if (emptyRectList == null) {
                emptyRectList = new LinkedList<Rect>();
            }
            emptyRectList.add(bottomEmptyRect);
        }

        return emptyRectList;
    }
}
