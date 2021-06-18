package kg666.service;

import kg666.data.UserMapper;
import kg666.po.User;
import kg666.vo.ResponseVO;
import kg666.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class UserService {
    final static String USERNAME_REPEAT = "Username repeats";
    final static String USER_NOT_EXIST = "Username not exits";
    final static String PASSWORD_WRONG = "Password is wrong";

    @Autowired
    UserMapper userMapper;

    /**
     * Register for user
     * User form, include username and password @param userVO
     * Success or not @return
     */
    public ResponseVO register(UserVO userVO){
        MessageDigest md5;
        try {
            User test = userMapper.getUser(userVO.getName());
            if (test == null){
                md5 = MessageDigest.getInstance("MD5");
                BigInteger digest = new BigInteger(md5.digest(userVO.getPassword().getBytes(StandardCharsets.UTF_8)));
                userMapper.insertUser(userVO.getName(), digest.toString(16));
                return ResponseVO.buildSuccess();
            } else{
                return ResponseVO.buildFailure(USERNAME_REPEAT);
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();//
            return ResponseVO.buildFailure("Encrypt Error");
        }
    }

    /**
     * User login
     * User form include username and password @param userVO
     * Success or not and uid for frontend to save@return
     */
    public  ResponseVO login(UserVO userVO){
        MessageDigest md5;
        try {
            User test = userMapper.getUser(userVO.getName());
            if (test != null) {
                md5 = MessageDigest.getInstance("MD5");
                BigInteger digest = new BigInteger(md5.digest(userVO.getPassword().getBytes(StandardCharsets.UTF_8)));
                String input = digest.toString(16);
                if (test.getPassword() .equals(input)) return ResponseVO.buildSuccess(test.getUid());
                else return ResponseVO.buildFailure(PASSWORD_WRONG);
            } else{
                return ResponseVO.buildFailure(USER_NOT_EXIST);
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return ResponseVO.buildFailure("Encrypt Error");
        }
    }
}
