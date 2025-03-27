package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PointService {
    @Autowired
    private UserPointTable userPointTable = new UserPointTable();
    @Autowired
    private PointHistoryTable pointHistoryTable = new PointHistoryTable();
    //사용자 포인트 조회
    public UserPoint readUserPoint(long id) {
        return userPointTable.selectById(id);
    }
    //사용자 포인트 충전
    public synchronized void updateUserChargePoint(long id, long amount) {
        var userPoint = userPointTable.selectById(id);
        if (userPoint != null) {
            long newPoints = userPoint.point() + amount;  // 기존 포인트에 충전 금액 추가
            //정책
            if (newPoints > 50000) {
                throw new IllegalArgumentException("충전 금액이 한도를 초과했습니다.");
            }
            userPointTable.insertOrUpdate(id, newPoints);  // 새로운 포인트 값 저장
        } else {
            userPointTable.insertOrUpdate(id, amount);  // 새로운 사용자에 대해 포인트 설정
        }
    }

    //사용자 포인트 사용
    public synchronized void updateUserUsePoint(long id, long amount) {
        var userPoint = userPointTable.selectById(id);
        if (userPoint != null) {
            long newPoint = userPoint.point() - amount;  // 기존 포인트에 충전 금액 추가
            
            //형식, 논리는 도메인에서 검증(사용금액, 남은 잔액, 유저ID 확인)
            UserPoint.userPoint(id,amount,newPoint);
            userPointTable.insertOrUpdate(id, newPoint);  // 새로운 포인트 값 저장
        } else {
            throw new IllegalArgumentException("없는 사용자입니다.");
        }

    }

    //사용자 포인트 변경 이력 입력
    public void createUserPointHistory(long id, long amount,TransactionType type) {
        //이력 입력하기 전에 충전 또는 사용에서 사용자 검증하기 때문에 isUser 삭제
//        isUser(id);
        pointHistoryTable.insert(id,amount,type,System.currentTimeMillis());
    }

    //사용자 포인트 변경 이력 조회
    public List<PointHistory> readUserPointHistory(long id) {
//        isUser(id);
        var userPoint = userPointTable.selectById(id);
        if(userPoint == null){
            throw new IllegalArgumentException("없는 사용자입니다.");
        }
        var userPointHistory = pointHistoryTable.selectAllByUserId(id);
        return userPointHistory;
    }

//변경 이력 조회에서만 사용하므로 별도로 빼지 않음
//    public void isUser(long id) {
//        var userPoint = userPointTable.selectById(id);
//        if(userPoint == null){
//            throw new IllegalArgumentException("없는 사용자입니다.");
//        }
//    }
}