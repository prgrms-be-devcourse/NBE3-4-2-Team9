package com.backend.domain.board.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue(value = "free")
public class FreeBoard extends Board {

    public FreeBoard(Long boardId, String subject, String content) {
        super(boardId, subject, content);
    }
}
