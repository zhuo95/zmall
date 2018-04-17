package com.zmall.service;

import com.github.pagehelper.PageInfo;
import com.zmall.common.ServerResponse;
import com.zmall.pojo.Shipping;

public interface IShippingService {

    ServerResponse add(Integer userId, Shipping shipping);

    ServerResponse del(Integer userId, Integer shippingId);

    ServerResponse update(Integer userId, Shipping shipping);

    ServerResponse<Shipping> select(Integer userId, Integer shippingId);

    ServerResponse<PageInfo> list(Integer userId , int pageNum, int pageSize);
}
