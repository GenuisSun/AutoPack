package com.qizuang.plugin

import com.android.build.gradle.api.BaseVariant
import org.gradle.api.Plugin
import org.gradle.api.Project

class PackagingForEase implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.extensions.create("packagingConfig", Extension)
        //创建自动化插件执行 打包任务
        project.android.applicationVariants.all { BaseVariant variant ->
            if (variant.buildType.name.contains('release') && !variant.buildType.name.contains('prerelease')) {
                def variantName = variant.name.capitalize()
                PackagingProcesser task = project.tasks.create("assemble${variantName}Packaging", PackagingProcesser)
                task.targetProject = project
                task.variant = variant

                // 依赖 assemble, 需要先编译出包
                task.dependsOn variant.assemble
            }
        }
    }
}