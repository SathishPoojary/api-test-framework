package com.shc.automation.api.test.framework.internal.config.injector;

import com.google.inject.Guice;
import com.google.inject.Injector;

public enum APIDependencyInjector {
    INSTANCE;
    private static Injector injector;

    APIDependencyInjector() {
        setInjector();
    }

    private void setInjector() {
        if(injector == null) {
            injector = Guice.createInjector(new APIInjectConfigModule());
        }
    }

    public <T> T getInstance(Class<T> type){
        return injector.getInstance(type);
    }
}
