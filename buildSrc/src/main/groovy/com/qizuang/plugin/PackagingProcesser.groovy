package com.qizuang.plugin

import com.android.build.gradle.api.BaseVariant
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

/**
 * Author: stc
 * Date:   2020/5/28
 * Desc:   ❤-- 自动化加固 生成渠道包任务 --❤
 *         ❤-- 一键自动化打包方法：
 *         ❤-- 脚本在项目根目录的 auth_packaging 文件夹中，
 *         ❤-- 只需到此文件夹中右键auth_packaging.sh文件，
 *         ❤-- 点击 Run auth_packaging.sh 即可在Terminal中查看执行过程，等待最后渠道包生成。
 *         ❤-- 或者 直接在使用系统命令执行脚本文件，操作流程一样
 *         ❤-- 流程完毕之后渠道包在项目根目录的/auth_packaging/chanel/文件夹 如果未显示 请刷新一下即可
 **/
class PackagingProcesser extends DefaultTask {

    //-------------------TODO start==>>自动化打包相关路径设置 目前相关路径写死---------------------------
    //-------------------TODO 特别说明：如无特殊需求 尽量不要改动此块，改动后请在ReadMe.md文件中加以说明--
    //-------------------TODO {Base}/{项目名}  已做处理，无需再进行设置，默认为项目目录下的 auth_packaging 文件夹中--
    //-------------------TODO 相关路径的文件夹后必须要加 “/”  请勿删除，否则会找不到路径--
    /**
     * 本地 加固jar资源文件存放路径===>>{Base}/{项目名}/auth_packaging/jiagu/
     */
    def JIAGU_ROOT_PATH = "./_360jiagu/jiagu/jiagu.jar"
//    def JIAGU_ROOT_PATH_WIN = "./_360jiagu/jiagu_win/jiagu.jar"
//    def JIAGU_ROOT_PATH_MAC = "./_360jiagu/jiagu_mac/jiagu.jar"
    /**
     * 加固完成后的apk文件存放路径===>>{Base}/{项目名}/auth_packaging/jiagu_complete/
     */
    def JIAGU_COMPLETE_PATH = "./buildSrc/auth_packaging/jiagu_complete/"
    /**
     * 加固完成后生成渠道jar资源文件存放路径---mac和win用的都是同一个打渠道jar文件===>>{Base}/{项目名}/auth_packaging/jiagu/walle-cli-all.jar"
     */
    def CHANEL_JAR_PATH = "./_360jiagu/walle/walle-cli-all.jar"
    /**
     * 最终生成的渠道包apk资源文件存放路径===>>{Base}/{项目名}/auth_packaging/chanel/"
     */
    def OUTPUT_APK_CHANNEL_PATH = "./buildSrc/auth_packaging/chanel/"
    /**
     * 导入相关渠道信息文件位置
     */
    def CHANNEL_FILE_PATH = "./buildSrc/auth_packaging/channel"

    //-------------------TODO end==>>自动化打包相关路径设置------------------------------------



    //-------------------TODO start==>>以下相关动态设置的变量---------------------------

    /**
     * 360相关账号
     */
    def ACCOUNT_360
    def PASSWORD_360
    /**
     * chanel渠道包生成文件夹命名为当前版本
     */
    def CHANEL_VERSION_NAME
    /**
     * 由于签名位置信息 前台不同项目难以保持一致 估不写死，做成可动态传递
     */
    def KEY_FILE
    def KEY_MOBILE_ALIAS
    def KEY_PASSWORD

    //-------------------TODO end==>>以下相关动态设置的变量---------------------------

    @Input
    public Project targetProject

    @Input
    public BaseVariant variant

