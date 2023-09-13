package imya.tennis.service;

import imya.tennis.model.Player;
import imya.tennis.service.score.PlayerScore;
import imya.tennis.service.score.Score;
import imya.tennis.service.state.MatchState;
import imya.tennis.service.state.PointsState;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;
import java.util.List;

@Data
@Slf4j
public class CurrentMatch {
    private final PlayerScore firstPlayerScore;
    private final PlayerScore secondPlayerScore;
    private PointsState pointsState;
    private MatchState matchState;
    private static final int SETS_TO_WIN = 3;

    public CurrentMatch(Player firstPlayer, Player secondPlayer) {
        this.firstPlayerScore = new PlayerScore(firstPlayer);
        this.secondPlayerScore = new PlayerScore(secondPlayer);
        pointsState = PointsState.REGULAR;
        matchState = MatchState.ONGOING;
    }

    public void takeScore(long id) {
        if (matchState.equals(MatchState.FIRST_PLAYER_WON) || matchState.equals(MatchState.SECOND_PLAYER_WON)) {
            throw new IllegalStateException("Game already ended");
        }

        if (id != firstPlayerScore.getPlayer().getId() && id != secondPlayerScore.getPlayer().getId()) {
            throw new IllegalArgumentException("Neither player has this id");
        }

        PlayerScore playerScore = getPlayerScoreById(id);
        PlayerScore oppositePlayerScore = getOppositePlayerScoreById(id);

        switch (pointsState) {
            case REGULAR -> doRegular(playerScore, oppositePlayerScore);
            case GAME -> doGame(playerScore, oppositePlayerScore);
            case TIE_BREAK -> doTieBreak(playerScore, oppositePlayerScore);
        }
    }

    private void doRegular(PlayerScore playerScore, PlayerScore oppositePlayerScore) {
        playerScore.setPoints(playerScore.getPoints().next());

        if (playerScore.getPoints().equals(Score.FORTY) && oppositePlayerScore.getPoints().equals(Score.FORTY)) {
            pointsState = PointsState.GAME;
        } else if (playerScore.getPoints().equals(Score.GAME)) {
            newSet(playerScore, oppositePlayerScore);
        }
    }

    private void doGame(PlayerScore playerScore, PlayerScore oppositePlayerScore) {
        if (oppositePlayerScore.getPoints().equals(Score.GAME)) {
            oppositePlayerScore.setPoints(Score.FORTY);
        } else if (playerScore.getPoints().equals(Score.GAME)) {
            newSet(playerScore, oppositePlayerScore);
        } else {
            playerScore.setPoints(playerScore.getPoints().next());
        }
    }

    private void doTieBreak(PlayerScore playerScore, PlayerScore oppositePlayerScore) {
        playerScore.setTieBreak(playerScore.getTieBreak() + 1);

        if (playerScore.getTieBreak() - oppositePlayerScore.getTieBreak() == 2) {
            newSet(playerScore, oppositePlayerScore);
        }
    }

    private void newSet(PlayerScore playerScore, PlayerScore oppositePlayerScore) {
        pointsState = PointsState.REGULAR;
        playerScore.setSet(playerScore.getSet() + 1);

        if (playerScore.getSet() == 6 && oppositePlayerScore.getSet() <= 4 ||
            playerScore.getSet() == 7 && (oppositePlayerScore.getSet() >= 5 && oppositePlayerScore.getSet() < 7)) {
            checkWinConditionOrContinue();
        } else if (playerScore.getSet() == 6 && oppositePlayerScore.getSet() == 6) {
            pointsState = PointsState.TIE_BREAK;
        }

        resetPoints();
    }

    private void checkWinConditionOrContinue() {
        firstPlayerScore.endSet();
        secondPlayerScore.endSet();

        List<Integer> firstSets = firstPlayerScore.getPreviousSets();
        List<Integer> secondSets = secondPlayerScore.getPreviousSets();

        int firstWins = 0, secondWins = 0;
        for (Iterator<Integer> firstIter = firstSets.iterator(), secondIter = secondSets.iterator();
             firstIter.hasNext() && secondIter.hasNext();) {
            int firstSet = firstIter.next(), secondSet = secondIter.next();

            if (firstSet > secondSet) {
                firstWins++;
            } else {
                secondWins++;
            }
        }

        if (firstWins >= SETS_TO_WIN) {
            matchState = MatchState.FIRST_PLAYER_WON;
        } else if (secondWins >= SETS_TO_WIN) {
            matchState = MatchState.SECOND_PLAYER_WON;
        }
    }

    public boolean isGameEnded() {
        return matchState.equals(MatchState.FIRST_PLAYER_WON) || matchState.equals(MatchState.SECOND_PLAYER_WON);
    }

    public Player getWinner() {
        if (matchState.equals(MatchState.ONGOING)) {
            throw new IllegalStateException("Game is not ended yet");
        }

        return matchState.equals(MatchState.FIRST_PLAYER_WON) ? firstPlayerScore.getPlayer() : secondPlayerScore.getPlayer();
    }

    private PlayerScore getPlayerScoreById(long id) {
        return firstPlayerScore.getPlayer().getId() == id ? firstPlayerScore : secondPlayerScore;
    }

    private PlayerScore getOppositePlayerScoreById(long id) {
        return firstPlayerScore.getPlayer().getId() == id ? secondPlayerScore : firstPlayerScore;
    }

    private void resetPoints() {
        firstPlayerScore.setPoints(Score.ZERO);
        secondPlayerScore.setPoints(Score.ZERO);
    }
}
