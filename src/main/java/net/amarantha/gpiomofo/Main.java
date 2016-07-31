package net.amarantha.gpiomofo;

import com.google.inject.Guice;

public class Main {

    public static void main(String[] args) {
        Guice.createInjector(new LiveModule())
            .getInstance(GpioMoFo.class)
                .start();
    }

}
