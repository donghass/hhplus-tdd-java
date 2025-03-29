package io.hhplus.tdd;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.hhplus.tdd.point.PointService;
import io.hhplus.tdd.point.TransactionType;
import io.hhplus.tdd.point.UserPoint;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.Test;

public class ConcurrentTest {
    private final PointService pointService = new PointService();

    @Test
    void 동시성_테스트_CountDownLatch() throws InterruptedException {
        int numberOfThreads = 60;
        CountDownLatch latch = new CountDownLatch(numberOfThreads);  // 100개의 스레드를 기다리도록 설정
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

        // 각 스레드에서 수행할 작업
        Runnable task = () -> {
            try {
                pointService.updateUserChargePoint(1L, 500L);
            } finally {
                latch.countDown();  // 작업이 끝나면 latch 카운트 감소
            }
        };

        // 작업을 100번 수행
        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(task);
        }

        latch.await();  // 모든 스레드가 완료될 때까지 대기

        var point = pointService.readUserPoint(1);
        executorService.shutdown();

        assertEquals(30000, point.point(), "포인트 값이 30000이어야 합니다.");
    }

    @Test
    void 사용자_포인트_충전_조회() {
        // given
        long userId = 1L;
        long amount = 500L;
        TransactionType type = TransactionType.CHARGE; // 예시로 충전 타입 사용

        UserPoint mockUserPoint = new UserPoint(userId, amount, System.currentTimeMillis());

        pointService.updateUserChargePoint(userId,amount);

        //사용자 포인트 조회
        var select = pointService.readUserPoint(userId);
        assertEquals(mockUserPoint.point(), select.point());
    }
}
