// Copyright (c) 1998-2019 Core Solutions Limited. All rights reserved.
// ============================================================================
// CURRENT VERSION CNT.5.0.1
// ============================================================================
// CHANGE LOG
// CNT.5.0.1 : 2019-XX-XX, damon.huang, creation
// ============================================================================
package com.damon.controller.user;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.damon.service.user.UserService;

import lombok.extern.slf4j.Slf4j;

/**
 * @author damon.huang
 *
 */
@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    @RequestMapping(value="/user", method = RequestMethod.GET)
    public List listUser(){
        return userService.findAll();
    }

    @PutMapping(value="/index/{id}")
    public String index(@PathVariable String id) {
        log.debug("indexing user by {}", id);
        try {
            userService.index(id);
        } catch (final IOException e) {
            return "404";
        }
        log.debug("indexed user by {} done", id);
        return "201";
    }

    @GetMapping(value="/index/get/{id}")
    public String getDocFromES(@PathVariable String id) {
        log.debug("get doc from Es by {}", id);
        try {
            return userService.getDocFromES(id);
        } catch (final IOException e) {
            return "";
        }
    }

    @GetMapping(value="/index/query/{searchContent}")
    public List<String> queryFromES(@PathVariable String searchContent) throws IOException {
        return userService.queryFromES(searchContent);
    }

}
