package com.cykj.mapper;

import com.cykj.bean.Tblcomment;
import com.cykj.bean.Tblgoods;
import com.cykj.bean.Tblshop;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
public interface TblcommentMapper {
    // 查询待评价的订单的店铺信息
    List<Tblcomment> findNoCommentShop(@Param("userid")long userid);
    // 查询待评价的订单的商品信息
    List<Tblcomment> findNoCommentGoods(@Param("userid")long userid,
                                        @Param("orderid")long orderid);
    // 已评价的订单信息和评价内容
    List<Tblcomment> findComment(@Param("userid")long userid,
                                 @Param("roleid")long roleid);

    // 用户评论
    int sendComment(@Param("orderid")long orderid,
                    @Param("userid")long userid,
                    @Param("roleid")long roleid,
                    @Param("commentcontent")String commentcontent,
                    @Param("commentscore")long commentscore);
}
