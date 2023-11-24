package de.hs.da.hskleinanzeigen.entity;
import jakarta.persistence.*;
import java.util.Date;

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
    private Category category;

    @ManyToOne
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;

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

    public Advertisement(AdType type, Category category, User user, String title, String description, int price, String location) {
        this.type = type;
        this.category = category;
        this.user = user;
        this.title = title;
        this.description = description;
        this.price = price;
        this.location = location;
    }

    public Advertisement() {

    }

    public int getId() {
        return id;
    }

    public AdType getType() {
        return type;
    }

    public Category getCategory() {
        return category;
    }

    public User getUser() {return user;}

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getPrice() {
        return price;
    }

    public String getLocation() {
        return location;
    }


}