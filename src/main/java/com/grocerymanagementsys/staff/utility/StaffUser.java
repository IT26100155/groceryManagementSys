package com.grocerymanagementsys.staff.utility;

import com.grocerymanagementsys.employee.enums.EmployeeRole;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public class StaffUser implements Serializable {
    private final Integer id;
    private final String fullName;
    private final String email;
    private final EmployeeRole role;

    public boolean isAdmin() {
        return role == EmployeeRole.ADMIN;
    }
}
