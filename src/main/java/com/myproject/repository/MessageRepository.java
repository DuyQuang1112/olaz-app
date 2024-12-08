package com.myproject.repository;

import com.myproject.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {

    @Query(value = "select * from tbl_message m where m.room_id =:m_id", nativeQuery = true)
    List<Message> getMessageByRoomId(@Param("m_id") Integer id);
}
