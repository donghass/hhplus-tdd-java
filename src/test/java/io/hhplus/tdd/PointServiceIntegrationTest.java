package io.hhplus.tdd;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.PointService;
import io.hhplus.tdd.point.TransactionType;
import io.hhplus.tdd.point.UserPoint;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
public class PointServiceIntegrationTest {

    @Autowired
    private UserPointTable userPointTable;
    @Autowired
    private PointService pointService;



    @DisplayName("포인트 충전")
    @Test
    void pointChargeTest() {
        // Arrange
        long userId = 1L;
        long amount = 5000L;

        //userPointTable.insertOrUpdate(userId, 1000L);
//        pointService.updateUserChargePoint(userId,amount);

        // Act updateUserChargePoint 실행시 예외 발생하면 실패 -- 500000만 포인트 충전하면 실패
        assertDoesNotThrow(() -> pointService.updateUserChargePoint(userId, amount));

        var readUser = pointService.readUserPoint(userId);
        // Assert -- insertOrUpdate 정상 실행 여부 확인
        assertEquals(userId, readUser.id());
        assertEquals(500, readUser.point());
    }

    @DisplayName("포인트 사용")
    @Test
    void pointUseTest() {
        // Arrange
        long userId = 1L;
        long amount = 500L;

        //포인트 사용이므로 초기값 세팅
        userPointTable.insertOrUpdate(userId, 1000L);
        // Act updateUserChargePoint 실행시 예외 발생하면 실패 -- 500000만 포인트 충전하면 실패
        assertDoesNotThrow(() -> pointService.updateUserUsePoint(userId, amount));

        var readUser = pointService.readUserPoint(userId);

        // Assert -- insertOrUpdate 정상 실행 여부 확인
        assertEquals(userId, readUser.id());
        assertEquals(500, readUser.point());
    }
//    @DisplayName("포인트 사용 이력 입력")
//    @Test
//    void pointHistoryTest() {
//        // Arrange
//        long userId = 1L;
//        long amount = 500L;
//
//        //포인트 사용이므로 초기값 세팅
//        userPointTable.insertOrUpdate(userId, 1000L);
//        // Act updateUserChargePoint 실행시 예외 발생하면 실패 -- 500000만 포인트 충전하면 실패
//        assertDoesNotThrow(() -> pointService.updateUserUsePoint(userId, amount));
//
//        var readUser = pointService.readUserPoint(userId);
//
//        // Assert -- insertOrUpdate 정상 실행 여부 확인
//        assertEquals(userId, readUser.id());
//        assertEquals(500, readUser.point());
//    }
//
//    @DisplayName("없는 사용자일 경우 포인트 사용 이력 조회 시 예외처리")
//    @Test
//    void pointHistorySelectIsNullTest() {
//        // Arrange
//        long userId = 1L;
//        when(userPointTable.selectById(userId)).thenReturn(null);
//
//        // Act & Assert: IllegalArgumentException이 발생하는지 확인
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
//            () -> pointService.readUserPointHistory(userId));
//        assertEquals("없는 사용자입니다.", exception.getMessage());
////        // Act
////        assertDoesNotThrow(() -> pointHistoryTable.selectAllByUserId(userId));
////
////        // Assert
////        verify(pointHistoryTable).selectAllByUserId(eq(userId));
//    }
//
//    @DisplayName("사용자 존재할 경우 포인트 사용 이력 조회 시 정상처리")
//    @Test
//    void pointHistorySelectIsNotNullTest() {
//        // Arrange
//        long userId = 1L;
//        // 사용자 정보가 존재한다고 가정
//        UserPoint userPoint = new UserPoint(userId, 1000L, System.currentTimeMillis());
//        when(userPointTable.selectById(userId)).thenReturn(userPoint);
//
//        List<PointHistory> expectedHistory = List.of(
//            new PointHistory(1, 1, 1000,TransactionType.USE,123456789L),
//            new PointHistory(2, 1, 1000,TransactionType.USE,123456789L)
//        );
//
//        when(pointHistoryTable.selectAllByUserId(userId)).thenReturn(expectedHistory);
//
//        // Act
//        List<PointHistory> actualHistory = pointService.readUserPointHistory(userId);
//
//        // Assert
//        assertEquals(expectedHistory, actualHistory);
//        verify(pointHistoryTable).selectAllByUserId(userId);
//    }
}
