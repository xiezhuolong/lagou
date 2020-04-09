package com.lagou.sqlSession;

import com.lagou.pojo.Configuration;
import com.lagou.pojo.MappedStatement;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.List;

public class DefaultSqlSesion implements SqlSession {
    private Configuration configuration;

    public DefaultSqlSesion(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public <E> List<E> selectList(String statementId, Object... params) throws Exception {
        //将要去完成simpleEXecutor里面的query方法调用
        SimpleExecutor simpleExecutor = new SimpleExecutor();
        MappedStatement mappedStatement = configuration.getMappedStatementMap().get(statementId);
        List<Object> list = simpleExecutor.query(configuration, mappedStatement, params);
        return (List<E>) list;
    }

    @Override
    public <T> T selectOne(String statementId, Object... params) throws Exception {
        List<Object> objects = selectList(statementId, params);
        if (objects.size() == 1) {
            return (T) objects.get(0);
        } else {
            throw new RuntimeException("查询结果为空或者返回结果过多");
        }
    }

    @Override
    public Boolean insertOne(String statementId, Object... params) throws Exception {
        return update(statementId, params);
    }

    @Override
    public Boolean insertList(String statementId, Object... params) throws Exception {
        return update(statementId, params);
    }

    @Override
    public Boolean update(String statementId, Object... params) throws Exception {
        SimpleExecutor simpleExecutor = new SimpleExecutor();
        MappedStatement mappedStatement = configuration.getMappedStatementMap().get(statementId);
        return simpleExecutor.noQuery(configuration, mappedStatement, params);
    }

    @Override
    public Boolean deleteList(String statementId, Object... params) throws Exception {
        return update(statementId, Arrays.asList(params[0].toString().split(",")));
    }

    @Override
    public <T> T getMapper(Class<?> mapperClass) {
        // 使用JDK动态代理来为DAO接口生成代理对象，并返回
        Object proxyInstance = Proxy.newProxyInstance(DefaultSqlSesion.class.getClassLoader(), new Class[]{mapperClass}, new InvocationHandler() {
            @Override
            public Object invoke(Object o, Method method, Object[] args) throws Throwable {
                // 底层都还是去执行JDBC代码 //根据不同情况，来调用selectList或者selectOne
                // 准备参数 1: statementId :sql语句的唯一标识：namespace.id= 接口全限定名.方法名
                // 方法名：findAll
                String methodName = method.getName();
                String className = method.getDeclaringClass().getName();
                String statementId = className + "." + methodName;
                // 准备参数2: params:args
                // 获取被调用方法的返回值类型
                Type genericReturnType = method.getGenericReturnType();
                // 获取被调用方法的传入参数类型
                Type genericParameterType = null;
                if (method.getGenericParameterTypes().length > 0) {
                    genericParameterType = method.getGenericParameterTypes()[0];
                }
                // 获取方法对应sql语句，做类型判断，确定调用哪个方法，以及处理
                MappedStatement mappedStatement = configuration.getMappedStatementMap().get(statementId);
                String sqlType = mappedStatement.getSql().substring(0, 6);
                switch (sqlType) {
                    case "select":
                        // 判断是否进行了 泛型类型参数化
                        if (genericReturnType instanceof ParameterizedType) {
                            List<Object> objects = selectList(statementId, args);
                            return objects;
                        }
                        return selectOne(statementId, args);
                    case "insert":
                        // 判断是否进行了 泛型类型参数化
                        if (genericParameterType instanceof ParameterizedType) {
                            return insertList(statementId, args);
                        }
                        return insertOne(statementId, args);
                    case "update":
                        return update(statementId, args);
                    case "delete":
                        return deleteList(statementId, args);
                }
                //目前不支持更高级的语句
                throw new Exception("目前仅支持select|insert|update|delete开头的SQL语句，需要额外支持请联系作者");
            }
        });
        return (T) proxyInstance;
    }
}
