package org.senju.eshopeule.service;

import org.senju.eshopeule.exceptions.UsernameAlreadyExistsException;
import org.senju.eshopeule.model.user.Role;

public interface StaffService {
    void createAccount(String username, String password, Role role) throws UsernameAlreadyExistsException;
}
