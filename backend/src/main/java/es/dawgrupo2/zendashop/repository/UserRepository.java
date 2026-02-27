package es.dawgrupo2.zendashop.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import es.dawgrupo2.zendashop.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByName(String name);
    Optional<User> findByEmail(String email);
    Optional<User> findBySurname(String surname);
    List<User> findByDisabledFalse(Pageable pageable);

}