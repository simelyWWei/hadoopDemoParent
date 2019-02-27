package com.example.hivedemo.controllor;

import com.example.hivedemo.syncEvent.SerchHiveEvent;
import com.example.hivedemo.syncEvent.SerchHiveParams;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
public class HiveTestControllor {

    @Autowired
    WebApplicationContext wac;

    @Autowired
    DataSource druidDataSource;



    @RequestMapping(value = "/listtable",method = RequestMethod.GET)
    public List<String> testHiveCon(){
        List<String> list = new ArrayList<>();
        String sql = "show tables";
        Statement statement;
        ResultSet resultSet;
        try {
            statement = druidDataSource.getConnection().createStatement();
            resultSet = statement.executeQuery(sql);
            while (resultSet.next()){
                list.add(resultSet.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 查询Hive库中的某张数据表字段信息
     */
    @RequestMapping("/table/describe")
    public List<String> describeTable(String tableName) throws SQLException {
        List<String> list = new ArrayList<String>();
        Statement statement = druidDataSource.getConnection().createStatement();
        String sql = "describe " + tableName;
        log.info("Running: " + sql);
        ResultSet res = statement.executeQuery(sql);
        while (res.next()) {
            list.add(res.getString(1));
        }
        return list;
    }

    /**
     * 查询指定tableName表中的数据
     */
    @RequestMapping("/table/select")
    public List<String> selectFromTable(String tableName) throws SQLException {
        Statement statement = druidDataSource.getConnection().createStatement();
        String sql = "select * from " + tableName;
        log.info("Running: " + sql);
        ResultSet res = statement.executeQuery(sql);
        List<String> list = new ArrayList<>();
        int count = res.getMetaData().getColumnCount();
        String str = null;
        while (res.next()) {
            str = "";
            for (int i = 1; i < count; i++) {
                str += res.getString(i) + " ";
            }
            str += res.getString(count);
            log.info(str);
            list.add(str);
        }
        return list;
    }

    /**
     * 查询指定tableName指定字段
     */
    @RequestMapping("/table/selectColumn")
    public List<String> selectColumnFromTable(String tableName,String cloumns) throws SQLException {
        String[] split = cloumns.split(",");
        Statement statement = druidDataSource.getConnection().createStatement();
        StringBuilder sb = new StringBuilder();
        sb.append("select").append(" "+cloumns);
        sb.append(" from ").append(tableName);
        String sql = sb.toString();
        log.info("Running: " + sql);
        ResultSet res = statement.executeQuery(sql);
        List<String> list = new ArrayList<>();
        int count = res.getMetaData().getColumnCount();
        String str = null;
        while (res.next()) {
            str = "";
            for (int i = 1; i < count; i++) {
                str += res.getString(i) + " ";
            }
            str += res.getString(count);
            log.info(str);
            list.add(str);
        }
        return list;
    }

    /**
     * 查询指定tableName指定字段--异步方式，并写入文件中
     */
    @RequestMapping("/table/selectColumn2")
    public String selectColumnFromTable2(String tableName,String cloumns,String path) {
        String[] split = cloumns.split(",");

        StringBuilder sb = new StringBuilder();
        sb.append("select").append(" "+cloumns);
        sb.append(" from ").append(tableName);
        String sql = sb.toString();
        log.info("Running: " + sql);

        SerchHiveParams serchHiveParams = new SerchHiveParams();
        serchHiveParams.setSql(sql);
        serchHiveParams.setPath(path);
        serchHiveParams.setTableName(tableName);
        serchHiveParams.setColumns(cloumns);

        wac.publishEvent(new SerchHiveEvent(serchHiveParams));
        return "doing";
    }


}
