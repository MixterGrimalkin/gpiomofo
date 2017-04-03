package net.amarantha.gpiomofo.factory;

import com.google.inject.Singleton;
import net.amarantha.gpiomofo.annotation.Parameter;
import net.amarantha.utils.reflection.ReflectionUtils;

import java.util.Map;

import static net.amarantha.utils.reflection.ReflectionUtils.iterateAnnotatedFields;
import static net.amarantha.utils.reflection.ReflectionUtils.reflectiveSet;

@Singleton
public class ParameterInjector {

    public void inject(Object o, Map<String, String> config) {
        iterateAnnotatedFields(o, Parameter.class, (f,a) -> reflectiveSet(o, f, config.get(a.value())));


    }


}
