package imya.tennis.dto.response;

import imya.tennis.service.CurrentMatch;
import imya.tennis.service.state.PointsState;
import lombok.Data;

@Data
public class MatchResponse {
    private CurrentMatch match;
    private boolean isTieBreak;

    public MatchResponse(CurrentMatch currentMatch) {
        match = currentMatch;
        isTieBreak = currentMatch.getPointsState().equals(PointsState.TIE_BREAK);
    }
}
