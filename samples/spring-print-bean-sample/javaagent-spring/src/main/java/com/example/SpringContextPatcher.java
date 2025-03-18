package com.example;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import tech.ytsaurus.spyt.patch.annotations.AddMethod;
import tech.ytsaurus.spyt.patch.annotations.Decorate;
import tech.ytsaurus.spyt.patch.annotations.DecoratedMethod;
import tech.ytsaurus.spyt.patch.annotations.OriginClass;

@Decorate
@OriginClass("org.springframework.context.support.AbstractApplicationContext")
public class SpringContextPatcher {

    @DecoratedMethod
    protected void registerBeanPostProcessors(ConfigurableListableBeanFactory beanFactory) {
        __registerBeanPostProcessors(beanFactory);
        printBeans(beanFactory);
    }

    protected void __registerBeanPostProcessors(ConfigurableListableBeanFactory beanFactory) {
    }

    @AddMethod
    private void printBeans(ConfigurableListableBeanFactory beanFactory) {
        String[] definitionNames = beanFactory.getBeanDefinitionNames();
        for (String beanName : definitionNames) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);
            System.out.println(beanDefinition);
            System.out.println("--------------------------------------------------------");
        }
    }
}
