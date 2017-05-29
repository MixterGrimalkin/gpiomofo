package net.amarantha.gpiomofo.display.zone;

//import org.json.simple.JSONArray;
//import org.json.simple.JSONObject;
//import org.json.simple.parser.JSONParser;
//import org.json.simple.parser.ParseException;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class MessageGroup {

    public static final String SEP = ";;";

    public MessageGroup(String[] zoneIds) {
        this.zoneIds = zoneIds;
        nextMessageSet();
    }

    private String id;
    private String[] zoneIds;
    private Map<Integer, Map<String, Message>> allMessages = new HashMap<>();
    private Map<String, Boolean> hasCollected = new HashMap<>();
    private int insertPointer = 0;
    private int requestPointer = -1;
    private boolean persistMessages = true;

    public void clearMessages() {
        clearMessages(true);
    }

    public void clearMessages(boolean resetPointer) {
        allMessages.clear();
        if ( resetPointer ) {
            insertPointer = 0;
            nextMessageSet();
        }
        saveMessages();
    }

    public void addMessages(String input) {
        String[] msgs = input.split(SEP);
        if ( msgs.length!= zoneIds.length ) {
            throw new IllegalArgumentException("Wrong number of messages, expected " + zoneIds.length);
        }
        Map<String, Message> messages = new LinkedHashMap<>();
        for (int i = 0; i< zoneIds.length; i++ ) {
            messages.put(zoneIds[i], new Message(msgs[i]));
        }
        allMessages.put(insertPointer++, messages);
        saveMessages();
    }

    public void nextMessageSet() {
        requestPointer++;
        if ( requestPointer >= allMessages.keySet().size() ) {
            requestPointer = 0;
        }
        for ( String field : zoneIds) {
            hasCollected.put(field, false);
        }
    }

    public Message requestMessage(TextZone zone) {
        String id = zone.getId();
        if ( hasCollected(id) ) {
            return null;
        }
        Map<String, Message> currentMessages = allMessages.get(requestPointer);
        if ( currentMessages!=null ) {
            Message result = currentMessages.get(id);
            if ( result!=null ) {
                hasCollected.put(id, true);
                if (allCollected()) {
                    nextMessageSet();
                }
                return result;
            }
        }
        return null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPersistMessages(boolean persistMessages) {
        this.persistMessages = persistMessages;
    }

    private boolean hasCollected(String id) {
        return hasCollected.get(id)!=null && hasCollected.get(id);
    }

    private boolean allCollected() {
        return !hasCollected.values().contains(false);
    }


    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void saveMessages() {
        if ( persistMessages && filename!=null ) {
            try (FileWriter file = new FileWriter(filename)) {
                file.write(getAsJson());
                file.flush();
            } catch (IOException e) {
                System.out.println("Error writing file");
            }
        }
    }

    public String[] getZoneIds() {
        return zoneIds;
    }

    private String filename;

    public void loadMessages() {
//        if ( persistMessages && filename!=null ) {
//            try (FileReader file = new FileReader(filename)) {
//                JSONParser parser = new JSONParser();
//                JSONObject obj = (JSONObject) parser.parse(file);
//                Object pointer = obj.get("requestPointer");
//                if ( pointer!=null ) {
//                    requestPointer = Integer.parseInt(pointer.toString());
//                }
//                JSONArray messagesJson = (JSONArray) obj.get("messages");
//                if (messagesJson != null) {
//                    Iterator<JSONObject> iter = messagesJson.iterator();
//                    allMessages.clear();
//                    insertPointer = 0;
//                    while (iter.hasNext()) {
//                        JSONObject jsonEntry = iter.next();
//                        Map<String, Message> messages = new HashMap<>();
//                        for (String field : zoneIds) {
//                            JSONObject messageJson = (JSONObject) jsonEntry.get(field);
//                            if (messageJson != null) {
//                                messages.put(field, new Message(messageJson.get("id").toString(), messageJson.get("text").toString()));
//                            }
//                        }
//                        allMessages.put(insertPointer++, messages);
//                    }
//                }
//            } catch (IOException e) {
//                saveMessages();
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//        }
    }

    private String getAsJson() {

//        JSONObject json = new JSONObject();
//        JSONArray messages = new JSONArray();
//        json.put("messages", messages);
//        json.put("requestPointer", requestPointer);
//
//        for ( Entry<Integer, Map<String, Message>> entry : allMessages.entrySet() ) {
//            JSONObject messageObject = new JSONObject();
//            for ( Entry<String, Message> messageEntry : entry.getValue().entrySet() ) {
//                messageObject.put(messageEntry.getKey(), messageEntry.getValue());
//            }
//            messages.add(messageObject);
//        }
//
//        return json.toJSONString();
        throw new UnsupportedOperationException("JSON fucked up");
    }

    public String listMessages() {
        String result = "";
        for ( Entry<Integer, Map<String, Message>> entry : allMessages.entrySet() ) {
            for ( String field : zoneIds) {
                result += entry.getValue().get(field).getText() + SEP;
            }
            result = result.substring(0, result.length()-SEP.length()) + "\n";
        }
        if ( result.length()>1 ) {
            result = result.substring(0, result.length()-1);
        }
        return result;
    }

}
