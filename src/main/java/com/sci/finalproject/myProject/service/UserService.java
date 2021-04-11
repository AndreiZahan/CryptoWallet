package com.sci.finalproject.myProject.service;

import com.sci.finalproject.myProject.model.Role;
import com.sci.finalproject.myProject.model.User;
import com.sci.finalproject.myProject.repository.RoleRepository;
import com.sci.finalproject.myProject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {

    public UserRepository userRepository;
    public RoleRepository roleRepository;
    public BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User findUserByUserName(String userName) {
        return userRepository.findByUserName(userName);
    }

    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }

    public User saveUser(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setActive(true);
        Role userRole = roleRepository.findByRole("USER");
        user.setRoles(new HashSet<Role>(Arrays.asList(userRole)));
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        List<User> result = userRepository.findAll();

        if (result.size() > 0) {
            return result;
        } else {
            return new ArrayList<>();
        }
    }

    public void deleteUserById(Long id) throws Exception {
        Optional<User> user = userRepository.findById(id);

        if(user.isPresent())
        {
            userRepository.deleteById(id);
        } else {
            throw new Exception("No user record exist for given id");
        }
    }
}
