package com.github.panpf.sketch.util

/**
 * Platform-specific application directories for JVM or macOS desktop applications.
 *
 * @see com.github.panpf.sketch.core.jvm.test.util.AppDirsJvmTest
 * @see com.github.panpf.sketch.core.macos.test.util.AppDirsMacosTest
 */
expect class AppDirs private constructor() {

    companion object {

        /**
         * Get config files directory. Recommended appName: 'MyApp'
         *
         * Linux follows XDG_CONFIG_HOME. Windows follows APPDATA.
         *
         * appName is not null example:
         * * Windows: C:\Users\My\AppData\Roaming\${appName}
         * * macOS: /Users/My/Library/Preferences/${appName}
         * * Linux: /home/My/.config/${appName}
         *
         * appName is null example:
         * * Windows: C:\Users\My\AppData\Roaming
         * * macOS: /Users/My/Library/Preferences
         * * Linux: /home/My/.config
         */
        fun getConfigDir(appName: String?): String

        /**
         * Get data files directory. Recommended appName: 'MyApp'
         *
         * Linux follows XDG_DATA_HOME. Windows follows LOCALAPPDATA.
         *
         * appName is not null example:
         * * Windows: C:\Users\My\AppData\Local\${appName}
         * * macOS: /Users/My/Library/Application Support/${appName}
         * * Linux: /home/My/.local/share/${appName}
         *
         * appName is null example:
         * * Windows: C:\Users\My\AppData\Local
         * * macOS: /Users/My/Library/Application Support
         * * Linux: /home/My/.local/share
         */
        fun getDataDir(appName: String?): String

        /**
         * Get cache files directory. Recommended appName: 'MyApp'
         *
         * Linux follows XDG_CACHE_HOME. Windows follows LOCALAPPDATA.
         *
         * appName is not null example:
         * * Windows: C:\Users\My\AppData\Local\${appName}\Cache
         * * macOS: /Users/My/Library/Caches/${appName}
         * * Linux: /home/My/.cache/${appName}
         *
         * appName is null example:
         * * Windows: C:\Users\My\AppData\Local\Cache
         * * macOS: /Users/My/Library/Caches
         * * Linux: /home/My/.cache
         */
        fun getCacheDir(appName: String?): String

        /**
         * Get desktop files directory. Recommended appName: 'MyApp'
         *
         * Linux follows XDG_DESKTOP_DIR. Windows follows registry 'Desktop' property.
         *
         * appName is not null example:
         * * Windows: C:\Users\My\Desktop\${appName}
         * * macOS: /Users/My/Desktop/${appName}
         * * Linux: /home/My/Desktop/${appName}
         *
         * appName is null example:
         * * Windows: C:\Users\My\Desktop
         * * macOS: /Users/My/Desktop
         * * Linux: /home/My/Desktop
         */
        fun getDesktopDir(appName: String?): String

        /**
         * Get document files directory. Recommended appName: 'MyApp'
         *
         * Linux follows XDG_DOCUMENTS_DIR. Windows follows registry 'Personal' property.
         *
         * appName is not null example:
         * * Windows: C:\Users\My\Documents\${appName}
         * * macOS: /Users/My/Documents/${appName}
         * * Linux: /home/My/Documents/${appName}
         *
         * appName is null example:
         * * Windows: C:\Users\My\Documents
         * * macOS: /Users/My/Documents
         * * Linux: /home/My/Documents
         */
        fun getDocumentsDir(appName: String?): String

        /**
         * Get download files directory. Recommended appName: 'MyApp'
         *
         * Linux follows XDG_DOWNLOAD_DIR. Windows follows registry '{374DE290-123F-4565-9164-39C4925E467B}' property.
         *
         * appName is not null example:
         * * Windows: C:\Users\My\Downloads\${appName}
         * * macOS: /Users/My/Downloads/${appName}
         * * Linux: /home/My/Downloads/${appName}
         *
         * appName is null example:
         * * Windows: C:\Users\My\Downloads
         * * macOS: /Users/My/Downloads
         * * Linux: /home/My/Downloads
         */
        fun getDownloadsDir(appName: String?): String

        /**
         * Get picture files directory. Recommended appName: 'MyApp'
         *
         * Linux follows XDG_PICTURES_DIR. Windows follows registry 'My Pictures' property.
         *
         * appName is not null example:
         * * Windows: C:\Users\My\Pictures\${appName}
         * * macOS: /Users/My/Pictures/${appName}
         * * Linux: /home/My/Pictures/${appName}
         *
         * appName is null example:
         * * Windows: C:\Users\My\Pictures
         * * macOS: /Users/My/Pictures
         * * Linux: /home/My/Pictures
         */
        fun getPicturesDir(appName: String?): String

        /**
         * Get music files directory. Recommended appName: 'MyApp'
         *
         * Linux follows XDG_MUSIC_DIR. Windows follows registry 'My Music' property.
         *
         * appName is not null example:
         * * Windows: C:\Users\My\Music\${appName}
         * * macOS: /Users/My/Music/${appName}
         * * Linux: /home/My/Music/${appName}
         *
         * appName is null example:
         * * Windows: C:\Users\My\Music
         * * macOS: /Users/My/Music
         * * Linux: /home/My/Music
         */
        fun getMusicDir(appName: String?): String

        /**
         * Get video files directory. Recommended appName: 'MyApp'
         *
         * Linux follows XDG_VIDEOS_DIR. Windows follows registry 'My Video' property.
         *
         * appName is not null example:
         * * Windows: C:\Users\My\Videos\${appName}
         * * macOS: /Users/My/Movies/${appName}
         * * Linux: /home/My/Videos/${appName}
         *
         * appName is null example:
         * * Windows: C:\Users\My\Videos
         * * macOS: /Users/My/Movies
         * * Linux: /home/My/Videos
         */
        fun getVideosDir(appName: String?): String
    }
}