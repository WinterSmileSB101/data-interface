# data-interface
数据源接口
---
> 之前更新了一个python爬虫的比价系统,然后最近抽找时间写了一个java版的,并且提供接口给大家调用,初衷是方便大家做一些项目可以加上一个小功能,所以请不要商业用途,提供出来的接口需要不可以连续频繁访问,设置了5秒限制,希望大家遵守游戏规则,如果确实需要频繁一点获取?1.代码托管到[jithub](https://github.com/18312097143/data-interface),可以clone,自行下载提取主要功能模块整合,2.联系我针对个别 ip or user 授权访问。

* 说明：
  这次抓取的不是慢慢买的比价信息，是比比鲸的数据，为什么换了呢?实在没想到慢慢买的工程师如此简单粗暴，如果同一个IP短时间获取稍微过多,竟然封IP(20分钟左右吧,高效的是电脑浏览器都访问不了,这`反爬`我还真是第一见),这也是我不能忍的,想破也简单,搭一个IP池即可,之前玩python曾经搭一个IP池,但是现在java方面还没有时间同步,等有时间了写一个IP池接口那以后就方便了!

* 项目坐标:
https://github.com/18312097143/data-interface

* 声明:
 数据来源于比比鲸,请不要商业用途。

* 接口
  ex:http://47.107.101.121:8081/data-interface/bijia?key=荣耀10&page=1
  说明:
  * key 必要参数
  * page 可选参数,default=1
    
    正常调用:
      ![成功调用](https://files-shaines-1258193137.cos.ap-guangzhou.myqcloud.com/java-data-interface.jpg)
    频繁调用: ![在这里插入图片描述](https://files-shaines-1258193137.cos.ap-guangzhou.myqcloud.com/java-data-interface2.jpg)
   
#####  讨论:
* 博客同步到 [SHY BLOG](https:/shaines.cn)
