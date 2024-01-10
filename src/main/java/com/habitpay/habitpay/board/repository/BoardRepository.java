package com.habitpay.habitpay.board.repository;

import com.habitpay.habitpay.board.dto.BoardListResponseDto;
import com.habitpay.habitpay.board.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
    List<BoardListResponseDto> findAllByOrderByModifiedAtDesc();
}
