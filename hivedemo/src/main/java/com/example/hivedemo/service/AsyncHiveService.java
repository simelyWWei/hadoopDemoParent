package com.example.hivedemo.service;

import com.example.hivedemo.Utils.ScanDataUtil;
import com.example.hivedemo.syncEvent.SerchHiveParams;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class AsyncHiveService {

    @Autowired
    DataSource druidDataSource;

    private static final String CONTAIN_SENSETIVE_DATA = "1";
    private static final String NOT_CONTAIN_SENSETIVE_DATA = "2";

    public void asyncDoSerchJob(SerchHiveParams source) throws SQLException, IOException {
        Map resultMap = this.getScanData(source);
        if (CONTAIN_SENSETIVE_DATA.equals(resultMap.get("result"))){
            // 说明有敏感信息
            System.out.println("有敏感信息");
            try {
                FileUtils.moveFile(new File("D:/"+source.getPath()), new File("D:/"+"hasSensetive/"+source.getPath()));
            } catch (Exception e) {
                System.err.println("文件移动报错."+e.getMessage());
            }
            // todo: 入库
        }
        if(NOT_CONTAIN_SENSETIVE_DATA.equals(resultMap.get("result"))){
            // 不含敏感信息
            System.out.println(resultMap.get("content"));
            // todo: 入库
        }
    }

    public Map getScanData(SerchHiveParams source) throws SQLException, IOException {
        Statement statement = druidDataSource.getConnection().createStatement();

        ResultSet res = statement.executeQuery(source.getSql());
        boolean flag = false;
        List<String> list = new ArrayList<>();
        int count = res.getMetaData().getColumnCount();
        String str = null;
        File file = new File("D:/" + source.getPath());
        if (!file.exists()) {
            file.createNewFile();
        }
        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
        StringBuilder scanTempData = new StringBuilder();

        Map exsitSentiveWords = new HashMap();
        while (res.next()) {
            str = "";
            for (int i = 1; i < count; i++) {
                str += res.getString(i) + "\t";
            }
            str += res.getString(count);
            scanTempData.append(System.lineSeparator() + str);

            if (scanTempData.length() > 134117728) {
                exsitSentiveWords = ScanDataUtil.scanSentiveWords(scanTempData.toString());
                if (exsitSentiveWords.get("result").equals("1")) {
                    // todo: 库名 表名
                    exsitSentiveWords.put("filename", "表" + source.getTableName() + "中含有敏感信息文件");
                    // todo: 扫描到敏感信息
                    flag = true;
                    return exsitSentiveWords;
                }
                scanTempData = new StringBuilder();
            }
            bw.write(str);
            bw.newLine();
            bw.flush();
            log.info(str);
            list.add(str);
        }
        bw.close();

        if (scanTempData.length() < 134117728) {
            exsitSentiveWords = ScanDataUtil.scanSentiveWords(scanTempData.toString());
            if (exsitSentiveWords.get("result").equals("1")) {
                // todo: 库名 表名
                exsitSentiveWords.put("filename", "表XX中含有敏感信息文件");
                flag = true;
                // todo: 扫描到敏感信息
                return exsitSentiveWords;
            }
        }
        //没有敏感信息
        exsitSentiveWords.put("result", "2");
        exsitSentiveWords.put("content", "文件内容不含有敏感信息。");

        log.info(">>>>>>>>>>>>>>>>>hive job success");
        return exsitSentiveWords;
    }
}
