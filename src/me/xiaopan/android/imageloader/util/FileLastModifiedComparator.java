package me.xiaopan.android.imageloader.util;

import java.io.File;
import java.util.Comparator;

/**
 * 文件最后修改日期比较器
 */
public class FileLastModifiedComparator implements Comparator<File> {
    @Override
    public int compare(File lhs, File rhs) {
        return (int) (lhs.lastModified() - rhs.lastModified());
    }
}
