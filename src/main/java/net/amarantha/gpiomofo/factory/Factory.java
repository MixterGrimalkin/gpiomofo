package net.amarantha.gpiomofo.factory;

import javax.inject.Inject;
import java.io.PrintStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Factory<T extends HasName> {

    @Inject private PrintStream out;

    private boolean failQuietly;

    private final String itemDescription;

    public Factory(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    ///////////////////
    // Registrations //
    ///////////////////

    private Map<String, T> registrations = new HashMap<>();

    public void register(String name, T t) {
        t.setName(name);
        register(t);
    }

    public void register(T t) {
        String name = t.getName();
        if ( name==null ) {
            name = getNextName();
        }
        if ( !failQuietly && registrations.containsKey(name) ) {
            throw new IllegalStateException(itemDescription + " '" + name + "' is already registered");
        }
        registrations.put(name, t);
        out.println(t.getClass().getSimpleName() + ": " + name);
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
        registrations.clear();
    }

    public void setFailQuietly(boolean failQuietly) {
        this.failQuietly = failQuietly;
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
