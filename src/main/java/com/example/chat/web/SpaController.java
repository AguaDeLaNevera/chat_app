package com.example.chat.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaController {

    @GetMapping({
            "/",
            "${APP_FRONTEND_LOGIN_PATH:/login}",
            "${APP_FRONTEND_REGISTER_PATH:/register}",
            "${APP_FRONTEND_CHAT_PATH:/chat}"
    })
    public String index() {
        return "forward:/index.html";
    }
}
