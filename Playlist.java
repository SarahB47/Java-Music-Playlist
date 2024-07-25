import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.Stack;
import java.util.PriorityQueue;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
public class Playlist {

    private String name;
    int playingMode = 0;
    private int playingIndex = 0; // for inorder/random playing mode/artist
    // recommended playing mode
    private ArrayList<PlayableItem> curList;
    private PlayableItem cur;
    private Stack<PlayableItem> history;
    private PriorityQueue<PlayableItem> freqListened;
    private ArrayList<PlayableItem> playlist;

    public Playlist() {
        this.name = "Default";
        this.curList = new ArrayList<>();
        this.history = new Stack<>();
        this.freqListened = new PriorityQueue<>(Collections.reverseOrder());
        this.playlist = new ArrayList<>();

    }

    public Playlist(String name) {
        this();
        this.name = name;

    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int size() {
        return this.playlist.size();
    }

    public String toString() {
        return this.name + "," + this.size() + " songs";
    }

    public void addPlayableItem(PlayableItem newItem) {
        playlist.add(newItem);
        if (playingMode == 2) {
            if (cur != null) {
                curList.add(newItem);
                Collections.sort(curList, Collections.reverseOrder());
                int newIndex = curList.indexOf(cur);
                if (playingIndex != newIndex) {
                    playingIndex = newIndex;
                }
            } else {
                curList.add(newItem);
                Collections.sort(curList, Collections.reverseOrder());
            }
        } else if (playingMode == 1) {
            Random rand = new Random();
            int indexToAdd = rand.nextInt(curList.size());
            if (indexToAdd <= curList.indexOf(cur)) {
                playingIndex++;
            }
            curList.add(indexToAdd, newItem);
        } else {
            curList.add(newItem);
        }
    }

    public void addPlayableItem(ArrayList<PlayableItem> newItem) {
        for (PlayableItem i : newItem) {
            addPlayableItem(i);
        }
    }

    public boolean removePlayableItem(int number) {
        if (number > playlist.size()) {
            return false;
        }

        if (playingMode == 0) {

            if ((curList.get(number - 1)) == cur) {
                playlist.remove(number - 1);
            } else {
                playlist.remove(number - 1);
                curList.remove(number - 1);
                if ((number - 1) < playingIndex) {
                    playingIndex--;
                }
            }
        }

        if (playingMode == 2) {
            if (playlist.get(number - 1) == cur) {
                playlist.remove(number - 1);
            } else {
                curList.removeAll(Collections.singleton(playlist.get(number - 1)));
                playlist.remove(number - 1);
                if ((number - 1) < playingIndex) {
                    playingIndex--;
                }
            }
        }
        return true;
    }

    public void switchPlayingMode(int newMode) {
        this.playingMode = newMode;
        if (playingMode == 2) {
            if (cur != null) {
                ArrayList<PlayableItem> curTemp = new ArrayList<>();
                curTemp.add(cur);
                playingIndex = 0;
                curList = curTemp;
            } else {
                curList = new ArrayList<>();
                playingIndex = 0;
            }

            freqListened.addAll(playlist);

            int count = freqListened.size();

            for (int i = 0; i < count; i++) {
                curList.add(freqListened.poll());
            }
        }
    }

    public ArrayList<String> getFiveMostPopular() {
        HashSet<String> popularArtists = new HashSet<>();
        PriorityQueue<PlayableItem> popular = new PriorityQueue<>(Collections.reverseOrder());

        for (PlayableItem i: playlist) {
            popular.add(i);
        }

        int counter = 5;
        while (counter > 0) {
            PlayableItem song = popular.poll();
            if (song != null) {
                popularArtists.add(song.getArtist());
                while ((popular.peek()) != null
                        && (popular.peek()).compareTo(song) == 0) {
                    popularArtists.add(popular.poll().getArtist());
                }
            }
            counter--;
        }

        List<String> list = new ArrayList<>(popularArtists);
        ArrayList<String> sortedPopular = (ArrayList<String>) list.stream().
            limit(5).collect(Collectors.toList());

        return sortedPopular;
    }
    public void goBack() {
        playingIndex--;
        if (playingIndex < 0) {
            playingIndex++;
            System.out.println("No more step to go back");
        }
        cur = curList.get(playingIndex);
    }

    private void playInOrder(int seconds) {
        int amountPlayed = 0;
        getNextPlayable();
        boolean completed = false;

        while (seconds > 0) {
            if (cur == null) {
                return;
            }
            if (cur.playable()) {
                completed = false;
                System.out.println("Seconds " + amountPlayed + " : " + cur.getTitle() + " start.");

                while (seconds > 0) {
                    if (cur.play()) {
                        seconds--;
                        amountPlayed++;
                    } else {
                        seconds--;
                        amountPlayed++;
                        completed = true;
                        cur.play();
                        break;
                    }
                }

                if (completed) {
                    System.out.println("Seconds " + amountPlayed + " : " + cur.getTitle()
                            + " complete.");
                    int before = curList.size();
                    int after = playlist.size();
                    cur = getNextPlayable();
                    if (before == after) {
                        if (cur == null) {
                            System.out.println("No more music to play.");
                            playingIndex++;
                            return;
                        } else {
                            playingIndex++;
                        }
                    } else {
                        if (playingMode == 0) {
                            curList = new ArrayList<>(playlist);
                        } else if (playingMode == 2) {
                            curList.remove(cur);
                        }
                        if (cur == null) {
                            System.out.println("No more music to play.");
                            return;
                        }
                    }
                }
            } else {
                System.out.println("Seconds " + amountPlayed + " : "
                        + cur.getTitle() + " start.");
                seconds -= 1;
                amountPlayed++;
                cur.play();
                System.out.println("Seconds " + amountPlayed + " : "
                        + cur.getTitle() + " complete.");
            }
        }
        if (seconds == 0 && completed) {
            System.out.println("Seconds " + amountPlayed + " : " + cur.getTitle() + " start.");
        }
    }


    public void play(int seconds) {
        if (seconds <= 0) {
            System.out.println("Invalid seconds");
        }
        playInOrder(seconds);
    }

    public String showPlaylistStatus() {
        String output = "";
        int counter = 1;
        for (PlayableItem i: playlist) {
            if (i == cur) {
                output += counter + ". " + i.toString() + " - Currently play" + "\n";
            } else {
                output += counter + ". " + i.toString() + "\n";
            }
            counter++;
        }
        return output.strip();
    }

    public PlayableItem getNextPlayable() {
        if (curList.size() == 0) {
            return null;
        }
        if (cur == null) {
            if (playingIndex > curList.size() - 1) {
                return null;
            }
            cur = curList.get(playingIndex);
            return cur;
        } else if (this.playingIndex == curList.size() - 1) {
            return null;
        } else {
            int index = playingIndex + 1;
            if (index <= curList.size()) {
                return curList.get(index);
            } else {
                return null;
            }
        }
    }

}
