package com.myproject.validate;

import com.myproject.exception.CustomIllegalArgumentException;
import com.myproject.model.JoinForm;
import com.myproject.repository.JoinFormRepository;

import java.util.List;
import java.util.Objects;

public class JoinFomValidate {

    public static void isExist(Integer roomId , Integer userId, JoinFormRepository joinFormRepository){
        List<JoinForm> joinForms = joinFormRepository.findBySenderId(userId);
        for(JoinForm joinForm : joinForms){
            if(joinForm != null && Objects.equals(joinForm.getRoom().getId(), roomId)){
                throw new CustomIllegalArgumentException("Can not send many request");
            }
        }
    }
}
