package main.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class DefaultController {

  @GetMapping("")
  public String index(Model model) {
    return "index";
  }

  @RequestMapping(value = "/**/{path:[^\\.]+}")
  public String forward() {
    return "forward:/";
  }
}
