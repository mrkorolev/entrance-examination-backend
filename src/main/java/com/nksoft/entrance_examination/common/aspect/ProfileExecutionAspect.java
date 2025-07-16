package com.nksoft.entrance_examination.common.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class ProfileExecutionAspect {
    @Around("@annotation(profileExecution)")
    public Object profile(ProceedingJoinPoint joinPoint,
                          ProfileExecution profileExecution) throws Throwable {
        Runtime runtime = Runtime.getRuntime();
        boolean logMemory = profileExecution.logMemory();

        long usedBefore = 0;
        if (logMemory) {
            usedBefore = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024);
        }

        long start = System.nanoTime();
        Object result = joinPoint.proceed();
        long end = System.nanoTime();

        long usedAfter = 0;
        long heapDiff = 0;
        if (logMemory) {
            usedAfter = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024);
            heapDiff = usedAfter - usedBefore;
        }

        long durationMs = (end - start) / 1_000_000;
        long minutes = durationMs / 1000 / 60;
        long seconds = (durationMs / 1000) % 60;

        if (logMemory) {
            log.info(
                    "{} executed in {} ms ({} min {} sec), heap: {} MB → {} MB (diff: {} MB)",
                    joinPoint.getSignature().getName(),
                    durationMs, minutes, seconds,
                    usedBefore, usedAfter, heapDiff
            );
        } else {
            log.info(
                    "{} executed in {} ms ({} min {} sec)",
                    joinPoint.getSignature().getName(),
                    durationMs, minutes, seconds
            );
        }
        return result;
    }
}
