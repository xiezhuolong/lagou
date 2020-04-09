package com.lagou.test;

import com.lagou.dao.IUserDAO;
import com.lagou.io.Resources;
import com.lagou.sqlSession.SqlSession;
import com.lagou.sqlSession.SqlSessionFactory;
import com.lagou.sqlSession.SqlSessionFactoryBuilder;
import org.junit.Test;

import java.io.InputStream;

public class IPersistenceTest {

    @Test
    public void test() throws Exception {
        InputStream resourcesAsStream = Resources.getResourcesAsStream("sqlMapConfig.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourcesAsStream);
        SqlSession sqlSession = sqlSessionFactory.openSession();

        //调用
        /*User user = new User();
        user.setId(1);
        user.setUsername("张三");
        User user2 = sqlSession.selectOne("user.selectOne", user);
        System.out.println(user2);*/
        /*List<User> users = sqlSession.selectList("user.selectList");
        for (User user1 : users) {
            System.out.println(user1);
        }*/
        IUserDAO userDAO = sqlSession.getMapper(IUserDAO.class);
        //查询全部
        /*List<User> all = userDAO.findAll();
        for (User user : all) {
            System.out.println(user);
        }*/
        //根据条件查询
        /*User user = new User();
        user.setId(1);
        user.setUsername("lisi");
        User user5 = userDAO.findByCondition(user);
        System.out.println(user5);*/
        //批量新增
        /*List<User> users = new ArrayList<>();
        User user1 = new User();
        user1.setId(3);
        user1.setUsername("3");
        users.add(user1);
        User user2 = new User();
        user2.setId(4);
        user2.setUsername("4");
        users.add(user2);
        boolean b = userDAO.createUsers(users);*/
        //批量删除
        /*boolean b = userDAO.deleteUserByIds("3,4");*/
        //单个新增
        /*User user3 = new User();
        user3.setId(5);
        user3.setUsername("5");
        boolean b = userDAO.createUser(user3);*/
        //更新
        /*User user4 = new User();
        user4.setId(5);
        user4.setUsername("1111");
        boolean b = userDAO.modifyUser(user4);*/
        //单个删除
        /*boolean b = userDAO.deleteUserByIds("5");*/

        /*System.out.println(b);*/
    }
}