    @TaskAction
    void jiagu() {

        ACCOUNT_360 = targetProject.packagingConfig.ACCOUNT_360
        PASSWORD_360 = targetProject.packagingConfig.PASSWORD_360
        CHANEL_VERSION_NAME = targetProject.packagingConfig.CHANEL_VERSION_NAME
        KEY_FILE = targetProject.packagingConfig.KEY_FILE
        println ("🧭KEY_FILE: $KEY_FILE")
        KEY_MOBILE_ALIAS = targetProject.packagingConfig.KEY_MOBILE_ALIAS
        println ("🧭KEY_MOBILE_ALIAS: $KEY_MOBILE_ALIAS")
        KEY_PASSWORD = targetProject.packagingConfig.KEY_PASSWORD
        println ("🧭KEY_PASSWORD: $KEY_PASSWORD")
        OUTPUT_APK_CHANNEL_PATH = OUTPUT_APK_CHANNEL_PATH + CHANEL_VERSION_NAME + "/"

        //默认加固包路径
//        def defaultJigguPath = getJigguPath() //  /Users/你的用户名/apptools/360jiagubao_mac
//        if (JIAGU_ROOT_PATH == "") {
//            JIAGU_ROOT_PATH = new File(defaultJigguPath + "/jiagu/").getCanonicalPath()
//        }
//        if (JIAGU_COMPLETE_PATH == "") {
//            JIAGU_COMPLETE_PATH = new File(defaultJigguPath + "/jiagu/qz_completed/").getCanonicalPath()
//        }
//        if (OUTPUT_APK_CHANNEL_PATH == "") {
//            OUTPUT_APK_CHANNEL_PATH = new File(defaultJigguPath + "/qz_chanel/").getCanonicalPath()
//        }
//        if (CHANNEL_FILE_PATH == "") {
//            CHANNEL_FILE_PATH = new File("./app/channel").getCanonicalPath()
//        }
//
//
//        println ("🧭JIAGU_ROOT_PATH: $JIAGU_ROOT_PATH")
//        println ("🧭JIAGU_COMPLETE_PATH: $JIAGU_COMPLETE_PATH")
//        println ("🧭OUTPUT_APK_CHANNEL_PATH: $OUTPUT_APK_CHANNEL_PATH")
//        println ("🧭CHANNEL_FILE_PATH: $CHANNEL_FILE_PATH")

        def iterator = variant.outputs.iterator()
        while (iterator.hasNext()) {
            def it = iterator.next()
            def apkFile = it.outputFile
            if (apkFile == null || !apkFile.exists()) {
                throw new GradleException("${apkFile} is not existed!")
            }
            //判断当前系统
            if(isMac()){
//                JIAGU_ROOT_PATH = JIAGU_ROOT_PATH_MAC
            }else{
//                JIAGU_ROOT_PATH = JIAGU_ROOT_PATH_WIN
            }

            def out = new StringBuilder()
            def err = new StringBuilder()
            // 1.当前360版本信息
            def version = "java -jar $JIAGU_ROOT_PATH -version".execute()
            version.waitForProcessOutput()
            // 2.360加固自动化----登录
            def login = "java -jar $JIAGU_ROOT_PATH -login $ACCOUNT_360 $PASSWORD_360".execute()
            login.in.eachLine {
                line ->
                    println line
            }
            // 3.360加固自动化----导入签名文件信息
            def importsign = "java -jar $JIAGU_ROOT_PATH -importsign $KEY_FILE $KEY_PASSWORD $KEY_MOBILE_ALIAS $KEY_PASSWORD".execute()
            importsign.in.eachLine {
                line ->
                    println line
            }
            // 4.360加固自动化----查看导入签名文件信息
            def showsign = "java -jar $JIAGU_ROOT_PATH -showsign".execute()
            showsign.in.eachLine {
                line ->
                    println line
            }

            // 5.360加固自动化----配置加固可选项  X86架构 崩溃日志  加固数据分析  盗版监测
            def configx86 = "java -jar $JIAGU_ROOT_PATH -config -x86 -crashlog -analyse -piracy".execute()
            configx86.in.eachLine {
                line ->
                    println line
            }
//            def config_crashlog = "java -jar $JIAGU_ROOT_PATH -config -crashlog".execute()
//            config_crashlog.waitForProcessOutput()

            // 5.360加固自动化----由于会选取文件夹第一个文件去生成渠道包，
            // 故每次先清空加固完成目录, 删除子路径无效，所以删除整个目录
            // 再重新创建目录（测试win下会自动创建该目录，mac未测试）
            def rm = "rm -rf $JIAGU_COMPLETE_PATH".execute()
            rm.waitForProcessOutput()
            def mkdir = "mkdir $JIAGU_COMPLETE_PATH".execute()
            mkdir.waitForProcessOutput()

            def rm1 = "rm -rf $OUTPUT_APK_CHANNEL_PATH".execute()
            rm1.waitForProcessOutput()
            def mkdir1 = "mkdir $OUTPUT_APK_CHANNEL_PATH".execute()
            mkdir1.waitForProcessOutput()

            // 5.360加固自动化----加固
            def p = "java -jar $JIAGU_ROOT_PATH -jiagu $apkFile $JIAGU_COMPLETE_PATH -autosign".execute()
            p.in.eachLine {
                line ->
                    println line
            }
            // 生成完成的需加固的apk文件路径 默认此文件夹的第一个文件
            def ls = "ls $JIAGU_COMPLETE_PATH".execute()
            ls.waitForProcessOutput(out, err)
            String[] array = out.toString().split("\n")
            if (array.size() == 0) {
                return
            }
            def signFile = array[0]
            def jiaguApkPath = "$JIAGU_COMPLETE_PATH/$signFile"
            println "jiaguApk path : $jiaguApkPath"
            // 6.360加固自动化----生成渠道包
            println("java -jar $CHANEL_JAR_PATH batch -f $CHANNEL_FILE_PATH  $jiaguApkPath  $OUTPUT_APK_CHANNEL_PATH")
            def channel = "java -jar $CHANEL_JAR_PATH batch -f $CHANNEL_FILE_PATH  $jiaguApkPath  $OUTPUT_APK_CHANNEL_PATH".execute()
            channel.waitForProcessOutput(out, err)
            println "packaging completed ! "
            ls = "ls $OUTPUT_APK_CHANNEL_PATH".execute()
            ls.waitForProcessOutput(out, err)
            println("$out")
        }
    }

    // 取决于程序猿当前使用的电脑，目前适配win和mac
    /**
     * 判断当前系统是win，还是mac；如果为linux 去360加固官网下载相关文件 放置auth_packaging目录下，修改一下代码即可
     * @return
     */
    def isMac() {
        // 自动路径
//        def prefix = "/apptools/360jiagubao_"
//        def userHome = System.getProperty("user.home")
        def osName= System.getProperty("os.name").toLowerCase();
        def osPath = ""
        if (osName.indexOf("mac")>=0&&osName.indexOf("os")>0) {
            // macOS
            osPath = "mac"
            return true
        }
//        else if (osName.indexOf("linux")>=0) {
            // linux
//            osPath = "linux_64"
//            return false
//        }
        else {
            // 默认都给windows
            osPath = "windows_64"
            return false
        }

        // /home/你的用户名/apptools/360jiagubao_linux_64
//        return new File(userHome + prefix + osPath).getCanonicalPath()

        //方法二
//            def now = "uname -a".execute().text.trim()
//            println "当前系统 : $now"
//            if(now.contains("Darwin")){
//                //mac系统
//                JIAGU_ROOT_PATH = JIAGU_ROOT_PATH_MAC;
//            }else if(now.contains("MINGW")){
//                //win系统
//                JIAGU_ROOT_PATH = JIAGU_ROOT_PATH_WIN;
//            }else{
//                throw new GradleException("${now} is not existed!")
//            }

    }
}