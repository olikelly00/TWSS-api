package tech.makers.aceplay.user;

import org.springframework.data.repository.CrudRepository;

// https://www.youtube.com/watch?v=5r3QU09v7ig&t=1043s
public interface UserRepository extends CrudRepository<User, Long> {
  User findByUsername(String username);
}
