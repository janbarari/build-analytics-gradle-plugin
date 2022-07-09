package io.github.janbarari.gradle.analytics.metric.modulesmethodcount.create

import io.github.janbarari.gradle.analytics.domain.model.ModuleMethodCount
import io.github.janbarari.gradle.analytics.domain.model.ModulePath
import io.github.janbarari.gradle.analytics.domain.model.ModulesMethodCountMetric
import io.github.janbarari.gradle.core.UseCase
import io.github.janbarari.gradle.extension.whenEach
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Collectors
import kotlin.io.path.Path
import kotlin.io.path.extension
import kotlin.io.path.pathString

class CreateModulesMethodCountMetricUseCase : UseCase<List<ModulePath>, ModulesMethodCountMetric>() {

    private val commentRegex = """(//.*)|(\/\*[^/*]*(?:(?!\/\*|\*\/)[/*][^/*]*)*\*\/)""".toRegex()

    private val javaModifiers = "public|private|protected|static|final|native|synchronized|abstract|transient|"
    private val javaMethodRegex =
        """($javaModifiers)+[\w\<\>\[\]\,\s]*\s*(\w+) *\([^\)]*\) *(\{|[^;])""".toRegex()

    private val kotlinMethodRegex = """((fun)+[\\${'$'}\w\<\>\w\s\[\]]*\s+\w.*\([^\)]*\) *(.*) *(\{|\=))""".toRegex()
    private val kotlinConstructorRegex =
        """(.*class[\\${'$'}\w\<\>\w\s\[\]]*\s+\w.*\([^\)]*\)|.*constructor.*\([^\)]*\))|((\sinit) *(\{|\=))"""
            .toRegex()

    override suspend fun execute(input: List<ModulePath>): ModulesMethodCountMetric {
        val modulesProperties = mutableListOf<ModuleMethodCount>()
        withContext(dispatcher) {
            val defers = mutableListOf<Deferred<Boolean>>()
            input.whenEach {
                defers.add(async {
                    modulesProperties.add(
                        ModuleMethodCount(
                            path = path,
                            value = getModuleMethodCount(absoluteDir)
                        )
                    )
                })
            }
            defers.forEach { it.await() }
        }
        return ModulesMethodCountMetric(modules = modulesProperties)
    }

    private fun getModuleMethodCount(directory: String): Int {
        var sourcePaths: List<Path>
        Files.walk(Path(directory)).use { stream ->
            sourcePaths =
                stream.map { obj: Path -> obj.normalize() }.filter(Files::isRegularFile).filter { isSourcePath(it) }
                    .collect(Collectors.toList())
        }

        var result = 0
        sourcePaths.whenEach {
            val content = toFile().inputStream().bufferedReader().use { it.readText() }
            val removedComments = content.replace(commentRegex, "")
            if (extension == "kt") {
                result += kotlinMethodRegex.findAll(removedComments).count()
                result += kotlinConstructorRegex.findAll(removedComments).count()
            }
            if (extension == "java") {
                result += javaMethodRegex.findAll(removedComments).count()
            }
        }

        return result
    }

    private fun isSourcePath(path: Path): Boolean {
        return (path.pathString.contains("src/main/java") || path.pathString.contains("src/main/kotlin")) &&
                (path.extension == "kt" || path.extension == "java")
    }

}
