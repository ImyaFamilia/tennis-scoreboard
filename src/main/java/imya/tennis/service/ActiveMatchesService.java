package imya.tennis.service;

import imya.tennis.dao.MatchDao;
import imya.tennis.dao.PlayerDao;
import imya.tennis.model.Match;
import imya.tennis.model.Player;
import jakarta.persistence.PersistenceException;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ActiveMatchesService {
    private static final Map<UUID, CurrentMatch> currentMatches = new ConcurrentHashMap<>();
    private static final PlayerDao playerDao = new PlayerDao();
    private static final MatchDao matchDao = new MatchDao();

    public synchronized static UUID createNewMatch(Player firstPlayer, Player secondPlayer) {
        if (firstPlayer.getName().equals((secondPlayer.getName()))) {
            throw new IllegalArgumentException("Player names can't be the same");
        }

        firstPlayer = playerDao.saveOrReadExisting(firstPlayer).get();
        secondPlayer = playerDao.saveOrReadExisting(secondPlayer).get();

        UUID uuid = UUID.randomUUID();
        CurrentMatch currentMatch = new CurrentMatch(firstPlayer, secondPlayer);
        currentMatches.put(uuid, currentMatch);

        log.info("Created new match -> {}", uuid);

        return uuid;
    }

    public static boolean hasMatch(UUID uuid) {
        return currentMatches.containsKey(uuid);
    }

    public static CurrentMatch getMatch(UUID uuid) {
        return currentMatches.get(uuid);
    }

    public synchronized static void endMatch(UUID uuid) {
        CurrentMatch currentMatch = getMatch(uuid);

        if (!currentMatch.isGameEnded()) {
            throw new IllegalStateException("Game has not been ended yet");
        }

        matchDao.save(
            new Match(
                currentMatch.getFirstPlayerScore().getPlayer(),
                currentMatch.getSecondPlayerScore().getPlayer(),
                currentMatch.getWinner()
            )
        );

        currentMatches.remove(uuid);

        log.info("Match ended and now saved into database -> {}", uuid);
    }
}
