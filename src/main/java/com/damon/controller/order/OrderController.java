package com.damon.controller.order;

import com.damon.pojo.JOrder;
import com.damon.service.order.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/order")
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping(value="/orders")
    public List<JOrder> listOrder(Map<String, Object> params){
        return orderService.listOrder(params);
    }

    @GetMapping(value = "/123")
    public JOrder getOrder() {
        return orderService.getOrder();
    }
}
