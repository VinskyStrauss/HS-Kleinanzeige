package de.hs.da.hskleinanzeigen;
import javax.persistence.*;

@Entity
@Table(name = "CATEGORY")
public class Category {
    @Id
    @GeneratedValue
    @Column(name = "ID")
    private int id;

    @Column(name = "NAME", nullable = false)
    private String name;
}
