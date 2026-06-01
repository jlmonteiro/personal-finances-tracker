plugins {
    alias(libs.plugins.node.gradle)
}

node {
    version = "20.18.0"
    download = true
    nodeProjectDir = projectDir
}

val npmBuild = tasks.register<com.github.gradle.node.npm.task.NpmTask>("npmBuild") {
    dependsOn(tasks.npmInstall)
    args = listOf("run", "build")

    inputs.dir(file("src"))
    inputs.file(file("package.json"))
    outputs.dir(file("dist"))
}

tasks.register<com.github.gradle.node.npm.task.NpmTask>("npmTest") {
    dependsOn(tasks.npmInstall)
    args = listOf("run", "test", "--", "--run")
}

tasks.register<com.github.gradle.node.npm.task.NpmTask>("npmLint") {
    dependsOn(tasks.npmInstall)
    args = listOf("run", "lint")
}

tasks.register("build") {
    dependsOn(npmBuild)
}
