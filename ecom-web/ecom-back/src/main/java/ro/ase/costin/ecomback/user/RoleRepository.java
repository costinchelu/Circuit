package ro.ase.costin.ecomback.user;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ro.ase.costin.ecomcommon.entity.Role;


@Repository
public interface RoleRepository extends CrudRepository<Role, Integer> {

}
