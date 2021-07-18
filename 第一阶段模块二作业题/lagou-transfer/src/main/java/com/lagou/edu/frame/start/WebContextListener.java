package com.lagou.edu.frame.start;

import com.lagou.edu.frame.start.ioc.IoCInitialization;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class WebContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        //拿到xml，并获取xml中的配置属性
        ServletContext servletContext = servletContextEvent.getServletContext();
        String ioC_bean_range = servletContext.getInitParameter("IoC_Bean_Range");
        try {
            new IoCInitialization().setIoC_bean_range(ioC_bean_range).InitIoC();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
