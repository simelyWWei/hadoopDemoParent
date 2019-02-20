package com.example.hivedemo.controllor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
public class HiveTestControllor {

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


}
