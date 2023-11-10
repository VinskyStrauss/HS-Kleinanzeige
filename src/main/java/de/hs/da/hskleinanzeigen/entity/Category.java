package de.hs.da.hskleinanzeigen.entity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import de.hs.da.hskleinanzeigen.entity.Advertisement;
import jakarta.persistence.*;
import java.util.List;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "CATEGORY")
public class Category {
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Id
    @GeneratedValue
    @Column(name = "ID")
    private int id;

    @Column(name = "NAME", nullable = false)
    private String name;

    @OneToMany(mappedBy = "category")
    private List<Advertisement> advertisements;

    public Category(String name) {
        this.name = name;
    }

    public Category() {
    }


}
