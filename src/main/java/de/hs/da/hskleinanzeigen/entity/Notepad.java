package de.hs.da.hskleinanzeigen.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "NOTEPAD")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notepad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private int id;

    @ManyToOne
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "AD_ID", nullable = false)
    private Advertisement advertisement;

    @Column(name = "NOTE")
    private String note;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATED", nullable = false)
    private Date created;

    @PrePersist
    protected void onCreate() {
        created = new Date();
    }
}