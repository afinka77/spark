package com.transfers.repository;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.Reader;

public class BasicRepositoryTest {
    private static final String MYBATIS_CONFIG = "mybatis-config.xml";
    protected static SqlSessionFactory sqlSessionFactory;

    static {
        try {
            SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
            try (Reader reader = Resources.getResourceAsReader(MYBATIS_CONFIG)) {
                sqlSessionFactory = builder.build(reader);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
