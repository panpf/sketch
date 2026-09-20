package com.github.panpf.sketch.util

import java.io.File
import java.nio.charset.StandardCharsets
import java.util.Locale

/**
 * Platform-specific application directories for JVM desktop applications.
 *
 * @see com.github.panpf.sketch.core.jvm.test.util.AppDirsJvmTest
 */
actual class AppDirs private actual constructor() {

    actual companion object {
        private val provider: Provider by lazy {
            val os = System.getProperty("os.name").lowercase(Locale.ROOT)
            when {
                os.contains("mac") || os.contains("darwin") -> MacOSProvider()
                os.contains("windows") || os.contains("win") -> WindowsProvider()
                else -> UnixProvider()
            }
        }

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
        actual fun getConfigDir(appName: String?): String = provider.getConfigDir(appName).path

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
        actual fun getDataDir(appName: String?): String = provider.getDataDir(appName).path

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
        actual fun getCacheDir(appName: String?): String = provider.getCacheDir(appName).path

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
        actual fun getDesktopDir(appName: String?): String = provider.getDesktopDir(appName).path

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
        actual fun getDocumentsDir(appName: String?): String =
            provider.getDocumentsDir(appName).path

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
        actual fun getDownloadsDir(appName: String?): String =
            provider.getDownloadsDir(appName).path

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
        actual fun getPicturesDir(appName: String?): String = provider.getPicturesDir(appName).path

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
        actual fun getMusicDir(appName: String?): String = provider.getMusicDir(appName).path

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
        actual fun getVideosDir(appName: String?): String = provider.getVideosDir(appName).path
    }

    private interface Provider {
        fun getConfigDir(appName: String?): File
        fun getDataDir(appName: String?): File
        fun getCacheDir(appName: String?): File
        fun getDesktopDir(appName: String?): File
        fun getDocumentsDir(appName: String?): File
        fun getDownloadsDir(appName: String?): File
        fun getPicturesDir(appName: String?): File
        fun getMusicDir(appName: String?): File
        fun getVideosDir(appName: String?): File
    }

    private class WindowsProvider : Provider {

        private val userHome: File by lazy {
            File(System.getProperty("user.home"))
        }

        private val windowsAppDataDir: File by lazy {
            environmentDirectory(
                environmentVariable = "APPDATA",
                fallback = { userHome.resolve("AppData/Roaming") }
            )
        }

        private val windowsLocalAppDataDir: File by lazy {
            environmentDirectory(
                environmentVariable = "LOCALAPPDATA",
                fallback = { userHome.resolve("AppData/Local") }
            )
        }

        private fun environmentDirectory(environmentVariable: String, fallback: () -> File): File {
            return System.getenv(environmentVariable)
                ?.takeIf { it.isNotBlank() }
                ?.let(::File)
                ?: fallback()
        }

        override fun getConfigDir(appName: String?): File {
            return if (appName == null) {
                windowsAppDataDir
            } else {
                windowsAppDataDir.resolve(appName)
            }
        }

        override fun getDataDir(appName: String?): File {
            return if (appName == null) {
                windowsLocalAppDataDir
            } else {
                windowsLocalAppDataDir.resolve(appName)
            }
        }

        override fun getCacheDir(appName: String?): File {
            return if (appName == null) {
                windowsLocalAppDataDir.resolve("Cache")
            } else {
                windowsLocalAppDataDir.resolve("$appName${File.separator}Cache")
            }
        }

        override fun getDesktopDir(appName: String?): File {
            val desktopDir = windowsKnownFolder(
                registryName = "Desktop",
                fallback = "Desktop"
            )
            return if (appName == null) {
                desktopDir
            } else {
                desktopDir.resolve(appName)
            }
        }

        override fun getDocumentsDir(appName: String?): File {
            val documentsDir = windowsKnownFolder(
                registryName = "Personal",
                fallback = "Documents"
            )
            return if (appName == null) {
                documentsDir
            } else {
                documentsDir.resolve(appName)
            }
        }

        override fun getDownloadsDir(appName: String?): File {
            val downloadsDir = windowsKnownFolder(
                registryName = "{374DE290-123F-4565-9164-39C4925E467B}",
                fallback = "Downloads"
            )
            return if (appName == null) {
                downloadsDir
            } else {
                downloadsDir.resolve(appName)
            }
        }

        override fun getPicturesDir(appName: String?): File {
            val picturesDir = windowsKnownFolder(
                registryName = "My Pictures",
                fallback = "Pictures"
            )
            return if (appName == null) {
                picturesDir
            } else {
                picturesDir.resolve(appName)
            }
        }

        override fun getMusicDir(appName: String?): File {
            val musicDir = windowsKnownFolder(
                registryName = "My Music",
                fallback = "Music"
            )
            return if (appName == null) {
                musicDir
            } else {
                musicDir.resolve(appName)
            }
        }

        override fun getVideosDir(appName: String?): File {
            val videosDir = windowsKnownFolder(
                registryName = "My Video",
                fallback = "Videos"
            )
            return if (appName == null) {
                videosDir
            } else {
                videosDir.resolve(appName)
            }
        }

        /**
         * Reads a Windows user directory from:
         *
         * HKCU\Software\Microsoft\Windows\CurrentVersion\Explorer\User Shell Folders
         *
         * Example:
         *     Personal = D:\Documents
         *
         * This is preferable to:
         *     C:\Users\<user>\Documents
         *
         * because Windows allows users to move Known Folders.
         * reg.exe is part of Windows, so no third-party dependency is required.
         */
        private fun windowsKnownFolder(
            registryName: String,
            fallback: String,
        ): File {
            val value = readWindowsUserShellFolder(valueName = registryName)
            if (value != null) {
                return File(value)
            }
            return File(userHome, fallback)
        }

        private fun readWindowsUserShellFolder(valueName: String): String? = runCatching {
            val command = listOf(
                "reg.exe",
                "query",
                "HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\User Shell Folders",
                "/v",
                valueName
            )
            val process = ProcessBuilder(command)
                .redirectErrorStream(true)
                .start()
            val output = process.inputStream
                .readAllBytes()
                .toString(StandardCharsets.UTF_8)
            val exitCode = process.waitFor()
            if (exitCode != 0) {
                return@runCatching null
            }
            parseRegQueryValue(output = output, valueName = valueName)
        }.getOrNull()

        /**
         * Parses output similar to:
         *     Personal    REG_EXPAND_SZ    D:\Documents
         * or:
         *     Personal    REG_EXPAND_SZ    %USERPROFILE%\Documents
         */
        private fun parseRegQueryValue(output: String, valueName: String): String? {
            val line = output
                .lineSequence()
                .firstOrNull { it.trim().startsWith(prefix = valueName, ignoreCase = false) }
                ?: return null
            val match = "^\\s*${Regex.escape(valueName)}\\s+\\S+\\s+(.+?)\\s*$"
                .toRegex()
                .find(line)
                ?: return null
            return expandWindowsEnvironmentVariables(value = match.groupValues[1])
        }

        /**
         * Expands Windows-style environment variables:
         *     %USERPROFILE%
         *     %LOCALAPPDATA%
         *     %USERNAME%
         */
        private fun expandWindowsEnvironmentVariables(value: String): String {
            return "%([^%]+)%".toRegex()
                .replace(value) { match ->
                    System.getenv(match.groupValues[1])
                        ?: match.value
                }
        }
    }

    private class MacOSProvider : Provider {

        private val userHome: File by lazy {
            File(System.getProperty("user.home"))
        }

        override fun getConfigDir(appName: String?): File {
            return if (appName == null) {
                userHome.resolve("Library/Preferences")
            } else {
                userHome.resolve("Library/Preferences/$appName")
            }
        }

        override fun getDataDir(appName: String?): File {
            return if (appName == null) {
                userHome.resolve("Library/Application Support")
            } else {
                userHome.resolve("Library/Application Support/$appName")
            }
        }

        override fun getCacheDir(appName: String?): File {
            return if (appName == null) {
                userHome.resolve("Library/Caches")
            } else {
                userHome.resolve("Library/Caches/$appName")
            }
        }

        override fun getDesktopDir(appName: String?): File {
            val desktopDir = userHome.resolve("Desktop")
            return if (appName == null) {
                desktopDir
            } else {
                desktopDir.resolve(appName)
            }
        }

        override fun getDocumentsDir(appName: String?): File {
            val documentsDir = userHome.resolve("Documents")
            return if (appName == null) {
                documentsDir
            } else {
                documentsDir.resolve(appName)
            }
        }

        override fun getDownloadsDir(appName: String?): File {
            val downloadsDir = userHome.resolve("Downloads")
            return if (appName == null) {
                downloadsDir
            } else {
                downloadsDir.resolve(appName)
            }
        }

        override fun getPicturesDir(appName: String?): File {
            val picturesDir = userHome.resolve("Pictures")
            return if (appName == null) {
                picturesDir
            } else {
                picturesDir.resolve(appName)
            }
        }

        override fun getMusicDir(appName: String?): File {
            val musicDir = userHome.resolve("Music")
            return if (appName == null) {
                musicDir
            } else {
                musicDir.resolve(appName)
            }
        }

        override fun getVideosDir(appName: String?): File {
            val videosDir = userHome.resolve("Movies")
            return if (appName == null) {
                videosDir
            } else {
                videosDir.resolve(appName)
            }
        }
    }

    private class UnixProvider : Provider {

        private val userHome: File by lazy {
            File(System.getProperty("user.home"))
        }

        private fun linuxUserDirectory(
            environmentVariable: String,
            defaultDirectory: () -> File
        ): File {
            return System.getenv(environmentVariable)
                ?.takeIf { it.isNotBlank() }
                ?.let(::File)
                ?: defaultDirectory()
        }

        override fun getConfigDir(appName: String?): File {
            val linuxUserConfigHome: File = linuxUserDirectory(
                environmentVariable = "XDG_CONFIG_HOME",
                defaultDirectory = { userHome.resolve(".config") }
            )
            return if (appName == null) {
                linuxUserConfigHome
            } else {
                linuxUserConfigHome.resolve(appName)
            }
        }

        override fun getDataDir(appName: String?): File {
            val linuxUserDataHome: File = linuxUserDirectory(
                environmentVariable = "XDG_DATA_HOME",
                defaultDirectory = { userHome.resolve(".local/share") }
            )
            return if (appName == null) {
                linuxUserDataHome
            } else {
                linuxUserDataHome.resolve(appName)
            }
        }

        override fun getCacheDir(appName: String?): File {
            val linuxUserCacheHome: File = linuxUserDirectory(
                environmentVariable = "XDG_CACHE_HOME",
                defaultDirectory = { userHome.resolve(".cache") }
            )
            return if (appName == null) {
                linuxUserCacheHome
            } else {
                linuxUserCacheHome.resolve(appName)
            }
        }

        override fun getDesktopDir(appName: String?): File {
            val desktopDir =
                linuxUserDir(variableName = "XDG_DESKTOP_DIR", defaultDirName = "Desktop")
            return if (appName == null) {
                desktopDir
            } else {
                desktopDir.resolve(appName)
            }
        }

        override fun getDocumentsDir(appName: String?): File {
            val documentsDir =
                linuxUserDir(variableName = "XDG_DOCUMENTS_DIR", defaultDirName = "Documents")
            return if (appName == null) {
                documentsDir
            } else {
                documentsDir.resolve(appName)
            }
        }

        override fun getDownloadsDir(appName: String?): File {
            val downloadsDir =
                linuxUserDir(variableName = "XDG_DOWNLOAD_DIR", defaultDirName = "Downloads")
            return if (appName == null) {
                downloadsDir
            } else {
                downloadsDir.resolve(appName)
            }
        }

        override fun getPicturesDir(appName: String?): File {
            val picturesDir =
                linuxUserDir(variableName = "XDG_PICTURES_DIR", defaultDirName = "Pictures")
            return if (appName == null) {
                picturesDir
            } else {
                picturesDir.resolve(appName)
            }
        }

        override fun getMusicDir(appName: String?): File {
            val musicDir = linuxUserDir(variableName = "XDG_MUSIC_DIR", defaultDirName = "Music")
            return if (appName == null) {
                musicDir
            } else {
                musicDir.resolve(appName)
            }
        }

        override fun getVideosDir(appName: String?): File {
            val videosDir = linuxUserDir(variableName = "XDG_VIDEOS_DIR", defaultDirName = "Videos")
            return if (appName == null) {
                videosDir
            } else {
                videosDir.resolve(appName)
            }
        }

        /**
         * Linux user directories are defined by:
         *
         *   ~/.config/user-dirs.dirs
         *
         * Example:
         *   XDG_DOCUMENTS_DIR="$HOME/Documents"
         *   XDG_PICTURES_DIR="$HOME/Pictures"
         *
         * The parser supports:
         *   "$HOME/xxx"
         *   "$HOME/xxx/yyy"
         *   absolute paths
         *
         * If the configuration file or entry does not exist,
         * the supplied default directory is used.
         */
        private fun linuxUserDir(variableName: String, defaultDirName: String): File {
            val linuxUserConfigHome: File = linuxUserDirectory(
                environmentVariable = "XDG_CONFIG_HOME",
                defaultDirectory = { userHome.resolve(".config") }
            )
            val configFile = File(linuxUserConfigHome, "user-dirs.dirs")
            if (configFile.isFile) {
                val value = configFile.useLines { lines ->
                    lines
                        .map { it.trim() }
                        .firstNotNullOfOrNull { line ->
                            parseUserDirLine(line = line, variableName = variableName)
                        }
                }
                if (value != null) {
                    return File(value)
                }
            }
            return File(userHome, defaultDirName)
        }

        private fun parseUserDirLine(line: String, variableName: String): String? {
            val prefix = "$variableName="
            if (!line.startsWith(prefix)) {
                return null
            }
            var value = line
                .substring(prefix.length)
                .trim()
            if (value.length >= 2 &&
                value.first() == '"' &&
                value.last() == '"'
            ) {
                value = value.substring(startIndex = 1, endIndex = value.length - 1)
            }
            value = value.replace(oldValue = "\$HOME", newValue = userHome.absolutePath)
            return value.takeIf { it.isNotBlank() }
        }
    }
}
