package me.xiaopan.sketchsample.event;

import me.xiaopan.sketchsample.util.AppConfig;

public class AppConfigChangedEvent {
    public AppConfig.Key key;

    public AppConfigChangedEvent(AppConfig.Key key) {
        this.key = key;
    }
}
