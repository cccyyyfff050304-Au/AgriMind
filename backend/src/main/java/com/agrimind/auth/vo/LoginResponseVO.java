package com.agrimind.auth.vo;

import com.agrimind.user.vo.UserProfileVO;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponseVO {

    private String token;

    private String tokenType;

    private Long expiresIn;

    private UserProfileVO user;
}
