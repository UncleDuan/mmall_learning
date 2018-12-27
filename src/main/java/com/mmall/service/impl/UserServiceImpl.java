package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Created by ionolab-DP on 2018/12/18.
 */
@Service("iUserService")
public class UserServiceImpl implements IUserService {
    @Autowired
    private UserMapper userMapper;
    @Override
    public ServerResponse<User> login(String username, String password) {

        int resultCount=userMapper.checkUsername(username);
        if(resultCount==0){
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        //// TODO: 2018/12/18 密码登录MD5
        String md5password=MD5Util.MD5EncodeUtf8(password);

        User user=userMapper.selectLogin(username,md5password);
        if(user==null){
            return ServerResponse.createByErrorMessage("密码错误");

        }

        user.setPassword(StringUtils.EMPTY);

        return ServerResponse.createBySuccess("登录成功", user);
    }

    public ServerResponse<String> register(User user){

        ServerResponse validResponse=this.checkValid(user.getUsername(),Const.USERNAME);
        if (!validResponse.isSuccess()){
            return validResponse;

        }
        validResponse=this.checkValid(user.getEmail(),Const.EMAIL);
        if(!validResponse.isSuccess()){
            return validResponse;
        }
        user.setRole(Const.Role.ROLE_CUSTOMER);
        //MD5加密
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));

        int resultCount=userMapper.insert(user);
        if(resultCount==0)
            return ServerResponse.createByErrorMessage("Registeration failed");
        return ServerResponse.createBySuccessMessage("Registeration success");


    }

    public ServerResponse<String> checkValid(String str, String type){
        //isNotBlank()字符串为空格时返回false
        //isNotEmpty()字符串为空格时返回true

        if(StringUtils.isNoneBlank(type)){
            if (Const.USERNAME.equals(type)) {
                int resultCount = userMapper.checkUsername(str);
                if (resultCount > 0) {
                    return ServerResponse.createByErrorMessage("Username already exists");
                }
            }
            if (Const.EMAIL.equals(type)){
                int resultCount = userMapper.checkEmail(str);
                if (resultCount > 0) {
                    return ServerResponse.createByErrorMessage("Email already exists");
                }
            }
        }else {
            ServerResponse.createByErrorMessage("Parameter error");
        }
        return ServerResponse.createBySuccessMessage("Validation success");

    }

    public ServerResponse selectQuestion(String username){
        ServerResponse validResponse=this.checkValid(username,Const.USERNAME);
        if(validResponse.isSuccess()){
            //用户已经存在
            return ServerResponse.createByErrorMessage("User does not exist");
        }
        //查找它的问题
        String question=userMapper.selectQusetionByUsername(username);
        if(!StringUtils.isNoneBlank(question)){
            return ServerResponse.createBySuccess(question);
        }
        return ServerResponse.createByErrorMessage("Question is null");

    }
    public ServerResponse<String> checkAnswer(String username, String question, String answer){
        int resultCount=userMapper.checkAnswer(username,question,answer);
        if (resultCount>0){
            //答案正确
            //唯一性Token
            String forgetToken = UUID.randomUUID().toString();
            TokenCache.setKey(TokenCache.TOKEN_PREFIX+username,forgetToken);
            return ServerResponse.createBySuccess(forgetToken);

        }
        return ServerResponse.createByErrorMessage("Wrong answer");
    }

    public ServerResponse<String> forgetResetPassword(String username,String passwordNew,String forgetToken){
        if(StringUtils.isBlank(forgetToken)){
            return ServerResponse.createByErrorMessage("Token in need, reset failed");

        }
        ServerResponse validResponse=this.checkValid(username,Const.USERNAME);
        if (validResponse.isSuccess()){
            return ServerResponse.createByErrorMessage("User does not exist");
        }
        String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX+username);
        //校验从TokenCache中得到的Token
        if (StringUtils.isBlank(token)){
            return ServerResponse.createByErrorMessage("Token is invalid or expired");

        }

        if(StringUtils.equals(forgetToken,token)){
            String md5PasswordNew=MD5Util.MD5EncodeUtf8(passwordNew);
            int rowCount=userMapper.updatePasswordByUsername(username,md5PasswordNew);
            if (rowCount>0){
                return ServerResponse.createBySuccessMessage("Reset password success");
            }else {
                return ServerResponse.createByErrorMessage("Reset password failed, please try again later");
            }

        }
        return ServerResponse.createByErrorMessage("Reset password failed");
    }

    public ServerResponse<String> resetPassword(String passwordOld, String passwordNew, User user){

        //防止横向越权，校验用户旧密码
        int resultCount=userMapper.checkPassword(MD5Util.MD5EncodeUtf8(passwordOld),user.getId());
        if(resultCount==0){
            return ServerResponse.createByErrorMessage("Old password is not correct");
        }
        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        int updateCount=userMapper.updateByPrimaryKeySelective(user);
        if(updateCount>0){
            return ServerResponse.createBySuccessMessage("Reset password success");
        }
        return ServerResponse.createByErrorMessage("Reset password failed");


    }

    public ServerResponse<User> updateInformation(User user){
        //username不能被更新
        //email也要进行一个校验，校验新的email是不是已经存在。
        int resultCount=userMapper.checkPasswordByUserId(user.getEmail(),user.getId());
        if (resultCount>0){
            return ServerResponse.createByErrorMessage("Email already exists, please try again with another email");
        }
        User updateUser=new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());
        int updateCount = userMapper.updateByPrimaryKeySelective(updateUser);
        if (updateCount>0){
            return ServerResponse.createBySuccess("Update information success",updateUser);

        }
        return ServerResponse.createByErrorMessage("Update information failed, please try later");
    }

    public ServerResponse<User> getInformation(Integer userId){
        User user=userMapper.selectByPrimaryKey(userId);
        if (user==null){
            return ServerResponse.createByErrorMessage("Can't find current user");
        }
//        返回用户信息给前台，但是去除密码信息。
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);
    }

    public ServerResponse checkAdminRole(User user){
        if(user!=null && user.getRole().intValue()==Const.Role.ROLE_ADMIN){
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }
}
