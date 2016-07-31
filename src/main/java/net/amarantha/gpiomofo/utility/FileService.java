package net.amarantha.gpiomofo.utility;

public interface FileService {
    String readFromFile(String filename);

    boolean writeToFile(String filename, String content);
}
