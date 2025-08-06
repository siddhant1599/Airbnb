package com.spring.app.airBnb.util;

import com.spring.app.airBnb.entity.User;
import org.springframework.security.core.context.SecurityContextHolder;

public class AppUtils {
    public static User getCurrentUser(){
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
