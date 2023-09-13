package imya.tennis.dto.response;

import imya.tennis.model.Match;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class MatchesResponse {
    List<Match> list;
    int page;
    long totalPages;
    long countOfMatches;
}
