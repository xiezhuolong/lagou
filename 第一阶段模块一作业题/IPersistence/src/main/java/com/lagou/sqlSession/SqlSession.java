package com.lagou.sqlSession;

import java.util.List;

public interface SqlSession {
    //查询所有
    public <E> List<E> selectList(String statementId, Object... params) throws Exception;

    //根据条件查询单个
    public <T> T selectOne(String statementId, Object... params) throws Exception;

    //新增一个
    public <T> T insertOne(String statementId, Object... params) throws Exception;

    //批量新增
    public <T> T insertList(String statementId, Object... params) throws Exception;

    //修改单个
    public <T> T updateOne(String statementId, Object... params) throws Exception;

    //批量删除
    public <T> T deleteList(String statementId, Object... params) throws Exception;

    //为DAO接口生成代理实现类
    public <T> T getMapper(Class<?> mapperClass);
}
