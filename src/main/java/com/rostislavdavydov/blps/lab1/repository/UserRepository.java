package com.rostislavdavydov.blps.lab1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.rostislavdavydov.blps.lab1.model.User;

@Repository
public interface UserRepository extends CrudRepository<User, Long>, JpaRepository<User, Long> {
}
