package com.tenco.blog.user;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor // 생성자 의존 주입 - DI 처리
@Repository // IoC 대상 + 싱글톤 패턴으로 관리 됨
public class UserRepository {

    private final EntityManager em;

    // 사용자 정보 조회(username, password)

    /**
     * 로그인 요청 기능 (사용자 정보 조회)
     * @param username
     * @param password
     * @return 성공 시 User 엔티티 실패시 null 반환
     */
    public User findByUsernameAndPassword(String username, String password) {
        //JPQL
        try {
            String jpql = "SELECT u FROM User u " +
                    "WHERE u.username = :username AND u.password = :password ";
            TypedQuery<User> typedQuery = em.createQuery(jpql, User.class);
            typedQuery.setParameter("username", username);
            typedQuery.setParameter("password", password);
            return typedQuery.getSingleResult();
        } catch (Exception e) {
            // 일치하는 사용자가 없거나 에러 발생 시 null 반환
            // 즉, 로그인 실패를 의미함
            return null;
        }
    }

    /**
     * 회원 정보 저장 처리
     *
     * @param user (비영속 상태)
     * @return User 엔티티 반환
     */
    @Transactional
    public User save(User user) {
        // 매개변수에 들어오는 user Object는 비영속화 된 상태이다.
        em.persist(user); // 영속성 컨텍스트에 user 객체를 관리하기 시작 함
        // 트랜잭션 커밋 시점에 실제 INSERT 쿼리를 실행한다.
        return user;
    }

    // 사용자명 중복 체크용 조회 기능
    public User findByUsername(String username) {
        // WHERE username =?
//        String jpql = " SELECT u FROM User u WHERE u.username = :username ";
//        TypedQuery<User> typedQuery = em.createQuery(jpql, User.class);
//        typedQuery.setParameter("username", username);
//        return typedQuery.getSingleResult();

        try {
            String jpql = " SELECT u FROM User u WHERE u.username = :username ";
            return em.createQuery(jpql, User.class)
                    .setParameter("username", username)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }

    }
}
