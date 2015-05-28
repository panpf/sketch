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

package me.xiaopan.sketch;

import java.io.File;

public class DownloadResult {
    private Object result;
    private boolean fromNetwork;

    private DownloadResult(){

    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public boolean isFromNetwork() {
        return fromNetwork;
    }

    public void setFromNetwork(boolean fromNetwork) {
        this.fromNetwork = fromNetwork;
    }

    public static DownloadResult createByFile(File resultFile, boolean fromNetwork){
        DownloadResult result = new DownloadResult();
        result.setResult(resultFile);
        result.setFromNetwork(fromNetwork);
        return result;
    }

    public static DownloadResult createByByteArray(byte[] resultDate, boolean fromNetwork){
        DownloadResult result = new DownloadResult();
        result.setResult(resultDate);
        result.setFromNetwork(fromNetwork);
        return result;
    }
}
