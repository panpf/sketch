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

package me.xiaopan.android.spear.execute;

import java.util.concurrent.Executor;

/**
 * 请求执行器
 */
public interface RequestExecutor {
    /**
     * 获取请求分发执行器
     */
    public Executor getRequestDispatchExecutor();

    /**
     * 获取本地任务执行器
     */
    public Executor getLocalRequestExecutor();

    /**
     * 获取网络任务执行器
     */
    public Executor getNetRequestExecutor();
}