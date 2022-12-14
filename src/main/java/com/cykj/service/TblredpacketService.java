package com.cykj.service;

import com.cykj.bean.Tblredpacket;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TblredpacketService {
    // 查询用户拥有的未使用的红包
    List<Tblredpacket> findUserRedPacket(long userid);
    // 根据系统日期去修改“我的红包状态”
    boolean updateDate(String nowTime);

}
