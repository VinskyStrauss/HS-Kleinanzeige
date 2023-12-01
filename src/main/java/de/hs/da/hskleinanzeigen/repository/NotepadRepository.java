package de.hs.da.hskleinanzeigen.repository;
import de.hs.da.hskleinanzeigen.entity.Advertisement;
import de.hs.da.hskleinanzeigen.entity.Notepad;
import de.hs.da.hskleinanzeigen.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotepadRepository extends JpaRepository<Notepad,Integer> {
    Optional<Notepad> findByUserAndAdvertisement(User user, Advertisement advertisement);

    Optional<List<Notepad>> findByUser(User user);

    void deleteByUserAndAdvertisement(User user, Advertisement advertisement);
}
