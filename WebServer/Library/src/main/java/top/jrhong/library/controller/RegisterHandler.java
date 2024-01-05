package top.jrhong.library.controller;

import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.jrhong.library.entitys.User;
import top.jrhong.library.repository.UserRepository;

import java.util.Optional;
import java.util.Random;

/**
 * 注册控制器
 * @author hongjr03
 */
@RestController
@RequestMapping("/register")
public class RegisterHandler {

    private String code;

    /**
     * 用户数据库操作接口
     */
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmailMessageUtil emailMessageUtil;

    // 生成验证码
    private String generateCode() {
        // 生成随机数
        Random random = new Random();
        // 生成验证码
        String code = "";
        for (int i = 0; i < 6; i++) {
            code += random.nextInt(10);
        }
        return code;
    }

    /**
     * 验证码发送
     * @param regForm 注册表单
     *                0 - 验证码错误
     *                1 - 验证码发送成功
     * @return 验证码验证结果
     */
    @PostMapping("/sendVerificationCode")
    public JSONObject sendVerificationCode(@RequestBody User regForm) {
        // 发送验证码
        code = generateCode();
        // 发送邮件
//        EmailMessageUtil emailMessageUtil = new EmailMessageUtil();
        String ret = emailMessageUtil.sendMessage(regForm.getEmail(),code);
        // 创建验证码验证返回信息
        JSONObject verificationCodeMessage = new JSONObject();
        verificationCodeMessage.put("message", "verificationCode");
        verificationCodeMessage.put("statusCode", 0);
        // 验证码发送成功
        if (ret.equals("send success")) {
            verificationCodeMessage.replace("statusCode", 1);
        }
        return verificationCodeMessage;
    }

    /**
     * 管理员注册
     * @param user 用户信息
     * @return 注册信息
     */
    @PostMapping("/admin")
    public JSONObject adminRegister(@RequestBody User user) {
        return register(user, "admin");
    }

    /**
     * 用户注册
     * @param user 用户信息
     * @return 注册信息
     */
    @PostMapping("/user")
    public JSONObject userRegister(@RequestBody User user) {
        return register(user, "user");
    }

    /**
     * 用户注册
     * 0 - 用户已存在
     * 1 - 用户不存在但验证码无效
     * 2 - 成功注册
     * @param user 用户表单
     * @return 注册状态
     */
    public JSONObject register(User user, String userGroups) {
        // 封装表单
        // 搜索用户
        // 封装表单
        Example<User> userExample = Example.of(user);
        // 搜索用户
        Optional<User> userOptional = userRepository.findOne(userExample);

        // 创建注册返回信息
        JSONObject registerInfo = new JSONObject();
        // 用户已存在
        if (userOptional.isPresent()) {
            registerInfo.put("statusCode", 0);
            registerInfo.put("message", "用户已存在");
        } else {
            // 验证验证码是否有效
            // 打印传入的验证码
            System.out.println(code);
            // 打印用户输入的验证码
            System.out.println(user.getCode());
            if (!user.getCode().equals(code)) {
                registerInfo.put("statusCode", 1);
                registerInfo.put("message", "验证码无效");
                return registerInfo;
            }
            // 设置用户组
            user.setGroups(userGroups);
            // 设置用户状态
            user.setState(1);
            // 设置用户借书数量
            user.setBookCount(10);
            // 保存用户
//            String idCard = userRepository.findAll().
//            Integer idCard_int = Integer.parseInt(idCard)+1;
//            idCard = Integer.toString(idCard_int);
//            user.setIdCard(idCard);
            user.setIdCard("18002020");
            String identity = "";
            if (userGroups.equals("user")) {
                identity = "用户";
            } else {
                identity = "管理员";
            }
            user.setIdentity(identity);

            user.setName(user.getUsername());
            user.setPhone("");

            // 加密密码
            String encryptedPassword = SecureUtil.md5(user.getPassword());
            user.setPassword(encryptedPassword);

            userRepository.save(user);
            registerInfo.put("statusCode", 2);
            registerInfo.put("message", "注册成功");
        }
        return registerInfo;
    }
}

// 472811