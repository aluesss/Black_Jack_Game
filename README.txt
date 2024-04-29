登陆界面重点测试的功能：密码加密提高安全性、邮箱/用户名占用、忘记密码、注销用户
欢迎界面的特色功能：每日签到奖励领取，每个用户每天只能领一次
欢迎界面重点测试的功能：退出登录按钮可以返回登录界面，可以重新登录，便于切换账号
已添加等级机制：统计赢得的score总量来升级
Signup UI testing: Password encrypted, email/username occupied, forget the password, delete account.
Welcome UI feature: Daily rewards, each user can only claim once per day.
Welcome UI function: log out button to go back login UI.
Level added: use the score winned to level up.

修改bug日志：
1. （新）密码、用户名、邮箱 都可以为空，要修改这个bug   --已修改
2.   修改密码界面点击取消或×后程序异常退出                         --已修改
Bug fix log:
1. Password, username, email can be empty.   --Fixed
2.The program exits with error after clicking Cancel or × on the password modification.

数据库密码：1768
Database password: 1768

查看数据库中信息：
Check the info in database:
USE pokerdb;
SELECT * FROM users;

删除数据库，重新建立：
Delete the database to rebuild.
DROP DATABASE pokerdb;

运行方式：
1.运行PokerServer.java
2.运行LoginUI.java
3.进入游戏后，点击左上角File中的Join the Game以连接服务器
4.在bet按钮旁边的输入框中输入赌注，点击bet开始游戏。通过hit叫牌，stop停牌
How to run:
1.Run the PokerServer.java
2.Run the LoginUI.java
3.After start the game, click Join the Game in the upper-left File to connect to the server
4.Type in the bet in the input next to the bet button, click bet to start the game, use hit to call more card, use stop to stop calling the card.

