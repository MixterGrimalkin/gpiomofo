package net.amarantha.gpiomofo.factory;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import net.amarantha.gpiomofo.annotation.Service;
import net.amarantha.gpiomofo.service.AbstractService;
import net.amarantha.utils.reflection.ReflectionUtils;

import java.util.HashSet;
import java.util.Set;

import static net.amarantha.utils.shell.Utility.log;

@Singleton
public class ServiceFactory {

    @Inject private Injector injector;

    private Set<AbstractService> services = new HashSet<>();

    public <S extends AbstractService> S get(Class<S> clazz) {
        S service = injector.getInstance(clazz);
        services.add(service);
        return service;
    }

    public void startAll() {
        services.forEach(AbstractService::start);
    }

    public void stopAll() {
        services.forEach(AbstractService::stop);
        services.clear();
    }

    public void inject(Object object) {
        ReflectionUtils.iterateAnnotatedFields(object, Service.class, (field, annotation) -> {
            Class<?> clazz = field.getType();
            if ( AbstractService.class.isAssignableFrom(clazz) ) {
                try {
                    field.setAccessible(true);
                    field.set(object, get((Class<AbstractService>)clazz));
                } catch (IllegalAccessException e) {
                    log("Could not inject service " + object.getClass().getSimpleName() + "!");
                    System.exit(1);
                }
            }
        });

    }

}
