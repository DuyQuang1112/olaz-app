package com.myproject.repository;

import com.myproject.model.JoinForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JoinFormRepository extends JpaRepository<JoinForm, Integer> {
    List<JoinForm> findAllByReceiverIdAndStatus(Integer senderId, String status);
    List<JoinForm> findBySenderId(Integer senderId);

}
