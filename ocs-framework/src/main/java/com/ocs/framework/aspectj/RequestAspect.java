package com.ocs.framework.aspectj;


import cn.hutool.extra.servlet.ServletUtil;
import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ocs.common.annotation.SimpleRequest;
import com.ocs.common.core.domain.entity.SysUser;
import com.ocs.common.core.domain.model.LoginUser;
import com.ocs.common.exception.ServiceException;
import com.ocs.framework.web.service.TokenService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;

/**
 * @desc 控制台日志切面
 */
@Aspect
@Component
public class RequestAspect {

    private Logger logger = LoggerFactory.getLogger(getClass().getName());

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private TokenService tokenService;


    @Pointcut("execution(public * com.ocs.*.controller..*.*(..))")
    public void toLog() {
    }

    @Before("toLog()")
    public void deBefore(JoinPoint joinPoint) throws Throwable {
        // 接收到请求，记录请求内容
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        logger.info("-------------------用户发起请求-----------------");
        // 记录下请求内容
        String reqUrl = request.getRequestURL().toString();
        logger.info("HTTP请求地址 : " + reqUrl);
        logger.info("HTTP请求方法 : " + request.getMethod());
        // 如果是表单，参数值是普通键值对。如果是application/json，则request.getParameter是取不到的。
        logger.info("HTTP请求类型 : " + request.getHeader("Content-Type"));
        String remoteAddr = request.getRemoteAddr();
        logger.info("请求IP地址 : " + remoteAddr);
        String methodName = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();
        logger.info("业务请求方法 : " + methodName);

        String params = request.getMethod().equals("GET") || request.getMethod().equals("DELETE") ? objectMapper.writeValueAsString(paramOfGet(request)) : paramOfPost(request);
        logger.info("HTTP请求参数:{}", params);
        LoginUser loginUser = tokenService.getLoginUser(request);
        SysUser currentUser = loginUser == null ? null : loginUser.getUser();
        if (currentUser != null) {
            logger.info("业务操作人员 : " + currentUser.getUserName());
            logger.info("业务操作人员名称 : " + currentUser.getNickName());
        }

    }

    @Around("toLog()")
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
        Method method = signature.getMethod();
        SimpleRequest annotation = method.getAnnotation(SimpleRequest.class);
        Object result = null;
        if (annotation != null) {
            Object[] requestBodyParams = getRequestBodyParams(proceedingJoinPoint);
            result = requestBodyParams != null ? proceedingJoinPoint.proceed(requestBodyParams) : proceedingJoinPoint.proceed();
        } else {
            result = proceedingJoinPoint.proceed();
        }

        long millis = System.currentTimeMillis() - start;
        logger.info("共花费:{}毫秒", millis);
        if (millis > 300) {
            String methodName = proceedingJoinPoint.getSignature().getName();
            logger.warn("方法名称:{},花费:{}毫秒", methodName, millis);
        }
        return result;
    }

    @AfterReturning(returning = "ret", pointcut = "toLog()")
    public void doAfterReturning(Object ret) throws Throwable {
        // 处理完请求，返回内容
        logger.info("方法的返回值 : " + JSON.toJSONString(ret));
    }

    // 后置异常通知
    @AfterThrowing(throwing = "ex", pointcut = "toLog()")
    public void throwss(JoinPoint jp, Exception ex) {
        logger.error("方法异常时执行.....", ex);
    }

    // 后置最终通知,final增强，不管是抛出异常或者正常退出都会执行
    @After("toLog()")
    public void after(JoinPoint jp) {

    }

    private Map<String, Object> paramOfGet(HttpServletRequest request) {
        return WebUtils.getParametersStartingWith(request, null);
    }

    private String paramOfPost(HttpServletRequest request) {
        return request.getRequestURL().toString().contains("/upload") ? "" : ServletUtil.getBody(request);
    }

    public Object[] getRequestBodyParams(ProceedingJoinPoint proceedingJoinPoint) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        if (request.getMethod().equals("POST")) {
            String paramStr = paramOfPost(request);
            try {
                Map<String, Object> map = objectMapper.readValue(paramStr, Map.class);
                // 获取方法，此处可将signature强转为MethodSignature
                MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
                Method method = signature.getMethod();
                Parameter[] parameters = method.getParameters();
                Object[] data = new Object[parameters.length];
                for (int i = 0; i < parameters.length; i++) {
                    String name = parameters[i].getName();
                    data[i] = map.get(name);
                    if (data[i] == null) {
                        logger.error("请求参数:{}", objectMapper.writeValueAsString(map));
                        throw new ServiceException("参数名:" + name + ",缺少参数值");
                    }
                }
                return data;
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

}
