package com.ocs.common.filter;

import cn.hutool.core.util.IdUtil;
import org.slf4j.MDC;

import javax.servlet.*;
import java.io.IOException;

/**
 * 日志增加TraceId
 *
 * @author tangyixiang
 * @Date 2022/1/6
 */
public class TraceIdFilter implements Filter {

    private static final String UNIQUE_ID = "TraceId";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        boolean bInsertMDC = createTraceId();
        try {
            filterChain.doFilter(request, response);
        } finally {
            if(bInsertMDC) {
                MDC.remove(UNIQUE_ID);
            }
        }
    }

    private boolean createTraceId() {
        String uniqueId = IdUtil.fastSimpleUUID();;
        MDC.put(UNIQUE_ID, uniqueId);
        return true;
    }
}
