package com.habitpay.habitpay.board.service;

import com.habitpay.habitpay.board.dto.BoardListResponseDto;
import com.habitpay.habitpay.board.dto.BoardRequestDto;
import com.habitpay.habitpay.board.dto.BoardResponseDto;
import com.habitpay.habitpay.board.entity.Board;
import com.habitpay.habitpay.board.repository.BoardRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;

    public BoardResponseDto createBoard(BoardRequestDto requestDto) {
        Board board = new Board(requestDto);
        boardRepository.save(board);
        return new BoardResponseDto(board);
    }

    public List<BoardListResponseDto> findAllBoard() {
        try {
            List<Board> boardList = boardRepository.findAll();
            List<BoardListResponseDto> responseDtoList = new ArrayList<>();

            for (Board board : boardList) {
                responseDtoList.add(
                        new BoardListResponseDto(board)
                );
            }
            return responseDtoList;
        } catch (Exception e) {
            System.out.println("게시물이 없습니다.");
            return null;
        }
    }

    public BoardResponseDto findOneBoard(Long id) {
        Board board = boardRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("조회 실패")
        );
        return new BoardResponseDto(board);
    }

    @Transactional
    public Long updateBoard(Long id, BoardRequestDto requestDto) {
        Board board = boardRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("존재하는 게시물이 아닙니다.")
        );
        board.update(requestDto);
        return board.getId();
    }

    @Transactional
    public Long deleteBoard(Long id) {
        boardRepository.deleteById(id);
        return id;
    }

}
