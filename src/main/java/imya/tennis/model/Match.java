package imya.tennis.model;

import jakarta.persistence.Entity;
import lombok.*;

import jakarta.persistence.*;

@Data
@RequiredArgsConstructor
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Match {
    @Id
    @GeneratedValue
    private long id;
    @ManyToOne
    @JoinColumn(nullable = false)
    @NonNull
    private Player firstPlayer;
    @ManyToOne
    @JoinColumn(nullable = false)
    @NonNull
    private Player secondPlayer;
    @ManyToOne
    @JoinColumn(nullable = false)
    @NonNull
    private Player winner;
}
