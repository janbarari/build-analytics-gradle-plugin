# Setup
To set up the plugin in your project, follow the below instructions:

### <strong>Step 1</strong>
Make sure your project uses Git VCS and Gradle version `6.1+`.
!!! Note ""

    Gradle Analytics Plugin uses Git terminal to get the branch names and latest HEAD commit hash. 
    so It is required for your project to use Git VCS.

<br/><br/>

### <strong>Step 2</strong>
Apply the Gradle Plugin to the root of your project.
=== "Kotlin"
    ``` kotlin
    plugins {
        id("io.github.janbarari.gradle-analytics-plugin") version "1.0.0-beta3"
    }
    ```
=== "Groovy"
    ``` groovy
    plugins {
        id 'io.github.janbarari.gradle-analytics-plugin' version '1.0.0-beta3'
    }
    ```
[For legacy plugin application, see the Gradle Plugin Portal.](https://plugins.gradle.org/plugin/io.github.janbarari.gradle-analytics-plugin)
<br/><br/>

### <strong>Step 3</strong> 
Add plugin configuration in the root of your project.

=== "Kotlin"
    ``` kotlin
    gradleAnalyticsPlugin {
        database {
            local = sqlite {
                path = "DATABASE_PATH"
                name = "DATABASE_NAME" // Don't add `.db` in the database name.
                user = "DATABASE_USER" // Remove `user` if you want the plugin to create the DB.
                password = "DATABASE_PASSWORD" // Remove `password` if you want the plugin to create the DB.
            }
            ci = mysql {
                host = "MYSQL_DATABASE_HOST"
                name = "MYSQL_DATABASE_NAME"
                user = "MYSQL_DATABASE_USER"
                password = "MYSQL_DATABASE_PASSWORD"
                port = MYSQL_DATABASE_PORT // Default is 3306
            }
        }
    
        trackingTasks = listOf(
            // Add your requested tasks to be analyzed, Example:
            ":app:assembleDebug"
        )
    
        trackingBranches = listOf(
            // requested tasks only analyzed in the branches you add here, Example:
            "master",
            "develop"
        )
    
        outputPath = "OUTPUT_REPORT_PATH"
    }
    ```
=== "Groovy"
    ``` groovy
    gradleAnalyticsPlugin {
        database {
            local = sqlite {
                path = 'DATABASE_PATH'
                name = 'DATABASE_NAME' // Don't add `.db` in the database name.
                user = 'DATABASE_USER' // Remove `user` if you want the plugin to create the DB.
                password = 'DATABASE_PASSWORD' // Remove `password` if you want the plugin to create the DB.
            }
            ci = mysql {
                host = 'MYSQL_DATABASE_HOST'
                name = 'MYSQL_DATABASE_NAME'
                user = 'MYSQL_DATABASE_USER'
                password = 'MYSQL_DATABASE_PASSWORD'
                port = MYSQL_DATABASE_PORT // Default is 3306
            }
        }
    
        trackingTasks = [
            // Add your requested tasks to be analyzed, Example:
            ':app:assembleDebug'
        ]
    
        trackingBranches = [
            // requested tasks only analyzed in the branches you add here, Example:
            'master',
            'develop'
        ]
    
        outputPath = 'OUTPUT_REPORT_PATH'
    }
    ```
<br/>
<strong>Important Notes</strong><br/>

- The plugin will create one automatically if there isn't an SQLite database. You only need to fill in the `name` and `path`.
- Both `sqlite / mysql` can be used to config `local` or `ci` databases.
- The `local` or `ci` database can be skipped if don't need analytics on each of them.
- If using the plugin in your CI/CD make sure the `CI=true` environment variable exists in your CI system environments and the `ci` database is configured.
- The `outputPath` can be skipped, it will generate the report inside the project build directory.

<br/>
