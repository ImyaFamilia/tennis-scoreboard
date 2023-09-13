package imya.tennis;

import imya.tennis.service.score.Score;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Score enum that represents tennis score points")
class ScoreTest {
    @Test
    @DisplayName("Next() return next score")
    void shouldIterate() {
        assertAll(() -> {
            assertEquals(Score.ZERO.next(), Score.FIFTEEN);
            assertEquals(Score.FIFTEEN.next(), Score.THIRTY);
            assertEquals(Score.THIRTY.next(), Score.FORTY);
            assertEquals(Score.FORTY.next(), Score.GAME);
        });
    }

    @Test
    @DisplayName("Throws IllegalStateException on attempting to get next score on last element")
    void shouldThrowIllegalStateExceptionOnIteratingLastElement() {
        assertThrows(IllegalStateException.class, Score.GAME::next);
    }
}