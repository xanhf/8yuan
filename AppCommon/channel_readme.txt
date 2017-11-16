批量生成渠道包(360加固)
1  生成签名包
2  利用360加固自动签名
3  多渠道打包
4  对多渠道包进行签名
5  重命名qihuo_9_xxx_sign.apk为qihuo_xxx.apk 使用：直接将chFileName.py放入apk所在文件夹，然后命令行python chFileName.py即可


(利用walle打包)
下载jar包  walle-cli-all.jar
https://github.com/Meituan-Dianping/walle
阅读第二种方式
命令行工具使用方式
https://github.com/Meituan-Dianping/walle/blob/master/walle-cli/README.md

1、生成签名包， 得到fxbtg.apk
2、cd进入 walle-cli-all.jar 所在目录，使用命令行

java -jar walle-cli-all.jar batch -f /Users/adu/AndroidStudioProjects/FXBTG/App_FXBTG/FXBTG/AppCommon/channel /Users/adu/appOut/wall/eight.apk /Users/adu/appOut/wall/v4.2.0_46

batch -f 渠道文件(channel 在工程目录中，不需要的渠道可#注视掉)  签名apk文件  输出文件夹
