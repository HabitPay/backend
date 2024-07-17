package com.habitpay.habitpay.domain.member.domain;

import com.habitpay.habitpay.domain.model.BaseTime;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "members")
public class Member extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column()
    private String nickname;

    @Column(unique = true)
    private String imageFileName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private boolean isActive;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Builder
    public Member(Long id, String email, String imageFileName, String nickname, Role role) {
        this.id = id;
        this.email = email;
        this.imageFileName = imageFileName;
        this.nickname = nickname;
        this.isActive = false;
        this.role = role;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
    }

    public void clear() {
        this.nickname = "탈퇴한 사용자";
        this.imageFileName = null;
        this.email = null;
        this.isActive = false;
        this.setDeletedAt(LocalDateTime.now());
    }

    public void activate(String nickname) {
        this.nickname = nickname;
        this.isActive = true;
    }

    public Member create(String email) {
        this.email = email;

        return this;
    }

    public String getRoleKey() {
        return this.role.getKey();
    }
}