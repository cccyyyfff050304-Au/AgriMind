package com.agrimind.user.vo;

import com.agrimind.user.entity.SysUser;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserProfileVO {

    private Long id;

    private String username;

    private String realName;

    private String phone;

    private String email;

    private String roleCode;

    public static UserProfileVO from(SysUser user) {
        return new UserProfileVO(
                user.getId(),
                user.getUsername(),
                user.getRealName(),
                user.getPhone(),
                user.getEmail(),
                user.getRoleCode()
        );
    }
}
