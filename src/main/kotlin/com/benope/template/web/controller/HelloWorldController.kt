package com.benope.template.web.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HelloWorldController {

    @GetMapping("/")
    fun helloWorld(): String {
        return "hello world"
    }

}