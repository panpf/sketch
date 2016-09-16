/*
 * Copyright (C) 2016 Peng fei Pan <sky@xiaopan.me>
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

package me.xiaopan.sketchsample.menu;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.ImageView;

import me.xiaopan.sketch.SketchImageView;

public class ImageMenu {

    private Activity activity;
    private SketchImageView imageView;

    private AlertDialog tempAlertDialog;

    public ImageMenu(Activity activity, SketchImageView imageView) {
        this.activity = activity;
        this.imageView = imageView;
    }

    public void show() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setTitle("菜单");

        String[] items = new String[3];
        items[0] = "ScaleType: " + (imageView.isSupportZoom() ? imageView.getImageZoomFunction().getScaleType() : imageView.getScaleType());
        items[1] = "显示分块区域: " + (imageView.isSupportLargeImage() && imageView.getLargeImageViewer().isShowTileRect());
        items[2] = "阅读模式: " + (imageView.isSupportZoom() && imageView.getImageZoomFunction().getImageZoomer().isReadMode());
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                tempAlertDialog.dismiss();

                switch (which) {
                    case 0:
                        showScaleTypeMenu();
                        break;
                    case 1:
                        if (imageView.isSupportLargeImage()) {
                            boolean newShowTileRect = !imageView.getLargeImageViewer().isShowTileRect();
                            imageView.getLargeImageViewer().setShowTileRect(newShowTileRect);
                        }
                        break;
                    case 2:
                        if (imageView.isSupportZoom()) {
                            boolean newReadMode = !imageView.getImageZoomFunction().getImageZoomer().isReadMode();
                            imageView.getImageZoomFunction().getImageZoomer().setReadMode(newReadMode);
                        }
                        break;
                }
            }
        });

        builder.setNegativeButton("取消", null);
        tempAlertDialog = builder.show();
    }

    private void showScaleTypeMenu() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setTitle("ScaleType");

        String[] items = new String[7];
        items[0] = "CENTER";
        items[1] = "CENTER_CROP";
        items[2] = "CENTER_INSIDE";
        items[3] = "FIT_START";
        items[4] = "FIT_CENTER";
        items[5] = "FIT_END";
        items[6] = "FIT_XY";

        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                tempAlertDialog.dismiss();

                switch (which) {
                    case 0:
                        imageView.setScaleType(ImageView.ScaleType.CENTER);
                        break;
                    case 1:
                        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        break;
                    case 2:
                        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                        break;
                    case 3:
                        imageView.setScaleType(ImageView.ScaleType.FIT_START);
                        break;
                    case 4:
                        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        break;
                    case 5:
                        imageView.setScaleType(ImageView.ScaleType.FIT_END);
                        break;
                    case 6:
                        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                        break;
                }
            }
        });

        builder.setNegativeButton("取消", null);
        tempAlertDialog = builder.show();
    }
}
