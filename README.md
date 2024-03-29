# ♟minzuchess-spring-boot♟
民族棋类网--Spring Boot版   #久棋平台

---
### demo请见【已停用】👉🏻:[www.eanson.work](http://www.eanson.work "www.eanson.work")
公共棋盘演示:[Demo](http://www.eanson.work/chess?n_t=1586511476075&ex=f96d4f00b2b34a2f86bca3f896ace743&ex2=f0d885aa015a46708898e61f6d632079&code=Demo)

具体游戏规则:见[首页](http://www.eanson.work)右侧超链接

**[码云仓库](https://gitee.com/eanson/minzuchess-spring-boot)**
## 项目描述
该项目提供了在线对弈平台和AI分析处理接口,实现了AI和人类招法分析、在线对弈和在线观看对弈、对局记录和回放等功能。
## 项目亮点
为了减轻服务端轮询访问改用WebSocket协议实现棋盘数据的及时传输🚀,采用Redis来缓存人类对弈时的步伐并用作之后的招法分析...
## 项目意义
有利于我国少数民族棋类活动的推广,也让AI设计者减轻一小部分负担,让开发AI的同学只关注AI本身设计，而不用为开发UI界面发愁,平台不仅提供裁判程序,而且还提供了招法记录服务,可以让AI设计者更快更好地分析AI行为。同时这也是我学习Java语言及其生态以来的第一个较完整的作品

## 要点
1. ```application.yml```文件中具体配置信息请自行配置
2. sql文件在在`resources`文件夹下,示例号用户名:`20201100`,密码:`123456`
3. 采用shiro-redis来共享session,支持负载均衡
4. 项目尚存许多设计不合理的地方,改动起来较为麻烦。
5. 项目重构后部分小bug未更改
6. 如需部署到云端,`nginx`配置文件在`resources`文件夹下
7. 项目需要`redis`支持
