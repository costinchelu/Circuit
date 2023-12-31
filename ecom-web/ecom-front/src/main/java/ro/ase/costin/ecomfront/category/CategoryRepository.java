package ro.ase.costin.ecomfront.category;


import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ro.ase.costin.ecomcommon.entity.Category;

import java.util.List;

@Repository
public interface CategoryRepository extends CrudRepository<Category, Integer> {

    List<Category> findAllByEnabledIsTrueOrderByName();
    /*
    varianta HQL
    @Query("SELECT c FROM Category c WHERE c.enabled = true ORDER BY c.name ASC")
    List<Category> findAllEnabled();
     */

    @Query("SELECT c FROM Category c WHERE c.enabled = true AND c.alias = ?1")
    Category findByAliasEnabled(String alias);


}
