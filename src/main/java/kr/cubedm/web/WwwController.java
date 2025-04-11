package kr.cubedm.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WwwController {
  @GetMapping("/www/")
  public String www() {
    return "forward:/www/index.html";
  }
}
