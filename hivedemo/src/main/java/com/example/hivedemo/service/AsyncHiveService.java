package com.example.hivedemo.service;

import com.example.hivedemo.Utils.ScanDataUtil;
import com.example.hivedemo.syncEvent.SerchHiveParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@Slf4j
@Service
public class AsyncHiveService {

    @Autowired
    DataSource druidDataSource;

    public void asyncDoSerchJob(SerchHiveParams source) throws SQLException, IOException {
        Statement statement = druidDataSource.getConnection().createStatement();

        ResultSet res = statement.executeQuery(source.getSql());
        int count = res.getMetaData().getColumnCount();
        String str = null;
        File file = new File("D:/" + source.getPath());
        if (!file.exists()) {
            file.createNewFile();
        }
        BufferedWriter bw = new BufferedWriter(new FileWriter(file));

        while (res.next()) {
            str = "";
            for (int i = 1; i < count; i++) {
                str += res.getString(i) + "\t";
            }
            str += res.getString(count);
            ScanDataUtil.scanColumnWords(str);
            bw.write(str);
            bw.newLine();
            bw.flush();
        }
        bw.close();
    }


}
