package com.example.bloominterface;

import com.example.bloominterface.pojo.LogEntry;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BloomInterfaceApplicationTests {

    private Logger logger= LoggerFactory.getLogger(this.toString());



    @Test
    void contextLoads() {
    }


    @Test
    public void test1(){



    }


    @Test
    public void testLog(){
      logger.info("first log");

    }

}
