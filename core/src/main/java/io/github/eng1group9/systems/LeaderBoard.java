package io.github.eng1group9.systems;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class LeaderBoard {
    private HashMap<String, Integer> entries;
    private int maxLen;
    private int length;

    /**
     * Creates a leaderboard from a map of Usernames and Scores, with the specified max length
     * 
     * @param leaderboard Pairings of unique usernames to scores
     * @param maxLength The maximum number of entries that can exist on the leaderboard
     */
    public LeaderBoard(HashMap<String, Integer> leaderboard, int maxLength){
        entries = new HashMap<>(leaderboard);
        maxLen = maxLength;
    }
    /**
     * Creates an empty leaderboard with 5 spaces
     */
    public LeaderBoard(){
        this(new HashMap<>(), 5);
    }

    public int getLength(){
        return length;
    }

    /**
     * Adds an entry to the leaderboard if there is space
     * If there is not sufficient space then behaviour will depend on whether the score ranks above the lowest value already in the leaderboard
     * If the score is less than or equal to the lowest value in the leaderboard, it will not be added
     * If the score is higher than the lowest value, it will be added and the lowest value will be removed
     * 
     * @param name The name of the user who achieved the score. If name already exists it will overwrite
     * @param score The score that the user achieved
     */
    public void addEntry(String name, int score){
        if(length == maxLen && get(maxLen) < score){
            popLowest();
        }
        else{
            length++;
        }
        entries.put(name, score);
    }

    private Entry<String, Integer> getEntry(int rank) throws IndexOutOfBoundsException{
        return getSortedList().get(rank);
    }

    /**
     * Gets the score corresponding to the given name
     * 
     * @param name The name of the user who's score to get
     * @return The score of the user
     */
    public Integer get(String name){
        return entries.get(name);
    }
    /**
     * Gets the score corresponding to the user of the specified rank when ordered by score
     * 
     * @param rank The rank of the user who's score to get
     * @return The score of the corresponding user
     * @throws IndexOutOfBoundsException If rank is out of range
     */
    public Integer get(int rank) throws IndexOutOfBoundsException{
        return getEntry(rank).getValue();
    }

    /**
     * Removes an entry by the rank when ordered by score
     * 
     * @param rank The rank to remove
     * @throws IndexOutOfBoundsException If rank is out of range
     */
    public void removeEntry(int rank) throws IndexOutOfBoundsException{
        if( rank < 0 | rank > length){
            throw new IndexOutOfBoundsException();
        }
        
        Integer removed = entries.remove(getEntry(rank).getKey());
        if(removed != null){
            length--;
        }
    }
    /**
     * Removes an entry by the name of the user
     * 
     * @param name The name of the user to remove
     */
    public void removeEntry(String name){
        length--;
        Integer removed = entries.remove(name);
        if(removed != null){
            length--;
        }
    }

    /**
     * Gets a list of entries ordered by ascending score
     * 
     * @return The list of ordered entries
     */
    public ArrayList<Map.Entry<String, Integer>> getSortedList(){
        ArrayList<Map.Entry<String, Integer>> list = new ArrayList<>(entries.entrySet());
        list.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));
        return list;
    }

    /**
     * Removes the lowest entry
     * 
     * @return The lowest entry
     */
    public Map.Entry<String, Integer> popLowest(){
        if(length == 0) return null;

        Map.Entry<String, Integer> lowest = getSortedList().get(length - 1);
        entries.remove(lowest.getKey());
        return lowest;
    }

    /**
     * Formats as an ordered leaderboard across multiple lines
     */
    public String toString(){
        String rankedList = "LEADERBOARD\n";
        int i = 1;
        for(Entry<String, Integer> e : getSortedList()){
            if(i > maxLen) break;

            rankedList += Integer.toString(i++) + ". " + e.getKey() + "    " + e.getValue().toString() + "\n";
        }
        while(i <= maxLen){
            rankedList += Integer.toString(i) + ".\n";
            i++;
        }

        return rankedList;
    }

    /**
     * Converts the LeaderBoard entries to a JSON
     * @return The JSON string
     */
    private String toJson(){
        String json = "{\n";
        for(Entry<String, Integer> e : getSortedList()){
            json += "\"" + e.getKey() + "\": " + e.getValue().toString() + ",\n";
        }
        json += "}";
        return json;
    }

    /**
     * Creates a LeaderBoard object from a JSON
     * 
     * @param json The JSON specifying the username and score entries
     * @param maxLength The maximum number of entries
     * @return The corresponding LeaderBoard
     */
    private static LeaderBoard fromJson(String json, int maxLength){
        HashMap<String, Integer> newEntries = new HashMap<>();

        try{
            json = json.replaceAll("[{}\n]", "");
            String[] lines = json.split(",");
            for(String line : lines){
                String[] parts = line.split(": ");
                String key = parts[0].replace("\"", "");
                Integer value =  Integer.parseInt(parts[1]);
                newEntries.put(key, value);
            }
        }
        catch(Exception e){
            e.printStackTrace();
            System.err.println("Error parsing leaderboard JSON. Using empty leaderboard");
        }
        return new LeaderBoard(newEntries, maxLength);
    }

    /**
     * Saves the leaderboard entry data to the specified file in JSON format
     * Does not save if the file cannot be written to
     * 
     * @param filepath The file to save the data to
     */
    public void saveToFile(String filepath){
        try{
            Files.write(Paths.get(filepath), toJson().getBytes());
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Loads a leaderboard from the specified file
     * 
     * @param filepath The file containing the entry data in JSON format
     * @param maxLength The maximum number of entries
     * @return The corresponding leaderboard. Or an empty leaderboard if the file is not readable
     */
    public static LeaderBoard loadFromFile(String filepath, int maxLength){
        Path path = Paths.get(filepath);
        try {
            String content = Files.lines(path).collect(Collectors.joining("\n"));
            return fromJson(content, maxLength);
        } catch (IOException e) {
            e.printStackTrace();
            return new LeaderBoard();
        }
    }
}
