package com.example.hivedemo.controllor;

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
}
