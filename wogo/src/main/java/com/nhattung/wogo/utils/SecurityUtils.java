package com.nhattung.wogo.utils;

import com.nhattung.wogo.enums.ErrorCode;
import com.nhattung.wogo.exception.AppException;
import com.nhattung.wogo.security.user.WogoUserDetails;
import com.sun.security.auth.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    public static Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof WogoUserDetails userDetails) {
            return userDetails.getId();
        }

        throw new AppException(ErrorCode.UNAUTHORIZED);
    }
}
