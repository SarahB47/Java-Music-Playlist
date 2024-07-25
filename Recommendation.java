import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;
import java.util.LinkedHashMap;
public class Recommendation {

    Map<Long, HashMap<String, Integer>> userData;

    public Recommendation(String filePath) {
        this.userData = new HashMap<>();
        parseCsvFile(filePath);
    }

    private void parseCsvFile(String csvFilePath) {
        try {
            File myFile = new File(csvFilePath);
            Scanner myScanner = new Scanner(myFile);
            myScanner.nextLine();
            while (myScanner.hasNextLine()) {
                String line = myScanner.nextLine();
                String[] userInfo = line.split(",");
                long userID = Long.parseLong(userInfo[0]);
                String artistName = userInfo[1];
                int minutesListened = Integer.parseInt(userInfo[3]);
                if (userData.containsKey(userID)) {
                    HashMap<String, Integer> temp = userData.get(userID);
                    if (temp.containsKey(artistName)) {
                        int listeningTotal = temp.get(artistName) + minutesListened;
                        temp.put(artistName, listeningTotal);
                    } else {
                        temp.put(artistName, minutesListened);
                    }
                    userData.put(userID, temp);
                } else {
                    HashMap<String, Integer> newData = new HashMap<>();
                    newData.put(artistName, minutesListened);
                    userData.put(userID, newData);
                }
            }
            myScanner.close();
        } catch (FileNotFoundException e) {
            return;
        }

        for (Long iD: userData.keySet()) {
            List<Map.Entry<String, Integer>> minutes = new ArrayList<>(userData.get(iD).entrySet());
            minutes.sort(Comparator.comparing(Map.Entry::getValue));
            Collections.reverse(minutes);
            List<Map.Entry<String, Integer>> topFive = minutes.stream().limit(5)
                    .collect(Collectors.toList());

            HashMap<String, Integer> values = new LinkedHashMap<>();

            for (Map.Entry<String, Integer> x: topFive) {
                values.put(x.getKey(), x.getValue());
            }

            userData.put(iD, values);
        }
    }

    public String[] recommendNewArtists(List<String> artistList) {
        // create a hashmap where the key is userID and the value is the jaccad similarity
        HashMap<Long, Double> jaccadsSimilarity = new HashMap<>();

        for (Long userID: userData.keySet()) {
            List<String> userArtists = userData.get(userID).keySet().stream().
                    collect(Collectors.toList());
            int intersection = 0;
            int union;
            for (String artist: artistList) {
                if (userArtists.contains(artist)) {
                    intersection++;
                }
            }
            union = artistList.size() + userArtists.size() - intersection;
            double jaccads = (double) intersection / (double) union;
            jaccadsSimilarity.put(userID, jaccads);
        }

        Long first = Collections.max(jaccadsSimilarity.entrySet(),
                Comparator.comparingDouble(Map.Entry::getValue)).getKey();

        jaccadsSimilarity.replace(first, -1.0);
        Long second = Collections.max(jaccadsSimilarity.entrySet(),
                Comparator.comparingDouble(Map.Entry::getValue)).getKey();

        jaccadsSimilarity.replace(second, -1.0);
        Long third = Collections.max(jaccadsSimilarity.entrySet(),
                Comparator.comparingDouble(Map.Entry::getValue)).getKey();

        Long[] indeces = {first, second, third};

        ArrayList<String> topArtists = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            HashMap<String, Integer> userInfo = userData.get(indeces[i]);
            for (String a : userInfo.keySet()) {
                if ((!topArtists.contains(a)) && (!artistList.contains(a))) {
                    topArtists.add(a);
                }
            }
        }

        String[] output = new String[topArtists.size()];

        for (int j = 0; j < topArtists.size(); j++) {
            output[j] = topArtists.get(j);
        }
        return output;

    }

}
