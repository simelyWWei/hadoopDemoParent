# hadoopDemoParent
使用Springboot 中使用hadoop工具
--hive查询以及hbase与bean的整合

#--------------------------------------------------------
2019/02/20 hiveDemo
在hive中需要配置Hiveserver2组件
<property>
        <name>hive.server2.thrift.port</name>
        <value>10000</value>
</property>

<property>
        <name>hive.server2.thrift.bind.host</name>
        <value>172.16.1.128</value>
</property>

<property>
        <name>hive.server2.long.polling.timeout</name>
        <value>5000</value>
</property>

<property>
        <name>hive.server2.thrift.min.worker.threads</name>
        <value>5</value>
</property>
<property>
        <name>hive.server2.thrift.max.worker.threads</name>
        <value>500</value>
</property>

启动Hiveserver2服务
# hive --service hiveserver2 &


