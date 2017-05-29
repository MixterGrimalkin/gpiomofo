package net.amarantha.gpiomofo.display.font;

import net.amarantha.gpiomofo.display.entity.AlignH;
import net.amarantha.gpiomofo.display.entity.Pattern;
import net.amarantha.utils.colour.RGB;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import static net.amarantha.gpiomofo.display.entity.AlignH.CENTRE;
import static net.amarantha.gpiomofo.display.entity.AlignH.RIGHT;

public class Font {

    public final static char NL = '\n';
    public final static char OPEN_TAG = '{';
    public final static char CLOSE_TAG = '}';

    private Map<Character, Pattern> chars = new HashMap<>();

    public void saveFont(String filename) {
        try ( FileWriter writer = new FileWriter(filename) ) {
            for (Entry<Character, Pattern> entry : chars.entrySet() ) {
                writer.write("~"+entry.getKey()+"\n");
                writer.write(entry.getValue().toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Font loadFont(String filename) {
        try {
            String fontFile = new String(Files.readAllBytes(Paths.get(filename)));
            String[] lines = fontFile.split("\n");
            Character currentChar = null;
            Integer width = null;
            StringBuilder sb = new StringBuilder();
            for ( String line : lines ) {
                if ( line.length() > 1 && line.charAt(0)=='~' ) {
                    if ( currentChar!=null && width!=null ) {
                        Pattern p = new Pattern(width, sb.toString());
                        registerPattern(currentChar, p);
                    }
                    currentChar = line.charAt(1);
                    width = null;
                    sb = new StringBuilder();
                } else {
                    if ( width==null ) {
                        width = line.length();
                    }
                    sb.append(line);
                }
            }
            if ( currentChar!=null && width!=null ) {
                Pattern p = new Pattern(width, sb.toString());
                registerPattern(currentChar, p);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

//    public final void registerPattern(char key, boolean[][] bits) {
//        registerPattern(key, new Pattern(bits));
//    }

    public final void registerPattern(char key, Pattern pattern) {
        chars.put(key, pattern);
    }

    public final Pattern getPattern(char key) {
        return chars.get(key);
    }

    public final int getHeight(char key) {
        Pattern c = getPattern(key);
        if ( c!=null ) {
            return c.getHeight();
        }
        return 0;
    }

    public final int getWidth(char key) {
        Pattern c = getPattern(key);
        if ( c!=null ) {
            return c.getWidth();
        }
        return 0;
    }

    public final int getStringWidth(String str) {
        int width = 0;
        int rowWidth = 0;
        boolean inTag = false;
        if ( str!=null ) {
            for (int i = 0; i < str.length(); i++) {
                char c = str.charAt(i);
                if (inTag) {
                    if (c == CLOSE_TAG) {
                        inTag = false;
                    }
                } else {
                    if (c == OPEN_TAG) {
                        inTag = true;
                    } else {
                        if (i == str.length() - 1) {
                            rowWidth += getWidth(c);
                            width = Math.max(rowWidth, width);
                        } else if (c == NL) {
                            width = Math.max(rowWidth, width);
                            rowWidth = 0;
                        } else {
                            rowWidth += getWidth(c) + 1;
                        }
                    }
                }
            }
        }
        return width;
    }

    public final int getStringHeight(String str) {
        int height = 0;
        int rowHeight = 0;
        boolean inTag = false;
        if ( str!=null ) {
            for (int i = 0; i < str.length(); i++) {
                char c = str.charAt(i);
                if (inTag) {
                    if (c == CLOSE_TAG) {
                        inTag = false;
                    }
                } else {
                    if (c == OPEN_TAG) {
                        inTag = true;
                    } else {
                        rowHeight = Math.max(rowHeight, getHeight(c));
                        if (i == str.length() - 1) {
                            height += rowHeight;
                        } else if (c == NL) {
                            height += rowHeight;
                            rowHeight = 0;
                        }
                    }
                }
            }
        }
        return height;
    }

    public final Pattern renderString(String str) {
        return renderString(str, AlignH.LEFT);
    }

    public final Pattern renderString(String str, AlignH align) {
        if ( str==null || str.isEmpty() ) {
            return new Pattern(1,1);
        }
        int cols = getStringWidth(str);
        int rows = getStringHeight(str);
        Pattern result = new Pattern(cols, rows);
        String[] lines = str.split("\n");
        if ( lines.length==1 ) {
            int cursorX = 0;
            boolean inTag = false;
            boolean penMode = false;
            String tag = "";
            for ( int c=0; c<str.length(); c++ ) {
                char chr = str.charAt(c);
                if ( inTag ) {
                    if ( chr==CLOSE_TAG ) {
                        if ( RED_STR.equals(tag) ) {
                            result.pen(new RGB(255,0,0));
                        } else if ( GREEN_STR.equals(tag) ) {
                            result.pen(new RGB(0,255,0));
                        } else if ( YELLOW_STR.equals(tag) ) {
                            result.pen(new RGB(255,255,0));
                        }
                        tag = "";
                        inTag = false;
                    } else {
                        tag += chr;
                    }
                } else {
                    if ( chr==OPEN_TAG ) {
                        inTag = true;
                        penMode = true;
                    } else {
                        Pattern pattern = getPattern(chr);
                        if (pattern != null) {
                            for (int row = 0; row < pattern.getHeight(); row++) {
                                for (int col = 0; col < pattern.getWidth(); col++) {
                                    if ( penMode ) {
                                        result.draw(col + cursorX, row, pattern.rgb(col, row));
                                    } else {
                                        result.draw(col + cursorX, row, pattern.rgb(col, row));
                                    }
                                }
                            }
                            cursorX += getWidth(chr) + 1;
                        }
                    }
                }
            }
        } else {
            int cursorY = 0;
            for (int l = 0; l < lines.length; l++) {
                String line = lines[l];
                int cursorX = 0;
                if ( align== RIGHT ) {
                    cursorX = cols - getStringWidth(line);
                } else if ( align== CENTRE ) {
                    cursorX = ( cols - getStringWidth(line) ) / 2;
                }
                int lineHeight = getStringHeight(line);
                Pattern pattern = renderString(line);
                for (int row = 0; row<pattern.getHeight(); row++ ) {
                    for (int col = 0; col<pattern.getWidth(); col++ ) {
                        result.draw(row + cursorY, col + cursorX, pattern.rgb(row, col));
                    }
                }
                cursorY += lineHeight;
            }
        }
        return result;
    }


    ///////////////////////////
    // Export Fonts to Files //
    ///////////////////////////

    public static void main(String args[]) {
        System.out.println("Exporting System Fonts...");
//        new SmallFont_Old().saveFont("fonts/SmallFont.fnt");
//        new SimpleFont_Old().saveFont("fonts/SimpleFont.fnt");
//        new LargeFont_Old().saveFont("fonts/LargeFont.fnt");
    }

    public static final String RED_STR = "red";
    public static final String GREEN_STR = "green";
    public static final String BLUE_STR = "blue";
    public static final String YELLOW_STR = "yellow";
    public static final String MULTI_STR = "multi";

}
