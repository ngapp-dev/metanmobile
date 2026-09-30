import androidx.room.gradle.RoomExtension
import com.google.devtools.ksp.gradle.KspExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure

/** Shared Room setup for Android/iOS database modules. */
class KmpRoomConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        apply(plugin = "metanmobile.kmp.library")
        apply(plugin = "androidx.room")
        apply(plugin = "com.google.devtools.ksp")

        extensions.configure<KspExtension> {
            arg("room.generateKotlin", "true")
        }
        extensions.configure<RoomExtension> {
            schemaDirectory("$projectDir/schemas")
        }
    }
}
