package com.ty.common.test;

import org.junit.runners.model.InitializationError;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Log4jConfigurer;

import java.io.FileNotFoundException;

/**
 * Created by Ysw on 2017/3/13.
 */
public class SpringTransactionalContextTests extends SpringJUnit4ClassRunner {

    static {
        try {
            Log4jConfigurer.initLogging("classpath:log4j.properties", 1000);
        }catch (FileNotFoundException e) {
            System.err.println("Cannot Initialize Log4j");
        }
    }


    public SpringTransactionalContextTests(Class<?> clazz) throws InitializationError {
        super(clazz);
    }
}
