package com.example.hivedemo.Utils;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ScanDataUtil {

    public static Map<String, String> scanColumnWords(String FileContent) {
        Map<String,String> map = new HashMap<>(5);
        map.put("status","1");
        log.info(">>>>>>>>>>>>>> do on every lines");
        return map;
    }

}
