package com.sky.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    public  static  final  String WX_LOGIN = "https://api.weixin.qq.com/sns/jscode2session";
    @Autowired
    private  WeChatProperties weChatProperties;
    @Autowired
    private UserMapper userMapper;
    /**
     * 微信登录
     * @param userLoginDTO
     * @return  User
     */
    @Override
    public User login(UserLoginDTO userLoginDTO) {

        String openid = getOpenId(userLoginDTO.getCode());
        //判断openid是否为空，如果为空，说明登录失败，抛出异常
        if(openid == null){

            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }
        //根据openid查询用户是否存在，如果不存在，保存用户信息
        User user = userMapper.getByOpenId(openid);

        if(user == null){
           user = User.builder().openid(openid).createTime(LocalDateTime.now()).build();
           userMapper.insert(user);
        }
        //返回用户信息
        return user;

    }
    /**
     * 获取openid
     * @param code
     * @return  String
     */

    private  String getOpenId(String code){
        Map<String,String> map = new HashMap<>();
        map.put("appid",weChatProperties.getAppid());
        map.put("secret",weChatProperties.getSecret());
        map.put("js_code",code);
        map.put("grant_type","authorization_code");
        String json = HttpClientUtil.doGet(WX_LOGIN, map);

        JSONObject jsonObject = JSONObject.parseObject(json);
        String openid = jsonObject.getString("openid");
        return openid;
    }
}
