package imya.tennis.service;

import imya.tennis.model.Player;
import imya.tennis.service.score.Score;
import imya.tennis.service.state.PointsState;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CurrentMatch class for currently active matches")
class CurrentMatchTest {
    final Player firstPlayer = new Player(1, "Вася");
    final Player secondPlayer = new Player(2, "Настя");
    final CurrentMatch currentMatch = new CurrentMatch(firstPlayer, secondPlayer);

    @Test
    @DisplayName("Can't take score with wrong player id")
    void testWrongPlayerId() {
        assertThrows(IllegalArgumentException.class, () -> currentMatch.takeScore(3),
            "Should throw IllegalArgumentException if provided wrong player id");
    }

    @Test
    @DisplayName("Can't get winner when game is not ended")
    void testCantGetWinner() {
        assertThrows(IllegalStateException.class, currentMatch::getWinner,
            "Should throw IllegalStateException when trying to get winner when game is not ended");
    }

    @Test
    @DisplayName("Regular points")
    void testRegularPoints() {
        for (int i = 0; i < 4; i++) {
            currentMatch.takeScore(firstPlayer.getId());
        }

        testNewSet();
    }

    @Test
    @DisplayName("Game points")
    void testGamePoints() {
        for (int i = 0; i < 3; i++) {
            currentMatch.takeScore(firstPlayer.getId());
            currentMatch.takeScore(secondPlayer.getId());
        }

        assertAll(() -> {
            assertEquals(currentMatch.getFirstPlayerScore().getPoints(), Score.FORTY, "Points should be 40");
            assertEquals(currentMatch.getSecondPlayerScore().getPoints(), Score.FORTY, "Points should be 40");
            assertEquals(currentMatch.getPointsState(), PointsState.GAME, "Points state should be GAME");
        });

        currentMatch.takeScore(firstPlayer.getId());

        assertAll(() -> {
            assertEquals(currentMatch.getFirstPlayerScore().getPoints(), Score.GAME, "First points should be AD");
            assertEquals(currentMatch.getSecondPlayerScore().getPoints(), Score.FORTY, "Second points should be 40");
        });

        currentMatch.takeScore(secondPlayer.getId());

        assertAll(() -> {
            assertEquals(currentMatch.getFirstPlayerScore().getPoints(), Score.FORTY, "Points should be 40");
            assertEquals(currentMatch.getSecondPlayerScore().getPoints(), Score.FORTY, "Points should be 40");
        });

        currentMatch.takeScore(secondPlayer.getId());

        assertAll(() -> {
            assertEquals(currentMatch.getFirstPlayerScore().getPoints(), Score.FORTY, "First points should be 40");
            assertEquals(currentMatch.getSecondPlayerScore().getPoints(), Score.GAME, "Second points should be AD");
        });

        currentMatch.takeScore(firstPlayer.getId());

        assertAll(() -> {
            assertEquals(currentMatch.getFirstPlayerScore().getPoints(), Score.FORTY, "Points should be 40");
            assertEquals(currentMatch.getSecondPlayerScore().getPoints(), Score.FORTY, "Points should be 40");
        });

        for (int i = 0; i < 2; i++) {
            currentMatch.takeScore(firstPlayer.getId());
        }

        testNewSet();
    }

    @Test
    @DisplayName("Tie break points")
    void testTieBreakPoints() {
        for (int i = 0; i < 20; i++) {
            currentMatch.takeScore(firstPlayer.getId());
        }

        for (int i = 0; i < 24; i++) {
            currentMatch.takeScore(secondPlayer.getId());
        }

        for (int i = 0; i < 4; i++) {
            currentMatch.takeScore(firstPlayer.getId());
        }

        assertAll(() -> {
            assertEquals(currentMatch.getFirstPlayerScore().getSet(), 6, "First set should equals to 6");
            assertEquals(currentMatch.getSecondPlayerScore().getSet(), 6, "Second set should equals to 6");
            assertEquals(currentMatch.getFirstPlayerScore().getTieBreak(), 0, "No tie break when started new game");
            assertEquals(currentMatch.getSecondPlayerScore().getTieBreak(), 0, "No tie break when started new game");
            assertEquals(currentMatch.getPointsState(), PointsState.TIE_BREAK, "Points state should be TIE_BREAK when sets reach 6-6");
        });

        currentMatch.takeScore(firstPlayer.getId());

        assertAll(() -> {
            assertEquals(currentMatch.getFirstPlayerScore().getTieBreak(), 1, "First tie break should go to 1");
            assertEquals(currentMatch.getSecondPlayerScore().getTieBreak(), 0, "No opposite tie break should be affected");
        });

        currentMatch.takeScore(secondPlayer.getId());

        assertAll(() -> {
            assertEquals(currentMatch.getFirstPlayerScore().getTieBreak(), 1, "First tie break should go to 1");
            assertEquals(currentMatch.getSecondPlayerScore().getTieBreak(), 1, "Second tie break should go to 1");
        });

        for (int i = 0; i < 2; i++) {
            currentMatch.takeScore(firstPlayer.getId());
        }

        testNewSetAfterEnd();
    }

