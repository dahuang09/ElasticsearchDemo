package com.damon.controller.order;

import com.damon.pojo.JOrder;
import com.damon.service.order.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/order")
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping(value="/orders")
    public  Map<String, Object> listOrder(@RequestParam("dataSource") String dataSource, @RequestParam("queryStr") String queryStr) throws  Exception {
        Map<String, Object> search = new HashMap<>();
        search.put("description", queryStr);
        search.put("dataSource", dataSource);
        return orderService.listOrder(search);
    }

    @GetMapping(value = "/123")
    public JOrder getOrder() {
        return orderService.getOrder();
    }

    @PutMapping(value="/batch/{count}")
    public String batchCreateOrder(@PathVariable Long count) {
        return orderService.batchCreateOrder(count);
    }

    @GetMapping(value = "/sync")
    public String batchSyncOrderToES() throws  Exception{
        return orderService.batchSyncOrderToES();
    }
}
