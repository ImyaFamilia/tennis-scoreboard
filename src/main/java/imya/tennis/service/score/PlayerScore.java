package imya.tennis.service.score;

import imya.tennis.model.Player;
import lombok.Data;

import java.util.LinkedList;
import java.util.List;

@Data
public class PlayerScore {
    private final Player player;
    private Score points;
    private int set;
    private int tieBreak;
    List<Integer> previousSets = new LinkedList<>();

    public PlayerScore(Player player) {
        this.player = player;
        points = Score.ZERO;
        set = 0;
    }

    public void endSet() {
        previousSets.add(set);
        reset();
    }

    public void reset() {
        points = Score.ZERO;
        set = 0;
        tieBreak = 0;
    }
}
