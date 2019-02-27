package com.example.hivedemo.syncEvent;

import lombok.Data;

@Data
public class SerchHiveParams {

    private String sql;

    private String path;

    private String tableName;

    private String columns;

}
