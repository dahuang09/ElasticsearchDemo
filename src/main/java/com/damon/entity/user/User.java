// Copyright (c) 1998-2019 Core Solutions Limited. All rights reserved.
// ============================================================================
// CURRENT VERSION CNT.5.0.1
// ============================================================================
// CHANGE LOG
// CNT.5.0.1 : 2019-XX-XX, damon.huang, creation
// ============================================================================
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
