package com.habitpay.habitpay.domain.participationstat.dao;

import com.habitpay.habitpay.domain.participationstat.domain.ParticipationStat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipationStatRepository extends JpaRepository<ParticipationStat, Long> {
}
