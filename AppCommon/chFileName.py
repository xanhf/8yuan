# -*- coding: utf-8 -*-
# 定义文档的编码
#!/usr/bin/env python
# 重命名fxbtg_9_xxx_sign.apk为fxbtg_xxx.apk
import os
import os.path
import shutil

_abspath = os.path.abspath('.')#当前文件夹
_start='/qihuo'
_judgeStr = '_'# 分割的字符串
print _abspath # 输出当前路径
_outputpath='output'#输出目的地文件夹

if os.path.exists(_abspath+'/'+_outputpath):#存在就删除文件夹
	shutil.rmtree(_abspath+'/'+_outputpath) 
#重建output
os.path.join(_abspath,_outputpath)
os.mkdir(_abspath+'/'+_outputpath)


for _file in  os.listdir(_abspath):#遍历当前文件夹
	print  _file
	if os.path.isfile(_file):#如果是文件
		sourcePathStart = os.path.splitext(_file)[0] #xxx.txt  获取xxx
		sourcePathEnd =os.path.splitext(_file)[1 ]#获取后缀
		print sourcePathStart 
		print sourcePathEnd
		if sourcePathEnd=='.apk' : # 筛选.apk文件
			if len(sourcePathStart.split('_'))>3:# 以_分割文件名前半部分 ,list大于3才往下走
				quDaoName = sourcePathStart.split('_')[2]#获取渠道名字
				shutil.copyfile(_file, _abspath+'/'+_outputpath+_file) #把文件copy到output文件夹中
				os.rename(_abspath+'/'+_outputpath+_file , _abspath+'/'+_outputpath+_start+_judgeStr+quDaoName+sourcePathEnd)#f重命名fxbtg_9_xxx_sign.apk为fxbtg_xxx.apk
			