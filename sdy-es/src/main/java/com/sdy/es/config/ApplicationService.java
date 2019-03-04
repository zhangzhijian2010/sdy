package com.sdy.es.config;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Spring 的获取Bean业务类
 *
 * Created by zhijian.zhang@chelaile.net.cn on 2018/11/27.
 */
@Component
public class ApplicationService implements ApplicationContextAware {
    /**
     * 上下文对象实例
     */
    private ApplicationContext applicationContext;

    /**
     * 获取applicationContext
     *
     * @return
     */
    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * 通过name获取 Bean.
     *
     * @param name
     * @return
     */
    public Object getBean(String name) {
        return getApplicationContext().getBean(name);
    }

    /**
     * 通过class获取Bean.
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T getBean(Class<T> clazz) {
        return getApplicationContext().getBean(clazz);
    }

    /**
     * 通过name,以及Clazz返回指定的Bean
     *
     * @param name
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T getBean(String name, Class<T> clazz) {
        return getApplicationContext().getBean(name, clazz);
    }
}

