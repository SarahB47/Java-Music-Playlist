/**
 * <b>May not add any accessor/mutator for this class</b>
 */

public class PlayableItem implements Comparable<PlayableItem> {
    private int lastPlayedTime;
    private int totalPlayTime;
    private String endpoint;
    private String title;
    private String artist;
    private int popularity;
    private int playedCounts; // How many time this song has been played, initially to be 0

    public PlayableItem(int lastTime, int totalPlayTime, String endpoint, String title,
                        String artist, int popularity) {
        this.lastPlayedTime = lastTime;
        this.totalPlayTime = totalPlayTime;
        this.endpoint = endpoint;
        this.title = title;
        this.artist = artist;
        this.popularity = popularity;
        this.playedCounts = 0;
    }

    public String getArtist() {
        return this.artist;
    }

    public String getTitle() {
        return this.title;
    }

    public int getPopularity() {
        return this.popularity;
    }

    public void setPopularity(int pop) {
        this.popularity = pop;
    }

    public boolean playable() {
        return lastPlayedTime != totalPlayTime;
    }

    public boolean play() {
        if (lastPlayedTime == totalPlayTime) {
            lastPlayedTime = 0;
            playedCounts++;
            return true;
        }
        if (lastPlayedTime < totalPlayTime) {
            lastPlayedTime++;
        }
        return playable();
    }

    public boolean equals(PlayableItem another) {

        boolean titleCompare = this.title.equals(another.title);
        boolean artistCompare = this.artist.equals(another.artist);
        boolean totalTimeCompare = this.totalPlayTime == another.totalPlayTime;
        boolean endpointCompare = this.endpoint.equals(another.endpoint);

        return titleCompare && artistCompare && totalTimeCompare && endpointCompare;
    }

    public String toString() {
        return this.title + "," + this.endpoint + "," + this.lastPlayedTime
                + "," + this.totalPlayTime + "," + this.artist + "," + this.popularity
                + "," + this.playedCounts;
    }

    @Override
    public int compareTo(PlayableItem o) {
        return this.playedCounts - o.playedCounts;
    }
}
