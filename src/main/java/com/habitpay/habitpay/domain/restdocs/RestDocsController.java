package com.habitpay.habitpay.domain.restdocs;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestDocsController {

    @GetMapping("/restDocs")
    public String getRestDocs() {
        return "hello";
    }
}
