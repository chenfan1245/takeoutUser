package com.cykj.service.Impl;

import com.cykj.bean.Tblcomment;
import com.cykj.mapper.TblcommentMapper;
import com.cykj.service.TblcommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TblcommentServiceImpl implements TblcommentService {
    @Autowired
    private TblcommentMapper commentMapper;

    // 查询待评价的订单的店铺信息
    @Override
    public List<Tblcomment> findNoCommentShop(long userid) {
        return commentMapper.findNoCommentShop(userid);
    }

    // 查询待评价的订单的商品信息
    @Override
    public List<Tblcomment> findNoCommentGoods(long userid, long orderid) {
        return commentMapper.findNoCommentGoods(userid, orderid);
    }

    // 已评价的订单信息和评价内容
    @Override
    public List<Tblcomment> findComment(long userid) {
        return commentMapper.findComment(userid);
    }
}
