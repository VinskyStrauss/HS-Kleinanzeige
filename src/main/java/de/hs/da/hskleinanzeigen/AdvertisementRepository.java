package de.hs.da.hskleinanzeigen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdvertisementRepository extends JpaRepository<Advertisement,Integer> {
    Advertisement findByTitle(String name);
}
