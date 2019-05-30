package com.damon.service.order;

import com.damon.dao.JOrderMapper;
import com.damon.pojo.JOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class OrderService {

    @Autowired
    private JOrderMapper jOrderMapper;

    public List<JOrder> listOrder(Map<String, Object> params) {
        return jOrderMapper.listOrder(params);
    }

    public JOrder getOrder() {
        return jOrderMapper.selectByPrimaryKey("123");
    }
}
