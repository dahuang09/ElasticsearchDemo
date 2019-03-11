// Copyright (c) 1998-2018 Core Solutions Limited. All rights reserved.
// ============================================================================
// CURRENT VERSION CNT.8.10.0
// ============================================================================
// CHANGE LOG
// CNT.8.10.0: 2018-09-10, damon.huang KME-3528, creation
// ============================================================================
package com.damon.constansts;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

import lombok.Data;

/**
 * @author damon.huang
 *
 */

@Data
public class ESIndexContext {

    private static ObjectMapper mapper = new ObjectMapper();

    private final boolean delete;
    private String entityName;
    private String entityId;
    private String json;

    /**
     * @param delete
     * @param entityName
     * @param entityId
     */
    public ESIndexContext(String entityName, String entityId, boolean delete) {
        this.entityName = entityName;
        this.entityId = entityId;
        this.delete = delete;
        mapper.registerModule(new ParameterNamesModule());
        mapper.registerModule(new Jdk8Module());
        mapper.registerModule(new JavaTimeModule()); // new module, NOT JSR310Module
    }

    /**
     * @param pojo the pojo to set
     * @throws JsonProcessingException
     */
    public void setPojo(Object pojo) throws JsonProcessingException {
        this.json = mapper.writeValueAsString(pojo);
    }

}
