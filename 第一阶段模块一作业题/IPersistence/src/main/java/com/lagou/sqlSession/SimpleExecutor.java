package com.lagou.sqlSession;

import com.lagou.config.BoundSql;
import com.lagou.pojo.Configuration;
import com.lagou.pojo.MappedStatement;
import com.lagou.utils.GenericTokenParser;
import com.lagou.utils.ParameterMapping;
import com.lagou.utils.ParameterMappingTokenHandler;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SimpleExecutor implements Executor {
    @Override                                                                               //user
    public <E> List<E> query(Configuration configuration, MappedStatement mappedStatement, Object... params) throws Exception {
        //  1. 注册驱动,获取链接
        Connection connection = configuration.getDataSource().getConnection();
        //  2. 获取sql语句 : select * from user where id = #{id} and username = #{username}
        //转换sql语句:  select * from user where id = ? and username = ? , 转换的过程中, 还需要对#{}里面的值进行解析存储
        String sql = mappedStatement.getSql();
        BoundSql boundSql = getBoundSql(sql);
        //  3.获取到预处理对象: preparedStatement
        PreparedStatement preparedStatement = connection.prepareStatement(boundSql.getSqlText());
        // 4. 设置参数
        String parameterType = mappedStatement.getParameterType();
        Class<?> parameterTypeClass = getClassType(parameterType);
        List<ParameterMapping> parameterMappingList = boundSql.getParameterMappingList();
        for (int i = 0; i < parameterMappingList.size(); i++) {
            ParameterMapping parameterMapping = parameterMappingList.get(i);
            String content = parameterMapping.getContent();
            //反射
            Field declaredField = parameterTypeClass.getDeclaredField(content);
            declaredField.setAccessible(true);
            Object o = declaredField.get(params[0]);
            preparedStatement.setObject(i + 1, o);
        }
        // 5. 执行sql
        ResultSet resultSet = preparedStatement.executeQuery();
        String resultType = mappedStatement.getResultType();
        Class<?> resultTypeClass = getClassType(resultType);
        ArrayList<Object> objects = new ArrayList<>();
        // 6. 封装返回结果集
        while (resultSet.next()) {
            Object o = resultTypeClass.newInstance();
            //元数据
            ResultSetMetaData metaData = resultSet.getMetaData();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                // 字段名
                String columnName = metaData.getColumnName(i);
                // 字段的值
                Object value = resultSet.getObject(columnName);
                //使用反射或者內省, 根据数据库表和实体的对应关系, 完成封装
                PropertyDescriptor propertyDescriptor = new PropertyDescriptor(columnName, resultTypeClass);
                Method writeMethod = propertyDescriptor.getWriteMethod();
                writeMethod.invoke(o, value);
            }
            objects.add(o);
        }
        return (List<E>) objects;
    }

    static final List<String> commonTypes = Arrays.asList("java.lang.Byte", "java.lang.Short", "java.lang.Integer", "java.lang.Long", "java.lang.Float", "java.lang.Double", "java.lang.Character", "java.lang.Boolean", "java.lang.String");

    @Override
    public Boolean noQuery(Configuration configuration, MappedStatement mappedStatement, Object... params) throws ClassNotFoundException, SQLException, NoSuchFieldException, IllegalAccessException {
        //非查询参数不能为空
        if (params == null || params.length == 0) {
            return false;
        }
        //  1. 注册驱动,获取链接
        Connection connection = configuration.getDataSource().getConnection();
        //  2. 获取sql语句 : select * from user where id = #{id} and username = #{username}
        //转换sql语句:  select * from user where id = ? and username = ? , 转换的过程中, 还需要对#{}里面的值进行解析存储
        String sql = mappedStatement.getSql();
        //1.处理<foreach>标签，根据参数List的Count数，每个之间加,。生成sql语句
        //2.实例化的对象需要判断是不是List，如果是则需要实例化List中的泛型，并循环params，根据params生成对应参数实体，
        Class<?> aClass = null;
        List<?> paramList = null;
        int firstPosition = sql.indexOf("<foreach>");
        if (firstPosition != -1) {
            int lastPosition = sql.indexOf("</foreach>");
            String oldValues = sql.substring(firstPosition + 9, lastPosition);
            String newValues = oldValues;
            if (params[0] instanceof List) {
                paramList = (List<?>) params[0];
                aClass = paramList.get(0).getClass();
                for (int i = 1; i < paramList.size(); i++) {
                    newValues += "," + newValues;
                }
            }
            /*newValues = newValues.substring(0, newValues.length() - 1);*/
            sql = sql.replace("<foreach>" + oldValues + "</foreach>", newValues);
        }
        // 并从当前params中解析出参数然后加入JDBC参数中。
        BoundSql boundSql = getBoundSql(sql);
        // 3.获取到预处理对象: preparedStatement
        PreparedStatement preparedStatement = connection.prepareStatement(boundSql.getSqlText());
        // 4. 设置参数
        String parameterType = mappedStatement.getParameterType();
        Class<?> parameterTypeClass;
        if (aClass != null) {
            parameterTypeClass = aClass;
        } else {
            parameterTypeClass = getClassType(parameterType);
        }
        List<ParameterMapping> parameterMappingList = boundSql.getParameterMappingList();
        int length = 1;
        //校验是否为普通常用类型
        boolean contains = commonTypes.contains(parameterTypeClass.getName());
        if (!contains) {
            length = parameterTypeClass.getDeclaredFields().length;
        }
        for (int i = 0; i < parameterMappingList.size() / length; i++) {
            //让属性根据实体的属性数赋值
            for (int j = 0; j < length; j++) {
                Object o;
                //实现mybatis普通类型参数名称任意定义
                if (contains) {
                    o = paramList.get(i);
                } else {
                    //反射
                    ParameterMapping parameterMapping = parameterMappingList.get(i * length + j);
                    String content = parameterMapping.getContent();
                    Field declaredField = parameterTypeClass.getDeclaredField(content);
                    declaredField.setAccessible(true);
                    if (paramList != null) {
                        o = declaredField.get(paramList.get(i));
                    } else {
                        o = declaredField.get(params[0]);
                    }
                }
                preparedStatement.setObject(i * length + j + 1, o);
            }
        }
        int i = preparedStatement.executeUpdate();
        return i > 0;
    }

    private Class<?> getClassType(String parameterType) throws ClassNotFoundException {
        if (parameterType != null) {
            Class<?> aClass = Class.forName(parameterType);
            return aClass;
        }
        return null;
    }

    /**
     * 完成对#{}的解析工作: 1.将#{}使用? 进行解析, 2.解析出#{}里面的值进行存储
     *
     * @param sql
     * @return
     */
    private BoundSql getBoundSql(String sql) {
        //标记处理类: 配置标记解析器来完成对占位符的解析处理工作
        ParameterMappingTokenHandler parameterMappingTokenHandler = new ParameterMappingTokenHandler();
        GenericTokenParser genericTokenParser = new GenericTokenParser("#{", "}", parameterMappingTokenHandler);
        //解析出来的sql
        String parseSql = genericTokenParser.parse(sql);
        //#{}里面解析出来的参数名称
        List<ParameterMapping> parameterMappings = parameterMappingTokenHandler.getParameterMappings();
        BoundSql boundSql = new BoundSql(parseSql, parameterMappings);
        return boundSql;
    }
}
