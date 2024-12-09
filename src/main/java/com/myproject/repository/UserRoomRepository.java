package com.myproject.repository;

import com.myproject.model.UserRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoomRepository extends JpaRepository<UserRoom, Integer> {
    List<UserRoom> findRoomByUserId(Integer userId);
    List<UserRoom> findUserByRoomId(Integer roomId);
    UserRoom findByUserIdAndRoomId(Integer userId, Integer roomId);
    UserRoom findByRoleAndRoomId(String role, Integer roomId);

}
