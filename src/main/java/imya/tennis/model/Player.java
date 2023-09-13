package imya.tennis.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(indexes = {@Index(name = "nameIndex", columnList = "name")})
public class Player {
    @Id
    @GeneratedValue
    private long id;

    @Column(nullable = false, unique = true)
    @Length(min = 5, max = 255)
    private String name;

    public Player(String name) {
        this.name = name;
    }
}