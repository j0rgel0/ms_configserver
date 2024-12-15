package com.lox.configserver.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class ClientRequestInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
            Object handler) {
        String requestUri = request.getRequestURI();
        String[] segments = requestUri.split("/");

        log.info("\n" +
                "*******************************************\n" +
                "* Incoming Request Details                 *\n" +
                "*******************************************\n" +
                "Request URI: {}\n" +
                "Segments   : {}\n" +
                "*******************************************", requestUri, String.join(", ", segments));

        if (segments.length > 2) {
            String application = segments[1];
            String profile = segments[2];
            String label = (segments.length > 3) ? segments[3] : null;

            if ("default".equals(profile)) {
                log.info("Ignoring 'default' profile for application '{}'.", application);
                return true;
            }

            if (label != null) {
                log.info("\n" +
                        "*******************************************\n" +
                        "* Config Request                          *\n" +
                        "*******************************************\n" +
                        "Application : {}\n" +
                        "Profile     : {}\n" +
                        "Label       : {}\n" +
                        "*******************************************", application, profile, label);
            } else {
                log.info("\n" +
                        "*******************************************\n" +
                        "* Config Request                          *\n" +
                        "*******************************************\n" +
                        "Application : {}\n" +
                        "Profile     : {}\n" +
                        "*******************************************", application, profile);
            }
        } else {
            log.warn("\n" +
                    "*******************************************\n" +
                    "* Invalid Config Request                  *\n" +
                    "*******************************************\n" +
                    "Received insufficient URI segments: {}\n" +
                    "*******************************************", requestUri);
        }

        return true;
    }
}
