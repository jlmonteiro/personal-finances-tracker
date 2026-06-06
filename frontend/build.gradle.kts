import com.github.gradle.node.npm.task.NpmTask

plugins {
    alias(libs.plugins.node.gradle)
}

node {
    version = "20.18.0"
    download = true
    nodeProjectDir = projectDir
}

val npmBuild = tasks.register<NpmTask>("npmBuild") {
    group = "frontend"
    description = "Build the frontend production bundle"
    dependsOn(tasks.npmInstall)
    args = listOf("run", "build")

    inputs.dir(file("src"))
    inputs.file(file("package.json"))
    outputs.dir(file("dist"))
}

tasks.register<NpmTask>("npmTest") {
    group = "frontend"
    description = "Run frontend unit tests"
    dependsOn(tasks.npmInstall)
    args = listOf("run", "test", "--", "--run")
}

tasks.register<NpmTask>("npmLint") {
    group = "frontend"
    description = "Run frontend linter"
    dependsOn(tasks.npmInstall)
    args = listOf("run", "lint")
}

tasks.register("build") {
    group = "frontend"
    description = "Build the frontend (alias for npmBuild)"
    dependsOn(npmBuild)
}
