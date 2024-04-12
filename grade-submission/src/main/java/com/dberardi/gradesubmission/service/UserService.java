package com.dberardi.gradesubmission.service;

import com.dberardi.gradesubmission.model.User;

public interface UserService {
    User getUser(Long id);
    User getUser(String username);
    User saveUser(User user);
}
