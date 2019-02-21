package com.example.hivedemo.eventListener;

import com.example.hivedemo.service.AsyncHiveService;
import com.example.hivedemo.syncEvent.SerchHiveEvent;
import com.example.hivedemo.syncEvent.SerchHiveParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.SQLException;

@Slf4j
@Component
@EnableAsync
public class AsyncHiveJobListener {

    @Autowired
    private AsyncHiveService asyncHiveService;

    @Async
    @EventListener(classes = {SerchHiveEvent.class})
    public void onSynSerchHiveEvent(SerchHiveEvent event) throws SQLException, IOException {
        log.info(">>>>>>>>>>>>> do hive job");
        asyncHiveService.asyncDoSerchJob((SerchHiveParams)event.getSource());
    }
}
