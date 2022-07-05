package io.github.janbarari.gradle.utils

import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class GitUtilsTest {

    @Test
    fun `check git branch name returns correctly`() {
        val branchName = GitUtils.currentBranch()
        println("Branch: $branchName")
        assertTrue {
            branchName.isNotEmpty()
        }
    }

}