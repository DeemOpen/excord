package com.deem.excord.repository;

import com.deem.excord.domain.EcUser;
import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

public interface UserRepository extends CrudRepository<EcUser, Long> {

    List<EcUser> findByEnabledOrderByUsernameAsc(Boolean value);

    EcUser findByUsername(String userName);

    EcUser findByUsernameAndPassword(String userName, String password);

    @Modifying
    @Transactional
    @Query(value = "update ec_user set enabled = false where last_login < DATE_SUB(NOW(),INTERVAL 60 DAY) or last_login is null;", nativeQuery = true)
    public Integer disableOldUsers();
}
