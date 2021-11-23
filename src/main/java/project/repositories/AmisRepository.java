package project.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import project.model.Amis;

@Repository
public interface AmisRepository extends CrudRepository<Amis, Long> {

}
