pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://www.jitpack.io" ) }

        val props = java.util.Properties().apply {
            load(java.io.FileInputStream(File(rootProject.projectDir, "github.properties")))
        }
        val githubUserId: String? = props.getProperty("gpr.usr")
        val githubApiKey:String? = props.getProperty("gpr.key")

        repositories {
            maven(url = uri("https://maven.pkg.github.com/Cuberto/liquid-swipe-android")) {
                name = "GitHubPackages"
                credentials {
                    username = githubUserId ?: System.getenv("GPR_USER")
                    password = githubApiKey ?: System.getenv("GPR_API_KEY")
                }
            }
        }
    }
}

rootProject.name = "SHE"
include(":app")
 