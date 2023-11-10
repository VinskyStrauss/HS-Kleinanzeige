package de.hs.da.hskleinanzeigen.repository;
import de.hs.da.hskleinanzeigen.entity.Advertisement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdvertisementRepository extends JpaRepository<Advertisement,Integer> {
    Advertisement findByTitle(String name);
}
