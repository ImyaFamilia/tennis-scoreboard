package imya.tennis.dao;

import java.util.List;
import java.util.Optional;

public interface Dao<T> {
    void save(T object);
    void saveAll(Iterable<T> iterable);
    Optional<T> read(long id);
    List<T> readAll();
}
