package net.amarantha.gpiomofo.config;

public abstract class Config {

    public void setup() {
        System.out.println(BAR+"\n TRIGGERS \n"+BAR);
        setupTriggers();
        System.out.println(BAR+"\n TARGETS \n"+BAR);
        setupTargets();
        System.out.println(BAR+"\n LINKS \n"+BAR);
        setupLinks();
        System.out.println(BAR);
    }

    public abstract void setupTriggers();

    public abstract void setupTargets();

    public abstract void setupLinks();

    public static final String BAR = "--------------------------------------------------";

}
