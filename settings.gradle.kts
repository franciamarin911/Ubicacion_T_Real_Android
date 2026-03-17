pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://api.mapbox.com/downloads/v2/releases/maven")
            credentials {
                username = "mapbox"
                //TOKEN SECRETO DE MAPBOX
                password = "sk.eyJ1IjoiZnJhbmNpYW1hcmluOTExIiwiYSI6ImNtbTVjbG9ueDA1YWIyd3EzMzkwYWh6MGkifQ.n7-Nv8sklwePlzLXxNLU9w"
            }
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
}

rootProject.name = "Proyecto_Ubi_tiempo_Real_FAMM"
include(":app")
 