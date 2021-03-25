package com.codeworrisors.Movie_Community_Web.controller;

import com.codeworrisors.Movie_Community_Web.request.ReviewRequest;

import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/test")
@Secured("*")
public class TestController{


    @GetMapping
    public String getMethodName(@ModelAttribute() ReviewRequest reviewRequest) {
        return reviewRequest.toString();
    }
    
}