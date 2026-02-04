package com.schedule.elevator.dto;

import com.schedule.elevator.entity.UserToken;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserTokenDTO extends UserToken implements java.io.Serializable {
    private String username;
    private String employeeId;
    private String roles;
}
