package cn.edu.hstc.controller;

import cn.edu.hstc.util.verify.util.IVerifyCodeGen;
import cn.edu.hstc.util.verify.util.IVerifyCodeGenImpl;
import cn.edu.hstc.util.verify.util.VerifyCode;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 验证码调用接口
 */
@Controller
@RequestMapping("/verify")
public class VerifyController extends BaseController {
    @GetMapping("/code.jpg")
    public void verifyCode(HttpServletRequest request, HttpServletResponse response) {
        IVerifyCodeGen iVerifyCodeGen = new IVerifyCodeGenImpl();
        try {
            //设置长宽
            VerifyCode verifyCode = iVerifyCodeGen.generate(80, 28);
            String code = verifyCode.getCode();
            logger.info("获取验证码"+code);
            //将VerifyCode绑定session
            request.getSession().setAttribute("VerifyCode", code);
            //设置响应头
            response.setHeader("Pragma", "no-cache");
            //设置响应头
            response.setHeader("Cache-Control", "no-cache");
            //在代理服务器端防止缓冲
            response.setDateHeader("Expires", 0);
            //设置响应内容类型
            response.setContentType("image/jpeg");
            response.getOutputStream().write(verifyCode.getImgBytes());
            response.getOutputStream().flush();
        } catch (IOException e) {
            logger.info("获取验证码异常", e);
        }
    }
}
