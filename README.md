# gank
原本打算将数据存储到mysql，然后通过elasticsearch-jdbc每分钟定时同步到es的，但苦于云服务器不过才1G小内存，根本不够扛，于是取消了mysql，不过同步用的脚本mysql_import_es.sh还在</br>
订阅流程：</br>
![基本架构图](http://cmtimeoss.oss-cn-shanghai.aliyuncs.com/20170716165901.png)
