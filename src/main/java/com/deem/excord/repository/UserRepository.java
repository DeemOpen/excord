package com.deem.excord.repository;

import com.deem.excord.domain.EcUser;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<EcUser, Long> {

    List<EcUser> findByEnabledOrderByUsernameAsc(Boolean value);

    EcUser findByUsername(String userName);

    EcUser findByUsernameAndPassword(String userName, String password);
}
