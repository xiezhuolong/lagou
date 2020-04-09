package com.lagou.sqlSession;

import java.util.List;

public interface SqlSession {
    //查询所有
    <E> List<E> selectList(String statementId, Object... params) throws Exception;

    //根据条件查询单个
    <T> T selectOne(String statementId, Object... params) throws Exception;

    //新增一个
    <T> T insertOne(String statementId, Object... params) throws Exception;

    //批量新增
    <T> T insertList(String statementId, Object... params) throws Exception;

    //修改单个
    <T> T update(String statementId, Object... params) throws Exception;

    //批量删除
    <T> T deleteList(String statementId, Object... params) throws Exception;

    //为DAO接口生成代理实现类
    <T> T getMapper(Class<?> mapperClass);
}
