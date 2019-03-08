// Copyright (c) 1998-2019 Core Solutions Limited. All rights reserved.
// ============================================================================
// CURRENT VERSION CNT.5.0.1
// ============================================================================
// CHANGE LOG
// CNT.5.0.1 : 2019-XX-XX, damon.huang, creation
// ============================================================================
package com.damon.controller.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.damon.service.user.UserService;

/**
 * @author damon.huang
 *
 */
@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    @RequestMapping(value="/user", method = RequestMethod.GET)
    public List listUser(){
        return userService.findAll();
    }

//    @RequestMapping(value = "/user", method = RequestMethod.POST)
//    public User create(@RequestBody User user){
//        return userService.save(user);
//    }
//
//    @RequestMapping(value = "/user/{id}", method = RequestMethod.DELETE)
//    public String delete(@PathVariable(value = "id") Long id){
//        userService.delete(id);
//        return "success";
//    }

}
