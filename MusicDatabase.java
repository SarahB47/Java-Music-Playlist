import java.io.FileNotFoundException;
import java.util.Hashtable;
import java.util.TreeMap;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.io.File;
import java.util.stream.Collectors;

public class MusicDatabase {

    private Hashtable<String, ArrayList<PlayableItem>> data;
    private TreeMap<String, ArrayList<PlayableItem>> artists;
    private Recommendation recommender;
    private int size;

    public MusicDatabase() {
        this.data = new Hashtable<>();
        this.artists = new TreeMap<>();
        this.recommender = new Recommendation("UserData.csv");
        this.size = 0;
    }

    public boolean addSongs(File inputFile) {
        try {
            Scanner myReader = new Scanner(inputFile);
            myReader.nextLine();
            while (myReader.hasNextLine()) {
                String songInfo = myReader.nextLine();
                String[] songSplit = songInfo.split(",");
                addSongs(songSplit[2], songSplit[3], Integer.parseInt(songSplit[4]),
                        Integer.parseInt(songSplit[5]), songSplit[7]);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            return false;
        }
        return true;
    }

    public void addSongs(String name, String artist, int duration,
                         int popularity, String endpoint) {
        // the song to be added
        PlayableItem song = new PlayableItem(0, duration, endpoint, name, artist, popularity);

        // getting the array of items with "name" in data
        ArrayList<PlayableItem> tempTitle = data.get(name);

        // adding to data
        if (tempTitle != null) {
            boolean duplicate = false;
            for (PlayableItem s : tempTitle) {
                if (s.equals(song)) {
                    s.setPopularity(popularity);
                    duplicate = true;
                }
            }

            if (!duplicate) {
                tempTitle.add(song);
                data.put(name, tempTitle);
                size++;
            }
        } else {
            ArrayList<PlayableItem> newTitles = new ArrayList<>();
            newTitles.add(song);
            data.put(name, newTitles);
            size++;
        }

        ArrayList<PlayableItem> tempArtist = artists.get(artist);

        if (tempArtist != null) {
            boolean duplicate2 = false;
            for (PlayableItem a : tempArtist) {
                if (a.equals(song)) {
                    duplicate2 = true;
                    break;
                }
            }
            if (!duplicate2) {
                tempArtist.add(song);
                artists.put(artist, tempArtist);
            }
        } else {
            ArrayList<PlayableItem> newArtist = new ArrayList<>();
            newArtist.add(song);
            artists.put(artist, newArtist);
        }
    }

    public ArrayList<PlayableItem> partialSearchBySongName(String name) {
        ArrayList<PlayableItem> nameSearch = new ArrayList<>();
        for (String key: data.keySet()) {
            if ((key.toLowerCase()).contains(name.toLowerCase())) {
                nameSearch.addAll(data.get(key));
            }
        }
        return nameSearch;
    }

    public ArrayList<PlayableItem> partialSearchByArtistName(String name) {
        ArrayList<PlayableItem> artistSearch = new ArrayList<>();
        for (String key: artists.keySet()) {
            if ((key.toLowerCase()).contains(name.toLowerCase())) {
                artistSearch.addAll(artists.get(key));
            }
        }
        artistSearch.sort(Comparator.comparing(PlayableItem::getPopularity));
        Collections.reverse(artistSearch);

        return artistSearch;
    }

    public ArrayList<PlayableItem> searchHighestPopularity(int threshold) {
        ArrayList<PlayableItem> highestPop = new ArrayList<>();

        for (ArrayList<PlayableItem> items: data.values()) {
            for (PlayableItem song: items) {
                if (song.getPopularity() >= threshold) {
                    highestPop.add(song);
                }
            }
        }
        highestPop.sort(Comparator.comparing(PlayableItem::getPopularity));
        Collections.reverse(highestPop);

        return highestPop;
    }

    public ArrayList<PlayableItem> getRecommendedSongs(List<String> fiveArtists) {
        String[] recommendedArtists = recommender.recommendNewArtists(fiveArtists);

        ArrayList<PlayableItem> recommendedSongs = new ArrayList<>();

        if (recommendedArtists.length == 0) {
            ArrayList<PlayableItem> none = new ArrayList<>();
            return none;
        }

        for (String artist: recommendedArtists) {
            ArrayList<PlayableItem> artistInfo = artists.get(artist);
            if (artistInfo != null) {
                for (PlayableItem song: artistInfo) {
                    recommendedSongs.add(song);
                }
            }
        }

        recommendedSongs.sort(Comparator.comparing(PlayableItem::getPopularity));
        Collections.reverse(recommendedSongs);
        ArrayList<PlayableItem> topTen = (ArrayList<PlayableItem>) recommendedSongs.stream().
                limit(10).collect(Collectors.toList());
        return topTen;
    }

    public int size() {
        return this.size;
    }
}
