package com.lagou.edu.service.impl;

import com.lagou.edu.annotation.Transactional;
import com.lagou.edu.annotation.ioc.Autowired;
import com.lagou.edu.annotation.ioc.Service;
import com.lagou.edu.dao.AccountDao;
import com.lagou.edu.pojo.Account;
import com.lagou.edu.service.TransferService;

/**
 * @author 应癫
 */
@Service
@Transactional
public class TransferServiceImpl implements TransferService {

    //private AccountDao accountDao = new JdbcAccountDaoImpl();

    // private AccountDao accountDao = (AccountDao) BeanFactory.getBean("accountDao");
    @Autowired("222")
    int anInt;
    @Autowired("111")
    String string;

    /*public TransferServiceImpl(int anInt, String string, Long aLong, AccountDao accountDao) {
        this.anInt = anInt;
        this.string = string;
        this.aLong = aLong;
        this.accountDao = accountDao;
    }*/

    Long aLong;
    // 最佳状态
    private AccountDao accountDao;

    /*@UseThisConstructor()
    public TransferServiceImpl(@Autowired("com.lagou.edu.dao.impl.JdbcAccountDaoImpl") AccountDao accountDao) {
        this.accountDao = accountDao;
    }*/

    // 构造函数传值/set方法传值
    public void setAccountDao(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    @Autowired
    public void SOut() {
        System.out.println("........Method Test.......");
    }

    public void Sout() {
        System.out.println("........Method Test1.......");
    }

    @Override
    public void transfer(String fromCardNo, String toCardNo, int money) throws Exception {

        /*try{
            // 开启事务(关闭事务的自动提交)
            TransactionManager.getInstance().beginTransaction();*/

        Account from = accountDao.queryAccountByCardNo(fromCardNo);
        Account to = accountDao.queryAccountByCardNo(toCardNo);

        from.setMoney(from.getMoney() - money);
        to.setMoney(to.getMoney() + money);

        accountDao.updateAccountByCardNo(to);
        int c = 1 / 0;
        accountDao.updateAccountByCardNo(from);

        /*    // 提交事务

            TransactionManager.getInstance().commit();
        }catch (Exception e) {
            e.printStackTrace();
            // 回滚事务
            TransactionManager.getInstance().rollback();

            // 抛出异常便于上层servlet捕获
            throw e;

        }*/
    }
}
