package com.zmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.zmall.common.ServerResponse;
import com.zmall.dao.ShippingMapper;
import com.zmall.pojo.Shipping;
import com.zmall.service.IShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("iShippingService")
public class ShippingServiceimpl implements IShippingService{

    @Autowired
    private ShippingMapper shippingMapper;

    //add address
    public ServerResponse add(Integer userId, Shipping shipping){
        //因为spring 对象绑定没有设置id
        shipping.setUserId(userId);
        //shippingMapper中的insert能把myBatis自动生成的key赋给shipping，所以我们直接get就行
        int rowCount = shippingMapper.insert(shipping);
        if(rowCount > 0){
            Map result = Maps.newHashMap();
            result.put("shippingId",shipping.getId());
            return ServerResponse.creatBySuccess("Add new address success",result);
        }
        return ServerResponse.creatByErrorMessage("Add new address fail");
    }

    //delete address
    public ServerResponse del(Integer userId, Integer shippingId){
        //要防止横向越权，因为登录状态的话可以传别人的shippingid
        int resultCount = shippingMapper.deleteByShippingIdUserId(userId,shippingId);
        if(resultCount>0){
            return ServerResponse.creatBySuccessMessage("Delete address success");
        }
        return ServerResponse.creatByErrorMessage("Delete address fail");
    }

    //update
    public ServerResponse update(Integer userId, Shipping shipping){
       //防止横向越权，把shipping中的userId设置成自己的ID
        shipping.setUserId(userId);
        //shippingMapper中的insert能把myBatis自动生成的key赋给shipping，所以我们直接get就行
        int rowCount = shippingMapper.updateByShipping(shipping);
        if(rowCount > 0){
            return ServerResponse.creatBySuccessMessage("Update address success");
        }
        return ServerResponse.creatByErrorMessage("Update address fail");
    }

    public ServerResponse<Shipping> select(Integer userId, Integer shippingId){
        Shipping shipping = shippingMapper.selectByShippingIdUserId(userId,shippingId);
        if(shipping == null){
            return ServerResponse.creatByErrorMessage("Can't find this address");
        }
        return ServerResponse.creatBySuccess(shipping);
    }

    //list
    public ServerResponse<PageInfo> list(Integer userId , int pageNum, int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<Shipping> shippingList = shippingMapper.selectByUserId(userId);
        PageInfo pageInfo = new PageInfo(shippingList);
        return ServerResponse.creatBySuccess(pageInfo);
    }

}
