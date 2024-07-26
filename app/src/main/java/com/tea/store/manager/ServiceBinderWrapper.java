package com.tea.store.manager;

import android.app.Service;
import android.os.Binder;

public class ServiceBinderWrapper extends Binder {
    public Service service;
    public ServiceBinderWrapper(Service service){
        this.service = service;
    }

    public Service getService() {
        return service;
    }
}
