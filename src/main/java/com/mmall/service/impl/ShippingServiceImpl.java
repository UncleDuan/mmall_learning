package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.ShippingMapper;
import com.mmall.pojo.Shipping;
import com.mmall.service.IShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by ionolab-DP on 2018/12/25.
 */
@Service("iShippingService")
public class ShippingServiceImpl implements IShippingService {

    @Autowired
    private ShippingMapper shippingMapper;

    @Override
    public ServerResponse<Shipping> add(Integer userId, Shipping shipping){
        shipping.setUserId(userId);
        int rowCount=shippingMapper.insert(shipping);
        if (rowCount>0){
            Map result= Maps.newHashMap();
            result.put("shippingId",shipping.getId());
            return ServerResponse.createBySuccess("Add address succeeded",shipping);
        }
        return ServerResponse.createByErrorMessage("Add address failed");
    }

    public ServerResponse<String> del(Integer userId, Integer shippingId){

        int rowCount=shippingMapper.deleteByPrimaryKey(shippingId);
        if (rowCount>0){
            return ServerResponse.createBySuccess("Delete address succeeded");
        }
        return ServerResponse.createByErrorMessage("Delete address failed");
    }


    public ServerResponse<String> update(Integer userId, Shipping shipping){
        shipping.setUserId(userId);
        int rowCount=shippingMapper.updateByShipping(shipping);
        if (rowCount>0){

            return ServerResponse.createBySuccess("Update address succeeded");
        }
        return ServerResponse.createByErrorMessage("Update address failed");
    }


    public ServerResponse<Shipping> select(Integer userId, Integer shippingId){
        Shipping shipping=shippingMapper.selectByShippingIdAndUserId(userId,shippingId);
        if (shipping==null){
            return ServerResponse.createByErrorMessage("Query address failed");
        }
        return ServerResponse.createBySuccess("Query address succeeded",shipping);
    }

    public ServerResponse<PageInfo> list(Integer userId, int pageNum, int pageSize){

        PageHelper.startPage(pageNum,pageSize);
        List<Shipping> shippingList=shippingMapper.selectByUserId(userId);
        PageInfo pageInfo=new PageInfo(shippingList);
        return ServerResponse.createBySuccess(pageInfo);

    }
}
