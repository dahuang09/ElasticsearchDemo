package com.damon.service.order;

import com.damon.dao.OrderMapper;
import com.damon.pojo.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class OrderService {

    @Autowired
    private OrderMapper orderMapper;

    public List<Order> listOrder(Map<String, Object> params) {
        return orderMapper.listOrder(params);
    }
}
