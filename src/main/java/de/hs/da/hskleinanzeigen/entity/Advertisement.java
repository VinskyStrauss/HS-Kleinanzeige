package de.hs.da.hskleinanzeigen.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "AD")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Advertisement {
    @Id
    @GeneratedValue
    @Column(name = "ID")
    private int id;

    @Enumerated(EnumType.STRING)
    @Column(name = "TYPE", nullable = false)
    private AdType type;

    @ManyToOne
    @JoinColumn(name = "CATEGORY_ID", nullable = false)
    private Category category;

    @ManyToOne
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;

    @OneToMany(mappedBy = "advertisement")
    private List<Notepad> notepads;

    @Column(name = "TITLE", nullable = false)
    private String title;

    @Column(name = "DESCRIPTION", nullable = false)
    private String description;

    @Column(name = "PRICE")
    private int price;

    @Column(name = "LOCATION")
    private String location;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATED", nullable = false)
    private Date created;

    @PrePersist
    protected void onCreate() {
        created = new Date();
    }
}