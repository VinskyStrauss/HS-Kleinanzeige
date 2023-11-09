package de.hs.da.hskleinanzeigen;

import de.hs.da.hskleinanzeigen.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category,Integer> {

    Category findByName(String name);
}
