package com.shop.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/** Active les tâches planifiées (réconciliation des paiements Stripe). */
@Configuration
@EnableScheduling
public class SchedulingConfig {}
