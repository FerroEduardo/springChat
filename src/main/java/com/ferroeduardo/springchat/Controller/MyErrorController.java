package com.ferroeduardo.springchat.Controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@Controller
public class MyErrorController implements ErrorController {

    private static final Logger logger = LoggerFactory.getLogger(MyErrorController.class);

    @RequestMapping("/error")
    public String handleError(Model model, HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
            logger.error("Error trying to access the page. Code " + statusCode);
            model.addAttribute("mensagemErro", "Ocorreu um erro("+statusCode+") ao acessar essa página.");
        } else {
            logger.error("Error trying to access the page. Unknown code");
            model.addAttribute("mensagemErro", "Ocorreu um erro ao acessar essa página.");
        }
        return "error";
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }
}
