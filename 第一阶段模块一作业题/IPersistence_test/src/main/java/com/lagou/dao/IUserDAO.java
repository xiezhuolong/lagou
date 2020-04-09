package com.lagou.dao;

import com.lagou.pojo.User;

import java.util.List;

public interface IUserDAO {
    //查询所有用户
    public List<User> findAll();

    //根据条件进行用户查询
    public User findByCondition(User user);

    /**
     * 新增一个用户
     *
     * @param user
     * @return boolean
     */
    public boolean createUser(User user);

    /**
     * 批量新增用户
     *
     * @param userList
     * @return boolean
     */
    public boolean createUsers(List<User> userList);

    /**
     * 修改用户
     *
     * @param user
     * @return boolean
     */
    public boolean modifyUser(User user);

    /**
     * 删除用户（多个通过英文逗号分割）
     *
     * @param ids
     * @return boolean
     */
    public boolean deleteUserByIds(String ids);
}
