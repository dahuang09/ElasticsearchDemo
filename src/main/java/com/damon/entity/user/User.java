package com.damon.entity.user;




import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

/**
 * @author damon.huang
 *
 */
@Data
public class User {
    private String id;
    private String username;
    private String password;

//    @Transient
    @JsonIgnore
    private String entityName = "User";
}
