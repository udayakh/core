package com.ht.shared.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * This utility is used to get the spring application context.
 * 
 * @author udaya Kumar
 * 
 */
public class ApplicationContextUtils implements ApplicationContextAware {

  private static ApplicationContextUtils INSTANCE;
  private static ApplicationContext context;

  public static ApplicationContextUtils getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new ApplicationContextUtils();
    }
    return INSTANCE;
  }

  private static ApplicationContext getContext() {
    return context;
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    context = applicationContext;
  }

  @SuppressWarnings("unchecked")
  public static <T> T getBean(String beanName) {
    T bean = null;
    try {
      bean = (T) getContext().getBean(beanName);
      return bean;
    } catch (Exception e) {
      return bean;
    }
  }
}
