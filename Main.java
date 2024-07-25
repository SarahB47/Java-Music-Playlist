import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {

        PlayableItem first = new PlayableItem(0, 201, "end", "lovelies", "x", 10);
        PlayableItem second = new PlayableItem(0, 174, "end2", "summertime", "y", 9);
        PlayableItem third = new PlayableItem(0, 263, "end2", "otw", "z", 8);
        PlayableItem fourth = new PlayableItem(0, 228, "end2", "call out my name", "a", 8);

        Playlist tester = new Playlist();
        tester.addPlayableItem(first);
        tester.play(202);
        tester.goBack();
        tester.play(202);
        tester.addPlayableItem(second);
        tester.play(280);
        tester.goBack();
        tester.play(280);
        tester.goBack();
        tester.play(280);
        tester.addPlayableItem(third);
        tester.switchPlayingMode(2);
        System.out.println(tester.playingMode);
        tester.play(500);
        tester.removePlayableItem(3);
        tester.play(70);
        tester.play(300);
        tester.goBack();
        tester.play(20);
        tester.addPlayableItem(third);
        System.out.println(tester.showPlaylistStatus());
        tester.removePlayableItem(3);
        tester.play(500);
        tester.addPlayableItem(third);
        tester.play(300);
        tester.goBack();
        tester.play(400);
        tester.goBack();
        tester.play(400);
        System.out.println(tester.showPlaylistStatus());
        tester.goBack();
        tester.play(500);
        System.out.println(tester.showPlaylistStatus());
        tester.removePlayableItem(3);
        tester.play(300);
        tester.goBack();
        tester.goBack();
        tester.play(90);
        System.out.println(tester.showPlaylistStatus());
        tester.addPlayableItem(third);
        System.out.println(tester.showPlaylistStatus());
        tester.play(90);
    }
}