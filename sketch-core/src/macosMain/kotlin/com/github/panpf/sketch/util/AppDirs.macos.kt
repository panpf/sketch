package com.github.panpf.sketch.util

import platform.Foundation.NSCachesDirectory
import platform.Foundation.NSDesktopDirectory
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSDownloadsDirectory
import platform.Foundation.NSLibraryDirectory
import platform.Foundation.NSMoviesDirectory
import platform.Foundation.NSMusicDirectory
import platform.Foundation.NSPicturesDirectory
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSUserDomainMask

/**
 * Platform-specific application directories for macos native desktop applications.
 *
 * @see com.github.panpf.sketch.core.macos.test.util.AppDirsMacosTest
 */
actual class AppDirs private actual constructor() {

    actual companion object {

        /**
         * Get config files directory. Recommended appName: 'MyApp'
         *
         * appName is not null example:
         * * macOS: /Users/My/Library/Preferences/${appName}
         *
         * appName is null example:
         * * macOS: /Users/My/Library/Preferences
         */
        actual fun getConfigDir(appName: String?): String {
            val paths =
                NSSearchPathForDirectoriesInDomains(NSLibraryDirectory, NSUserDomainMask, true)
            val cachesDirectory = (paths.firstOrNull() as? String)
                ?: throw IllegalStateException("Unable to find Library directory")
            return if (appName == null) {
                "${cachesDirectory}/Preferences"
            } else {
                "${cachesDirectory}/Preferences/${appName}"
            }
        }

        /**
         * Get data files directory. Recommended appName: 'MyApp'
         *
         * appName is not null example:
         * * macOS: /Users/My/Library/Application Support/${appName}
         *
         * appName is null example:
         * * macOS: /Users/My/Library/Application Support
         */
        actual fun getDataDir(appName: String?): String {
            val paths =
                NSSearchPathForDirectoriesInDomains(NSLibraryDirectory, NSUserDomainMask, true)
            val cachesDirectory = (paths.firstOrNull() as? String)
                ?: throw IllegalStateException("Unable to find Library directory")
            return if (appName == null) {
                "${cachesDirectory}/Application Support"
            } else {
                "${cachesDirectory}/Application Support/${appName}"
            }
        }

        /**
         * Get cache files directory. Recommended appName: 'MyApp'
         *
         * appName is not null example:
         * * macOS: /Users/My/Library/Caches/${appName}
         *
         * appName is null example:
         * * macOS: /Users/My/Library/Caches
         */
        actual fun getCacheDir(appName: String?): String {
            val paths =
                NSSearchPathForDirectoriesInDomains(NSCachesDirectory, NSUserDomainMask, true)
            val cachesDirectory = (paths.firstOrNull() as? String)
                ?: throw IllegalStateException("Unable to find Caches directory")
            return if (appName == null) {
                cachesDirectory
            } else {
                "${cachesDirectory}/${appName}"
            }
        }

        /**
         * Get desktop files directory. Recommended appName: 'MyApp'
         *
         * appName is not null example:
         * * macOS: /Users/My/Desktop/${appName}
         *
         * appName is null example:
         * * macOS: /Users/My/Desktop
         */
        actual fun getDesktopDir(appName: String?): String {
            val paths =
                NSSearchPathForDirectoriesInDomains(NSDesktopDirectory, NSUserDomainMask, true)
            val cachesDirectory = (paths.firstOrNull() as? String)
                ?: throw IllegalStateException("Unable to find Desktop directory")
            return if (appName == null) {
                cachesDirectory
            } else {
                "${cachesDirectory}/${appName}"
            }
        }

        /**
         * Get document files directory. Recommended appName: 'MyApp'
         *
         * appName is not null example:
         * * macOS: /Users/My/Documents/${appName}
         *
         * appName is null example:
         * * macOS: /Users/My/Documents
         */
        actual fun getDocumentsDir(appName: String?): String {
            val paths =
                NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, true)
            val cachesDirectory = (paths.firstOrNull() as? String)
                ?: throw IllegalStateException("Unable to find Documents directory")
            return if (appName == null) {
                cachesDirectory
            } else {
                "${cachesDirectory}/${appName}"
            }
        }

        /**
         * Get download files directory. Recommended appName: 'MyApp'
         *
         * appName is not null example:
         * * macOS: /Users/My/Downloads/${appName}
         *
         * appName is null example:
         * * macOS: /Users/My/Downloads
         */
        actual fun getDownloadsDir(appName: String?): String {
            val paths =
                NSSearchPathForDirectoriesInDomains(NSDownloadsDirectory, NSUserDomainMask, true)
            val cachesDirectory = (paths.firstOrNull() as? String)
                ?: throw IllegalStateException("Unable to find Downloads directory")
            return if (appName == null) {
                cachesDirectory
            } else {
                "${cachesDirectory}/${appName}"
            }
        }

        /**
         * Get picture files directory. Recommended appName: 'MyApp'
         *
         * appName is not null example:
         * * macOS: /Users/My/Pictures/${appName}
         *
         * appName is null example:
         * * macOS: /Users/My/Pictures
         */
        actual fun getPicturesDir(appName: String?): String {
            val paths =
                NSSearchPathForDirectoriesInDomains(NSPicturesDirectory, NSUserDomainMask, true)
            val cachesDirectory = (paths.firstOrNull() as? String)
                ?: throw IllegalStateException("Unable to find Pictures directory")
            return if (appName == null) {
                cachesDirectory
            } else {
                "${cachesDirectory}/${appName}"
            }
        }

        /**
         * Get music files directory. Recommended appName: 'MyApp'
         *
         * appName is not null example:
         * * macOS: /Users/My/Music/${appName}
         *
         * appName is null example:
         * * macOS: /Users/My/Music
         */
        actual fun getMusicDir(appName: String?): String {
            val paths =
                NSSearchPathForDirectoriesInDomains(NSMusicDirectory, NSUserDomainMask, true)
            val cachesDirectory = (paths.firstOrNull() as? String)
                ?: throw IllegalStateException("Unable to find Music directory")
            return if (appName == null) {
                cachesDirectory
            } else {
                "${cachesDirectory}/${appName}"
            }
        }

        /**
         * Get video files directory. Recommended appName: 'MyApp'
         *
         * appName is not null example:
         * * macOS: /Users/My/Movies/${appName}
         *
         * appName is null example:
         * * macOS: /Users/My/Movies
         */
        actual fun getVideosDir(appName: String?): String {
            val paths =
                NSSearchPathForDirectoriesInDomains(NSMoviesDirectory, NSUserDomainMask, true)
            val cachesDirectory = (paths.firstOrNull() as? String)
                ?: throw IllegalStateException("Unable to find Movies directory")
            return if (appName == null) {
                cachesDirectory
            } else {
                "${cachesDirectory}/${appName}"
            }
        }
    }
}