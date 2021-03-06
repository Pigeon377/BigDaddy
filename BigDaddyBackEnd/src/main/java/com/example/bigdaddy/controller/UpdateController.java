package com.example.bigdaddy.controller;


import com.example.bigdaddy.model.User;
import com.example.bigdaddy.repository.UserRepository;
import com.example.bigdaddy.util.PasswordEncryptor;
import com.example.bigdaddy.util.response.BaseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RequestMapping("/auth/update")
@RestController
public class UpdateController {

    @Autowired
    UserRepository userRepository;

    @PostMapping
    @ResponseBody
    public BaseResponse update(
            @RequestAttribute("mailbox") String mailbox,
            @RequestParam(value = "password",    required = true)  String password,
            @RequestParam(value = "new_password",required = false) String newPassword){
        try{
            Optional<User> optionalUser = userRepository.findById(mailbox);
            if(optionalUser.isPresent()){
                // 用户存在
                User user = optionalUser.get();
                if (PasswordEncryptor.checkPasswordHash(password, user.getPassword())){
                    // 用户密码正确
                    // 对应字段存在就更新
                    if (newPassword!=null){
                        user.setPassword(PasswordEncryptor.generatorPasswordHash(newPassword));
                    }
                    userRepository.saveAndFlush(user);
                    return new BaseResponse(0,"Succeed");
                }else{
                    return new BaseResponse(0,"PasswordWrong");
                }
            }else {
                return new BaseResponse(0,"FakeNews!");
                // 用户不存在 不过理论上不会有这个问题吧
            }
        }catch(Exception e){
            e.printStackTrace();
            return new BaseResponse(0,"InvalidArgument");
        }
    }

}
