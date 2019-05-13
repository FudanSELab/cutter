package cn.icedsoul.cutter;

import cn.icedsoul.cutter.repository.MethodCallRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ModifyScenarioInfoTest {

    @Autowired
    MethodCallRepository methodCallRepository;

    @Test
    public void testMethod() {
        //根据ScenarioName修改ScenarioFrequency
        methodCallRepository.modifyScenarioFrequencyByScenarioName("登录", 1000);
    }
}
