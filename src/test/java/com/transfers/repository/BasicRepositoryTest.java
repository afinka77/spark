package com.transfers.repository;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;

public class BasicRepositoryTest {
    private static final String MYBATIS_CONFIG = "mybatis-config.xml";
    protected static SqlSessionFactory sqlSessionFactory;

    static {
        try {
            SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
            try (InputStream stream = BasicRepositoryTest.class.getClassLoader()
                    .getResourceAsStream(MYBATIS_CONFIG)) {
                sqlSessionFactory = builder.build(stream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
