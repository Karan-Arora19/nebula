package pse.nebula.user.dto;

import lombok.Data;
import pse.nebula.user.model.Role;

@Data
public class UpdateRoleRequest {
    private Role role;
}
