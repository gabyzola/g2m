package g2m.g2m_backend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SpaController {

    @RequestMapping({"/{path:^(?!api).*$}", "/**/{path:^(?!api).*$}"})
    public String forwardToIndex() {
        return "forward:/index.html";
    }
}

