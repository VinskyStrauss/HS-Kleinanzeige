package de.hs.da.hskleinanzeigen;
import javax.persistence.*;
import java.util.Date;

enum AdType {
    OFFER,
    REQUEST
}
@Entity
@Table(name = "AD")
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
    private int category_id;

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
}