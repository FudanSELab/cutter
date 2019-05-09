package cn.icedsoul.cutter.data;

import cn.icedsoul.cutter.service.api.HandleDataService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import static cn.icedsoul.cutter.util.Common.isNullString;

/**
 * @author IcedSoul
 * @date 19-5-6 上午10:50
 */
@Log
@Component
public class HandleData implements ApplicationRunner {

    @Value("${cutter.dat-file}")
    private String file;

    @Autowired
    private HandleDataService handleDataService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if(!isNullString(file)){
            handleDataService.handleData(file);
            log.info("[NOTICE]: Handle Data Finished!");
        }
        else {
            log.info("Please define the file path!");
        }
    }
}
