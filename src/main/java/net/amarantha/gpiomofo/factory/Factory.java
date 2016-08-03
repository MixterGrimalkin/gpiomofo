package net.amarantha.gpiomofo.factory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Factory<T extends HasName> {

    private final String itemDescription;

    public Factory(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    ///////////////////
    // Registrations //
    ///////////////////

    private Map<String, T> registrations = new HashMap<>();

    protected void register(String name, T t) {
        t.setName(name);
        register(t);
    }

    protected void register(T t) {
        String name = t.getName();
        if ( name==null ) {
            name = getNextName();
        }
        if ( registrations.containsKey(name) ) {
            throw new IllegalStateException(itemDescription + " '" + name + "' is already registered");
        }
        registrations.put(name, t);
        System.out.println(t.getClass().getSimpleName() + ": " + name);
    }

    public T get(String name) {
        return registrations.get(name);
    }

    public Collection<T> getAll() {
        return registrations.values();
    }

    public void clearAll() {
        registrations.clear();
    }

    /////////////////
    // Auto-Naming //
    /////////////////

    protected String getNextName() {
        return getNextName(itemDescription);
    }

    protected String getNextName(String prefix) {
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
