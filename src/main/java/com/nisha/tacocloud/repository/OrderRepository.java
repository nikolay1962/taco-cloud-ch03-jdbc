package com.nisha.tacocloud.repository;

import com.nisha.tacocloud.domain.Order;

public interface OrderRepository {
    Order save(Order order);
}
