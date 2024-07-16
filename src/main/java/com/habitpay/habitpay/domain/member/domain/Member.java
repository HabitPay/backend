package com.habitpay.habitpay.domain.member.domain;

import com.habitpay.habitpay.domain.model.BaseTime;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @Column()
    private String imageFileName;

    @Column(nullable = false)
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

    public void updateProfile(String nickname, String imageFileName) {
        this.nickname = nickname;
        this.imageFileName = imageFileName;
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