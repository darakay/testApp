package com.darakay.testapp.testapp;


import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;


import java.io.IOException;
import java.io.Reader;


public class Database {
    private static Database instance;
    private SqlSessionFactory sessionFactory;

    private Database(){
        try(Reader reader = Resources.getResourceAsReader("mybatis-config.xml")) {
            this.sessionFactory = new SqlSessionFactoryBuilder().build(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Database getInstance(){
        if(instance == null)
            instance = new Database();
        return instance;
    }

    public SqlSessionFactory getSessionFactory(){
        return sessionFactory;
    }
}
