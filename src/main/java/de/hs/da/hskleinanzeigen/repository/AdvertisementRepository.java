package de.hs.da.hskleinanzeigen.repository;
import de.hs.da.hskleinanzeigen.entity.AdType;
import de.hs.da.hskleinanzeigen.entity.Advertisement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdvertisementRepository extends JpaRepository<Advertisement,Integer> {
    Optional<Advertisement> findByTitle(String name);

    @Query("SELECT ad FROM Advertisement ad WHERE (:type IS NULL OR ad.type = :type) AND (:categoryId IS NULL OR ad.category.id = :categoryId) AND ad.price BETWEEN :priceFrom AND :priceTo")
    Page<Advertisement> findByQuery(@Param("type") AdType type,
                                    @Param("categoryId") Integer categoryId,
                                    @Param("priceFrom") Integer priceFrom,
                                    @Param("priceTo") Integer priceTo,
                                    Pageable pageable);

}
