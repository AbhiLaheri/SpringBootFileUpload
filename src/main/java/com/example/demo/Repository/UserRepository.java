package com.example.demo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.Domain.User;

public interface UserRepository extends JpaRepository<User,Long>{

	User findByDescription(String username);
	
	

}
