package com.qizuang.plugin;

/**
 * Author: stc
 * Date:   2020/5/28
 * Desc:   ❤-- 以下为自动化插件动态传递的参数，如有修改，仿之即可 --❤
 **/
class Extension {

    /**
     * 360加固自动化账号
     */
    def ACCOUNT_360

    /**
     * 360加固自动化密码
     */
    def PASSWORD_360

    /**
     * chanel渠道包生成文件夹命名为当前版本
     */
    def CHANEL_VERSION_NAME

    /**
     * 由于签名位置信息 不同项目前台难以保持一致 估不写死，做成可动态传递
     */
    def KEY_FILE
    def KEY_MOBILE_ALIAS
    def KEY_PASSWORD
}
