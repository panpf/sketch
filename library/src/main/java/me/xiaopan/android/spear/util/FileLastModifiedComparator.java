/*
 * Copyright (C) 2013 Peng fei Pan <sky@xiaopan.me>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.xiaopan.android.spear.util;

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
