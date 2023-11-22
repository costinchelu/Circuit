package ro.ase.costin.ecomback.category;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import ro.ase.costin.ecomcommon.entity.Category;

import java.util.List;

@Repository
public interface CategoryRepository extends PagingAndSortingRepository<Category, Integer> {

    @Query("SELECT c FROM Category  c WHERE c.parent.id IS NULL")
    List<Category> findRootCategories(Sort sort);

    @Query("SELECT c FROM Category  c WHERE c.parent.id IS NULL")
    Page<Category> findRootCategories(Pageable pageable);

    @Query("SELECT c FROM Category  c WHERE c.name LIKE %?1%")
    Page<Category> search(String keyword, Pageable pageable);

    Category findByName(String name);

    Category findByAlias(String alias);

    Long countById(Integer id);

    @Modifying
    @Query("UPDATE Category c SET c.enabled = ?2 WHERE c.id = ?1")
    void updateEnabledStatus(Integer id, boolean enabled);
}
