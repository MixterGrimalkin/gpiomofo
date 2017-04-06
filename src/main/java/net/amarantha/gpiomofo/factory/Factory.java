package net.amarantha.gpiomofo.factory;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.pi4j.io.gpio.PinPullResistance;
import net.amarantha.gpiomofo.annotation.Parameter;
import net.amarantha.gpiomofo.factory.entity.HasEnable;
import net.amarantha.gpiomofo.factory.entity.HasName;
import net.amarantha.utils.properties.PropertiesService;
import net.amarantha.utils.service.ServiceFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static net.amarantha.utils.reflection.ReflectionUtils.iterateAnnotatedFields;
import static net.amarantha.utils.reflection.ReflectionUtils.reflectiveSet;
import static net.amarantha.utils.shell.Utility.log;

public class Factory<T extends HasName & HasEnable> {

    @Inject private Injector injector;
    @Inject private ServiceFactory services;
    @Inject private PropertiesService props;

    private boolean failQuietly;

    private final String itemDescription;

    Factory(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    //////////////
    // Creation //
    //////////////

    public <K extends T> K create(String name, Class<K> clazz, Map<String, String> config) {
        K result = injector.getInstance(clazz);
        register(name, result);
        services.injectServices(result);
        props.injectPropertiesOrExit(result);
        injectParameters(result, config);
        result.enable();
        return result;
    }

    public <K extends T> K create(Class<K> triggerClass) {
        return create(getNextName(triggerClass.getSimpleName()), triggerClass, null);
    }

    public <K extends T> K create(Class<K> triggerClass, Map<String, String> config) {
        return create(getNextName(triggerClass.getSimpleName()), triggerClass, config);
    }

    public <K extends T> K create(String name, Class<K> triggerClass) {
        return create(name, triggerClass, null);
    }


    ////////////////
    // Parameters //
    ////////////////

    private void injectParameters(Object object, Map<String, String> config) {
        if (config != null && !config.isEmpty()) {
            iterateAnnotatedFields(object, Parameter.class,
                    (field, annotation) -> {
                        if (config.containsKey(annotation.value())) {
                            reflectiveSet(object, field, config.get(annotation.value()),
                                    (type, value) -> {
                                        if (type == PinPullResistance.class) {
                                            return PinPullResistance.valueOf(value);
                                        }
                                        return null;
                                    }
                            );
                        }
                    }
            );
        }
    }

    ///////////////////
    // Registrations //
    ///////////////////

    private Map<String, T> registrations = new HashMap<>();

    void register(String name, T t) {
        t.setName(name);
        register(t);
    }

    private void register(T t) {
        String name = t.getName();
        if ( name==null ) {
            name = getNextName();
        }
        if ( !failQuietly && registrations.containsKey(name) ) {
            throw new IllegalStateException(itemDescription + " '" + name + "' is already registered");
        }
        registrations.put(name, t);
    }

    public T get(String name) {
        T result = registrations.get(name);
        if ( !failQuietly && result==null ) {
            throw new IllegalStateException(itemDescription + " '" + name + "' not registered");
        }
        return result;
    }

    public Collection<T> getAll() {
        return registrations.values();
    }

    public void clearAll() {
        nextIds.clear();
        registrations.clear();
    }

    public void setFailQuietly(boolean failQuietly) {
        this.failQuietly = failQuietly;
    }

    /////////////////
    // Auto-Naming //
    /////////////////

    private String getNextName() {
        return getNextName(itemDescription);
    }

    String getNextName(String prefix) {
        Integer nextId = nextIds.get(prefix);
        if ( nextId==null ) {
            nextId = 1;
        }
        String name = prefix + "-" + nextId++;
        nextIds.put(prefix, nextId);
        return name;
    }

    private Map<String, Integer> nextIds = new HashMap<>();

}
