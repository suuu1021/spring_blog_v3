package com.tenco.blog.user;

public class UserBuilderTest {

    // 메인 테스트
    public static void main(String[] args) {
        Student student1 = Student
                .builder()
                .id(100)
                .username("고길동")
                .build(); // new Student
        // lombok 빌더 패턴을 사용한다면 마지막에 .build() 는 반드시 호출

        User user1 = User
                .builder()
                .id(1)
                .username("홍길동")
                .password("1234")
                .email("a@naver.com");
        System.out.println(user1);

        User user2 = User.builder().id(2).username("가나디");
        System.out.println(user2);
    }
}
