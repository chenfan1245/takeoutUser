package com.cykj.web;

import com.alibaba.fastjson.JSON;
import com.cykj.bean.*;
import com.cykj.service.*;
import com.cykj.utils.SMSUtil;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
public class UserController {
    @Autowired
    HttpSession session;
    @Autowired
    TbluserSeriver tbluserSeriver;
    @Autowired
    TblgoodsService tblgoodsService;
    @Autowired
    TblredpacketService tblredpacketService;
    @Autowired
    TblcommentService tblcommentService;
    @Autowired
    TblshoppingcarService tblshoppingcarService;

    /* 查询购物车显示的内容 */
    @ApiOperation(value = "findShoppingcar",notes = "查询购物车显示的内容，传回店铺名称列表、规格内容列表、商品列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userid",value = "登录的用户的id"),
            @ApiImplicitParam(name = "specidList",value = "加入购物车时所选择的那些规格id")
    })
    @RequestMapping(value="/findShoppingcar",produces = { "text/html;charset=UTF-8;", "application/json;charset=UTF-8;" })
    public String findShoppingcar(long userid, List<Long> specidList){
        System.out.println("------显示购物车信息------");
        List<String> shopNameList = tblshoppingcarService.findCarShop(userid);  // 店铺名称列表
        List<Tblgoodsspec> specnNameList = new ArrayList<>(); // 规格内容列表
        List<Tblshoppingcard> goodsList = new ArrayList<>();    // 商品列表
        List<Long> goodsidList = new ArrayList<>();     // 商品id列表
        goodsList = tblshoppingcarService.findCarGoods(userid, shopNameList);
        for (Tblshoppingcard tblshoppingcard : goodsList) {
            goodsidList.add(tblshoppingcard.getGoodsid());
            specnNameList = tblshoppingcarService.findCarGoodsSpec(userid, goodsidList, specidList);
        }

        Map<String, Object> map = new HashMap<>();
        map.put("shopNameList",shopNameList);   // 店铺名称列表
        map.put("specnNameList",specnNameList); // 含用户id、商品id、商品名、规格内容
        map.put("goodsList", goodsList);        // 商品列表
        String json = JSON.toJSONString(map);
        return json;
    }

    /* 点击加号或减号或输入数值，修改购物车商品数量 */
    @ApiOperation(value = "updateShoppingcarNum",notes = "更新购物车内商品的购买数量，传回1成功 2失败 0购物车购买数量>商品库存")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "bugnum",value = "加入到购物车的商品的购买数量"),
            @ApiImplicitParam(name = "goodsid",value = "购物车中具体某个商品的商品id"),
            @ApiImplicitParam(name = "userid",value = "该购物车是用户id的")
    })
    @RequestMapping("/updateShoppingcarNum")
    public String updateShoppingcarNum(long bugnum, long goodsid, long userid){
        int goodsnum = tblshoppingcarService.findGoodsNum(goodsid);
        if (bugnum < goodsnum) {
            boolean flag = tblshoppingcarService.updateShoppingcarNum(bugnum, goodsid, userid);
            if (flag) {
                return "1";     // 购物车内商品数量更新成功
            } else {
                return "2";     // 购物车内商品数量更新失败
            }
        } else {
            return "0";         // 购物车添加的购买数量大于商品的库存，应不可再点击添加按钮或弹提示
        }
    }

    /* 点击加入购物车，将商家id、商品id和用户id加入到购物车中（默认购买数量为1） */
    @ApiOperation(value = "addShoppingcar",notes = "将商品添加到购物车的新增方法，传回1成功 2失败")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shopid",value = "加入到购物车的店铺的商家id"),
            @ApiImplicitParam(name = "goodsid",value = "加入到购物车的商品的商品id"),
            @ApiImplicitParam(name = "userid",value = "该购物车是用户id的")
    })
    @RequestMapping("/addShoppingcar")
    public String addShoppingcar(long shopid, long goodsid, long userid){
        boolean flag = tblshoppingcarService.addShoppingcar(shopid,goodsid,userid);
        if (flag) {
            return "1";     // 加入购物车成功
        } else {
            return "2";     // 加入购物车失败
        }
    }

    /* 查询已评价的订单信息和评价内容 */
    @ApiOperation(value = "findComment",notes = "查询待评价的订单信息，传回已评价订单信息和所有评价内容的列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userid",value = "登录的用户的id")
    })
    @RequestMapping(value="/findComment",produces = { "text/html;charset=UTF-8;", "application/json;charset=UTF-8;" })
    public String findComment(long userid){
        System.out.println("------显示已评价的订单信息------");
        List<Tblcomment> commentList = tblcommentService.findComment(userid); // 已评价的订单信息和所有评价内容
        String json = JSON.toJSONString(commentList);
        return json;
    }

    /* 查询待评价的订单信息 */
    @ApiOperation(value = "findNoComment",notes = "查询待评价的订单信息，传回待评价的订单和其商家信息列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userid",value = "登录的用户的id")
    })
    @RequestMapping(value="/findNoComment",produces = { "text/html;charset=UTF-8;", "application/json;charset=UTF-8;" })
    public String findNoComment(long userid){
        System.out.println("------显示待评价的订单信息------");
        List<Tblcomment> noCommentList = tblcommentService.findNoCommentShop(userid);   // 待评价的订单的商家信息
        for (Tblcomment tblcomment : noCommentList) {
            long orderid = tblcomment.getOrderid();     // 获取待评价的订单的订单id
            List<Tblcomment> list = tblcommentService.findNoCommentGoods(userid,orderid);   // 该订单的商品信息
            tblcomment.setGoodsList(list);  //
        }
        String json = JSON.toJSONString(noCommentList);
        return json;
    }

    /* 查询【我的红包】 */
    @ApiOperation(value = "findUserRedPacket",notes = "查询【我的红包】，传回用户拥有的红包的列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userid",value = "登录的用户的id")
    })
    @RequestMapping(value="/findUserRedPacket",produces = { "text/html;charset=UTF-8;", "application/json;charset=UTF-8;" })
    public String findUserRedPacket(long userid){
        System.out.println("------显示我的红包------");
        List<Tblredpacket> redpacketList = tblredpacketService.findUserRedPacket(userid);
        String json = JSON.toJSONString(redpacketList);
        return json;
    }

    /* 点击商品，查看商品详细信息 */
    @ApiOperation(value = "findGoods",notes = "点击商品，查看商品详细信息，传回商品详细信息列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "goodsid",value = "被点击的商品的商品id")
    })
    @RequestMapping(value="/findGoods",produces = { "text/html;charset=UTF-8;", "application/json;charset=UTF-8;" })
    public String findGoods(long goodsid){
        System.out.println("------显示商品详细信息------");
        Tblgoods tblgoods = tblgoodsService.findGoods(goodsid);
        String json = JSON.toJSONString(tblgoods);
        return json;
    }

    /* 点餐界面搜索框搜索商品名，点击按钮时执行的查询方法 */
    @ApiOperation(value = "findSearchGoods",notes = "点餐界面搜索框搜索商品名，点击按钮时执行的查询方法，传回对应的商品信息的列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shopid",value = "点击店铺进入点餐界面，该店铺的id"),
            @ApiImplicitParam(name = "goodsname",value = "搜索框输入的要搜索的商品名")
    })
    @RequestMapping(value="/findSearchGoods",produces = { "text/html;charset=UTF-8;", "application/json;charset=UTF-8;" })
    public String findSearchGoods(long shopid, String goodsname){
        System.out.println("------显示根据商品名查询出的商品的信息------");
        List<Tblgoods> searchGoodsList = tblgoodsService.findSearchGoods(shopid,goodsname);     // 搜索框搜索商品名找到对应的商品信息
        String json = JSON.toJSONString(searchGoodsList);
        return json;
    }

    /* 点击商铺查询该店的商品 */
    @ApiOperation(value = "findAllShopGoods",notes = "点击店铺进入点餐界面，显示有关的内容的信息，传回店铺信息类、招牌菜列表、左侧菜单栏内容列表、所有商品列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shopid",value = "点击店铺进入点餐界面，该店铺的id"),
            @ApiImplicitParam(name = "shopgoodstypeidid",value = "点击店左侧菜单栏，获得shopgoodstypeidid")
    })
    @RequestMapping(value="/findAllShopGoods",produces = { "text/html;charset=UTF-8;", "application/json;charset=UTF-8;" })
    public String findAllShopGoods(long shopid,long shopgoodstypeidid){
        System.out.println("------点击商铺查询该店的商品------");
        Tblshop tblshop = tblgoodsService.findShop(shopid);     // 店铺信息
        List<Tblshop> specialityList = tblgoodsService.findSpeciality(shopid);  // 该店铺的招牌菜
        String shopName = tblshop.getShopname();    // 店铺名称
        Tblshop shopgoodstype = tblgoodsService.findShopgoodstypeid(shopName);  // 查询找到shopgoodstypeid
        long shopgoodstypeidID = shopgoodstype.getShopgoodstypeid();
        List<Tblshop> shopGoodsTypeList = tblgoodsService.findShopGoodsType(shopgoodstypeidID); // 左侧菜单栏的内容
        List<Tblgoods> goodsList = tblgoodsService.findAllGoods(shopid,shopgoodstypeidid);         // 该店铺的所有商品

        Map<String, Object> map = new HashMap<>();
        String json = JSON.toJSONString(map);
        map.put("tblshop",tblshop);                         // 店铺信息
        map.put("specialityList", specialityList);          // 招牌菜
        map.put("shopGoodsTypeList", shopGoodsTypeList);    // 左侧菜单栏
        map.put("goodsList", goodsList);                    // 所有商品
        return json;
    }

    /* 首页显示所有店铺（根据【店铺名】和【商品名】和【商品类型】模糊查询） */
    @ApiOperation(value = "findAllShop",notes = "首页显示所有店铺（根据【店铺名】和【商品名】和【商品类型】模糊查询），传回所有店铺列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shopname",value = "店铺名"),
            @ApiImplicitParam(name = "goodsname",value = "商品名"),
            @ApiImplicitParam(name = "typeid",value = "商品类型id")
    })
    @RequestMapping(value="/findAllShop",produces = { "text/html;charset=UTF-8;", "application/json;charset=UTF-8;" })
    public String findAllShop(String shopname,String goodsname,long typeid){
        System.out.println("------首页显示所有店铺------");
        for (Tblshop tblshop : tblgoodsService.findAllShopGoods(shopname,goodsname,typeid)) {
            long shopid = tblshop.getShopid();
            List<Double> scoreList = tblgoodsService.findScore(shopid);
            Double scoreAvg = 5.0;
            if (!scoreList.isEmpty()) {
                scoreAvg = scoreList.stream().collect(Collectors.averagingDouble(Double::doubleValue));
            }
            boolean flag1 = tblgoodsService.updateScore(scoreAvg,shopid);
            long salesSum = tblgoodsService.findSales(shopid);
            boolean flag2 = tblgoodsService.updateSales(salesSum,shopid);
        }
        List<Tblshop> shopsList = tblgoodsService.findAllShopGoods(shopname,goodsname,typeid);
        String json = JSON.toJSONString(shopsList);
        return json;
    }

    /* 登录 */
    @ApiOperation(value = "login",notes = "登录方法")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "usertel",value = "登录用户电话"),
            @ApiImplicitParam(name = "userpwd",value = "登录用户密码")
    })
    @RequestMapping("/login")
    public String login(String usertel,String userpwd){
        Tbluser tbluser = tbluserSeriver.login(usertel, userpwd);
        if (tbluser != null){
            return "1";
        }else {
            return "2";
        }
    }

    // 点击发送验证码
    @RequestMapping("/sendMs")
    public String sendMs (HttpServletRequest request, String usertel){
        if(usertel!=null&&!usertel.equals("")){
            String s = SMSUtil.sendSMS(request,usertel);
            JSONObject json = (JSONObject)request.getSession().getAttribute("MsCode");
            System.out.println(json.getString("Code"));
            return json.getString("Code");
        }else{
            return "error";
        }
    }

    // 点击注册
    @RequestMapping("/enroll")
    public String register( String usertel, String userpwd) {
        /* 判断账号是否重复 */
        if (tbluserSeriver.checkTel(usertel)){
            return "3";
        }else {
            // 将用户信息存入数据库、这里省略
            int num = tbluserSeriver.enroll(usertel,userpwd);
            if (num > 0){
                return "1";
            }else {
                return "2";
            }
        }
    }
    /* 忘记密码 */
    @RequestMapping("/setPwd")
    public String setPwd( String usertel, String userpwd){
        System.out.println(usertel);
        /* 判断账号是否重复 */
        if (!tbluserSeriver.checkTel(usertel)){
            return "3";
        }else {
            // 将用户信息存入数据库、这里省略
            int num = tbluserSeriver.setPwd(usertel,userpwd);
            if (num > 0){
                return "1";
            }else {
                return "2";
            }
        }
    }

}
