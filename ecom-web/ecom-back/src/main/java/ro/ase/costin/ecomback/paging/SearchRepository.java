package ro.ase.costin.ecomback.paging;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

@NoRepositoryBean
public interface SearchRepository<T, I> extends PagingAndSortingRepository<T, I> {

    Page<T> findAll(String keyword, Pageable pageable);
}