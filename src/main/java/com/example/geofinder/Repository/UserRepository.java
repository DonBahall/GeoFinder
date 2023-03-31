package com.example.geofinder.Repository;

import com.example.geofinder.Model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserModel,Long> {
    Optional<UserModel> findByEmailAndPassword(String email,String password);
    Optional<UserModel> findByEmail(String email);
    Optional<UserModel> findById(Long id);
    boolean existsByEmail(String email);
}
