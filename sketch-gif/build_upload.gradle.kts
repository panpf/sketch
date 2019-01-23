//import com.android.build.gradle.internal.publishing.AndroidArtifacts
//import java.util.*
//import guru.stefma.bintrayrelease.PublishExtension
//
//Properties().apply { project.file("local.properties").takeIf { it.exists() }?.inputStream()?.use { load(it) } }.takeIf { !it.isEmpty }?.let { localProperties ->
////    apply { plugin("guru.stefma.bintrayrelease") }
//
////    version = android.defaultConfig.versionName
//    version = property("VERSION_NAME").toString()
//    group = "me.panpf"
//    configure<PublishExtension> {
//        artifactId = "sketch-gif"
//        desc = "Android, Image, Load, GIF"
//        website = "https://github.com/panpf/sketch"
//        userOrg = localProperties.getProperty("bintray.userOrg")
//        bintrayUser = localProperties.getProperty("bintray.user")
//        bintrayKey = localProperties.getProperty("bintray.apikey")
//    }
//}