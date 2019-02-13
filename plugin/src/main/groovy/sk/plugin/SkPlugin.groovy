package sk.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * @author com.sky
 * @version 1.0 on 2017-11-05 上午11:58
 * @see SkPlugin
 */
class SkPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        String taskNames = project.gradle.startParameter.taskNames.toString()
        System.out.println("taskNames is " + taskNames)
        String module = project.path.replace(":", "")
        System.out.println("current module is " + module)

        String[] moduleSplit = module.split("_")
        int moduleLength = moduleSplit.length
        if (moduleLength < 2) {
            throw new RuntimeException("you module name is not module_*** and cpt_***  not like this")
        }

        String resourcePrefixS = moduleSplit[moduleSplit.length - 1] + "_"
        boolean isCPT = moduleSplit[0].equals("cpt") || moduleSplit[0].equals("CPT")
        System.out.println("resourcePrefixS is " + resourcePrefixS)

        if (!project.hasProperty("isRunAlone")) {
            throw new RuntimeException("you should set isRunAlone in " + module + "'s gradle.properties")
        }

        if (!project.hasProperty("skVersion")) {
            throw new RuntimeException("you should set skVersion in " + module + "'s gradle.properties")
        }

        boolean isRunAlone = Boolean.valueOf(project.properties.get("isRunAlone"))
        String skVersion = String.valueOf(project.properties.get("skVersion"))
        System.out.println("isRunAlone 参数是:" + isRunAlone)
        System.out.println("skVersion 参数是:" + skVersion)
        System.out.println("isCPT 参数是:" + isCPT)

        if (isRunAlone) {
            project.apply plugin: 'com.android.application'
            System.out.println("apply plugin is " + 'com.android.application')

            project.android.sourceSets {
                main {
                    manifest.srcFile 'src/main/module/AndroidManifest.xml'
                }
            }
            if (!isCPT) {
                project.android.resourcePrefix resourcePrefixS
            }
        } else {
            project.apply plugin: 'com.android.library'
            System.out.println("apply plugin is " + 'com.android.library')

            project.android.sourceSets {
                main {
                    manifest.srcFile 'src/main/AndroidManifest.xml'
                    //集成开发模式下排除debug文件夹中的所有Java文件
                    java {
                        exclude 'debug/**'
                    }
                }
            }
            if (!isCPT) {
                project.android.resourcePrefix resourcePrefixS
            }
        }

        project.android.defaultConfig.javaCompileOptions.annotationProcessorOptions {
            arguments = [SK_MODULE_NAME: module]
        }

        project.dependencies {
            // sk
            api "com.jincanshen:sk:$skVersion"
            // sk注解解析
            annotationProcessor "com.jincanshen:sk-compiler:$skVersion"
        }

    }

}
