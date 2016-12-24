package com.minorityhobbies.util.ee;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.*;
import java.util.ArrayList;
import java.util.List;

public class LoadBeanOnStartupExtension implements Extension {
    private List<Bean<?>> eagerBeansList = new ArrayList<>();

    public <T> void collect(@Observes ProcessBean<T> event) {
        if (event.getAnnotated().isAnnotationPresent(LoadOnStartup.class)) {
            eagerBeansList.add(event.getBean());
        }
    }

    public void load(@Observes AfterDeploymentValidation event, BeanManager beanManager) {
        for (Bean<?> bean : eagerBeansList) {
            beanManager.getReference(bean, bean.getBeanClass(), beanManager.createCreationalContext(bean)).getClass();
        }
    }
}
