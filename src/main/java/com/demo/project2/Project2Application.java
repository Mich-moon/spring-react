package com.demo.project2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.demo.project2.model.URole;
import com.demo.project2.model.User;
import com.demo.project2.model.Role;
import com.demo.project2.repository.UserRepository;
import com.demo.project2.repository.RoleRepository;


@SpringBootApplication
public class Project2Application implements CommandLineRunner{

	public static void main(String[] args) {
		SpringApplication.run(Project2Application.class, args);
	}

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository rolesRepository;



	// run code at Springboot startup
	@Override
	public void run(String... args) throws Exception {

		// adding dummy data to repository

		// password = 'password123' encrypted as BCrypt

		this.userRepository.save(new User("Ramesh", "Fadatare", "admin@gmail.com", "$2a$10$sGNHrP2xydwJmv6gXTlIPu/9gNqhYhVNhr9UIPcZaU3CHGY42JM.a" ));
		this.userRepository.save(new User("Tom", "Cruise", "tom@gmail.com", "$2a$10$sGNHrP2xydwJmv6gXTlIPu/9gNqhYhVNhr9UIPcZaU3CHGY42JM.a" ));
		this.userRepository.save(new User("Tony", "Stark", "tony@gmail.com", "$2a$10$sGNHrP2xydwJmv6gXTlIPu/9gNqhYhVNhr9UIPcZaU3CHGY42JM.a" ));

		// roles types
		this.rolesRepository.save(new Role(URole.ROLE_ADMIN));
		this.rolesRepository.save(new Role(URole.ROLE_MODERATOR));
		this.rolesRepository.save(new Role(URole.ROLE_USER));

	}
}
