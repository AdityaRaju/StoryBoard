package com.om.context;


import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ApplicationContext {

    static ApplicationContext context = null;
    static ClassPathXmlApplicationContext springContext;
    private ApplicationContext(){


    }

    public static  ApplicationContext getInstance(){
        if(context == null){
            context = new ApplicationContext();
            springContext = new ClassPathXmlApplicationContext("/applicationContext.xml");
            //springContext.
        }
        return context;
    }

    public  Object getBean(String id){

        return springContext.getBean(id);

    }



}
