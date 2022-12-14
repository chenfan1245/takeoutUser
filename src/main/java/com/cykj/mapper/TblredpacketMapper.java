package com.cykj.mapper;

import com.cykj.bean.Tblgoods;
import com.cykj.bean.Tblredpacket;
import com.cykj.bean.Tblshop;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TblredpacketMapper {
    // 查询用户拥有的未使用的红包
    List<Tblredpacket> findUserRedPacket(@Param("userid")long userid);
    // 根据系统日期去修改“我的红包状态”
    boolean updateDate(@Param("nowTime")String nowTime);

}