    @Test
    @DisplayName("Player wins game by winning 3 of 3 sets 6-0")
    void testPlayer3Sets60Times() {
        for (int i = 0; i < 72; i++) {
            currentMatch.takeScore(firstPlayer.getId());
        }

        testGameEnded();
    }

    @Test
    @DisplayName("Player wins game by winning 3 of 3 sets 5-7")
    void testPlayer3Sets75Times() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 20; j++) {
                currentMatch.takeScore(firstPlayer.getId());
            }

            for (int j = 0; j < 28; j++) {
                currentMatch.takeScore(secondPlayer.getId());
            }
        }

        testGameEnded();
    }

    @Test
    @DisplayName("Player wins game by winning 3 of 5 sets 7-6")
    void testPlayer5Sets76Times() {
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 20; j++) {
                currentMatch.takeScore(secondPlayer.getId());
            }

            for (int j = 0; j < 20; j++) {
                currentMatch.takeScore(firstPlayer.getId());
            }

            for (int j = 0; j < 4; j++) {
                currentMatch.takeScore(secondPlayer.getId());
            }

            for (int j = 0; j < 4; j++) {
                currentMatch.takeScore(firstPlayer.getId());
            }

            for (int j = 0; j < 2; j++) {
                currentMatch.takeScore(secondPlayer.getId());
            }
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 20; j++) {
                currentMatch.takeScore(firstPlayer.getId());
            }

            for (int j = 0; j < 20; j++) {
                currentMatch.takeScore(secondPlayer.getId());
            }

            for (int j = 0; j < 4; j++) {
                currentMatch.takeScore(firstPlayer.getId());
            }

            for (int j = 0; j < 4; j++) {
                currentMatch.takeScore(secondPlayer.getId());
            }

            for (int j = 0; j < 2; j++) {
                currentMatch.takeScore(firstPlayer.getId());
            }
        }

        testGameEnded();
    }

    void testNewSet() {
        assertAll(() -> {
            assertEquals(currentMatch.getFirstPlayerScore().getSet(), 1, "Set should equals to 1 if won");
            assertEquals(currentMatch.getSecondPlayerScore().getSet(), 0, "Opposite set should not be affected");
            assertEquals(currentMatch.getFirstPlayerScore().getPoints(), Score.ZERO, "No points when started new set");
            assertEquals(currentMatch.getSecondPlayerScore().getPoints(), Score.ZERO, "No points when started new set");
            assertEquals(currentMatch.getPointsState(), PointsState.REGULAR, "Points state should be REGULAR when started new set");
        });
    }

    void testNewSetAfterEnd() {
        assertAll(() -> {
            assertEquals(currentMatch.getFirstPlayerScore().getSet(), 0, "Set should be empty");
            assertEquals(currentMatch.getSecondPlayerScore().getSet(), 0, "Set should be empty");
            assertEquals(currentMatch.getFirstPlayerScore().getPoints(), Score.ZERO, "No points when started new game");
            assertEquals(currentMatch.getSecondPlayerScore().getPoints(), Score.ZERO, "No points when started new game");
            assertEquals(currentMatch.getPointsState(), PointsState.REGULAR, "Points state should be REGULAR when started new game");
        });
    }

    void testGameEnded() {
        assertAll(() -> {
            assertTrue(currentMatch.isGameEnded(), "CurrentMatch should return true if game ended");
            assertThrows(IllegalStateException.class, () -> currentMatch.takeScore(firstPlayer.getId()),
                "Should throw IllegalStateException if trying to take score when game ended");
            assertDoesNotThrow(() -> {
                currentMatch.getWinner();
            }, "Shouldn't throw exception when getting winner");
        });
    }
}