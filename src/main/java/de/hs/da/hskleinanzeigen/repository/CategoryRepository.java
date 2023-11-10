package de.hs.da.hskleinanzeigen.repository;

import de.hs.da.hskleinanzeigen.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category,Integer> {

    Category findByName(String name);
}
