package com.myproject.controller;

import com.myproject.dto.JoinFormDTO;
import com.myproject.service.JoinFormService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/join-form")
@RequiredArgsConstructor
public class JoinFormController {

    private final JoinFormService joinFormService;

    @GetMapping("/pending")
    public ResponseEntity<List<JoinFormDTO>> getAllPendingForm(@RequestParam("userId") Integer userId){
        return ResponseEntity.status(HttpStatus.OK)
                .body(joinFormService.getAllPendingForm(userId));
    }

    @PostMapping("/handle-form")
    public ResponseEntity<String> handleForm(@RequestParam("joinFormId") Integer joinFormId, @RequestParam("action") String action){
        joinFormService.handleForm(joinFormId, action);
        return ResponseEntity.status(HttpStatus.OK)
                .body("Success");
    }

}
