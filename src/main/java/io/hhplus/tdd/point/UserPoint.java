package io.hhplus.tdd.point;


import java.util.concurrent.atomic.AtomicInteger;

public record UserPoint(
    long id,
    long point,
    long updateMillis
) {

    public static UserPoint empty(long id) {
        return new UserPoint(id, 0, System.currentTimeMillis());
    }

    public static UserPoint userPoint(long id, long point,long newPoint) {
        if (id <= 0) {
            throw new IllegalArgumentException("유저 ID는 음수일 수 없습니다.");
        }
        if (point < 0) {
            throw new IllegalArgumentException("포인트는 음수일 수 없습니다.");
        }
        if (newPoint < 0) {
            throw new IllegalArgumentException("잔액이 부족합니다.");
        }
        return new UserPoint(id, 0, System.currentTimeMillis());
    }



}
