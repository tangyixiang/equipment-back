package com.ocs.common.utils;

import com.ocs.common.exception.ServiceException;
import org.springframework.core.io.ClassPathResource;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;

/**
 * @author tangyixiang
 * @Date 2022/2/22
 */
public class TemplateDownloadUtils {

    public static void downloadByFileName(String fileName, HttpServletResponse response) {
        ClassPathResource classPathResource = new ClassPathResource("template/" + fileName);
        // 初始化流
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
            response.addHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));
            inputStream = classPathResource.getInputStream();
            outputStream = response.getOutputStream();
            byte[] bys = new byte[1024];
            int len;
            while ((len = inputStream.read(bys)) != -1)
                outputStream.write(bys, 0, len);
            outputStream.flush();
            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            throw new ServiceException("下载文件异常:" + e.getMessage());
        }

    }
}
