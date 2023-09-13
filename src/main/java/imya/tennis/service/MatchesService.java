package imya.tennis.service;

import imya.tennis.dao.MatchDao;
import imya.tennis.model.Match;

import java.util.List;

public class MatchesService {
    private static final MatchDao matchDao = new MatchDao();

    public static List<Match> getPageOfMatches(int itemsPerPage, int page) {
        return matchDao.readByPage(itemsPerPage, page);
    }

    public static List<Match> getPageOfMatches(int itemsPerPage, int page, String alike) {
        return matchDao.readByPageAlike(itemsPerPage, page, alike);
    }

    public static long getCountOfMatches() {
        return matchDao.readCount();
    }

    public static long getCountOfMatches(String alike) {
        return matchDao.readCountAlike(alike);
    }

    public static long calculateCountOfPages(int itemsPerPage, long count) {
        return (count / itemsPerPage) + 1;
    }

    public static List<Match> getMatchesForPage(int itemsPerPage, int page, String filterName) {
        if (filterName != null && !filterName.equals("")) {
            return MatchesService.getPageOfMatches(itemsPerPage, page, filterName);
        } else {
            return MatchesService.getPageOfMatches(itemsPerPage, page);
        }
    }
}
