package io.hhplus.tdd;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.PointService;
import io.hhplus.tdd.point.TransactionType;
import io.hhplus.tdd.point.UserPoint;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
public class PointServiceTest {
    @MockBean
    private UserPointTable userPointTable;

    @MockBean
    private PointHistoryTable pointHistoryTable;

    @Autowired
    private PointService pointService;



    @DisplayName("포인트 충전")
    @Test
    void pointChargeTest() {
        // Arrange
        long userId = 1L;
        long amount = 500L;
        // 기존 포인트가 1000L라고 가정
        UserPoint userPoint = new UserPoint(userId, 1000L, System.currentTimeMillis());
        when(userPointTable.selectById(userId)).thenReturn(userPoint);

        // Act updateUserChargePoint 실행시 예외 발생하면 실패 -- 500000만 포인트 충전하면 실패
        assertDoesNotThrow(() -> pointService.updateUserChargePoint(userId, amount));


        // Assert -- insertOrUpdate 정상 실행 여부 확인
        verify(userPointTable).insertOrUpdate(userId, 1500L);
    }

    @DisplayName("포인트 사용")
    @Test
    void pointUseTest() {
        // Arrange -- 유저ID -1 , amount 음수, userPoint 음수 넣을 경우 오류 발생하여 테스트 실패
        long userId = 1L;
        long amount = 500L;
        UserPoint userPoint = new UserPoint(userId, 1000L, System.currentTimeMillis());
        when(userPointTable.selectById(userId)).thenReturn(userPoint);

        // Act
        assertDoesNotThrow(() -> pointService.updateUserUsePoint(userId, amount));

        // Assert
        verify(userPointTable).insertOrUpdate(userId, 500L);
    }
    @DisplayName("포인트 사용 이력 입력")
    @Test
    void pointHistoryTest() {
        // given
        long userId = 1L;
        long amount = 500L;
        TransactionType type = TransactionType.USE; // 사용
       

        // when
        assertDoesNotThrow(() -> pointService.createUserPointHistory(userId, amount, type));

        // then: pointHistoryTable.insert() 메서드가 해당 인자값으로 호출되었는지 검증
        verify(pointHistoryTable).insert(eq(userId), eq(amount), eq(type), anyLong());
    }

    @DisplayName("없는 사용자일 경우 포인트 사용 이력 조회 시 예외처리")
    @Test
    void pointHistorySelectIsNullTest() {
        // Arrange
        long userId = 1L;
        when(userPointTable.selectById(userId)).thenReturn(null);

        // Act & Assert: IllegalArgumentException이 발생하는지 확인
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> pointService.readUserPointHistory(userId));
        assertEquals("없는 사용자입니다.", exception.getMessage());
//        // Act
//        assertDoesNotThrow(() -> pointHistoryTable.selectAllByUserId(userId));
//
//        // Assert
//        verify(pointHistoryTable).selectAllByUserId(eq(userId));
    }

    @DisplayName("사용자 존재할 경우 포인트 사용 이력 조회 시 정상처리")
    @Test
    void pointHistorySelectIsNotNullTest() {
        // Arrange
        long userId = 1L;
        // 사용자 정보가 존재한다고 가정
        UserPoint userPoint = new UserPoint(userId, 1000L, System.currentTimeMillis());
        when(userPointTable.selectById(userId)).thenReturn(userPoint);

        List<PointHistory> expectedHistory = List.of(
            new PointHistory(1, 1, 1000,TransactionType.USE,123456789L),
            new PointHistory(2, 1, 1000,TransactionType.USE,123456789L)
        );

        when(pointHistoryTable.selectAllByUserId(userId)).thenReturn(expectedHistory);

        // Act
        List<PointHistory> actualHistory = pointService.readUserPointHistory(userId);

        // Assert
        assertEquals(expectedHistory, actualHistory);
        verify(pointHistoryTable).selectAllByUserId(userId);
    }
}
