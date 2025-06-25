package com.tenco.blog.board;

import com.tenco.blog.user.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@RequiredArgsConstructor
@Controller // IoC 대상 - 싱글톤 패턴으로 관리 됨
public class BoardController {

    // DI 처리
    private final BoardRepository boardRepository;

    // 게시글 삭제 액선 처리
    // /board/{{board.id}}/delete" method="post"

    // 1. 로그인 여부 확인 (인증 검사)
    // 2. 로그인 X (로그인 페이지로 리다이렉트 처리)
    // 3. 로그인 O (게시글 존재 여부 확인)
    // 4. 권한 체크 (해당 유저의 게시물인지 확인)
    // 5. 리스트화면으로 리다이렉트 처리
    @PostMapping("/board/{id}/delete")
    public String delete(@PathVariable(name = "id") Long id, HttpSession session) {
        // 1. 로그인 체크 Define.SESSION_USER
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            // 2. 로그인 페이지로 리다이렉트 처리
            // redirect: --> 내부 에서 페이지를 찾는게 아님
            // 다시 클라이언트에 와서 -> GET 요청이 온 것 (HTTP 메세지 생성 됨)
            return "redirect:/login-form";
        }
        // <-- 관리자가 게시물 강제 삭제 가능성이 있음
        // 3. 게시물 존재 여부 확인
        Board board = boardRepository.findById(id);
        if (board == null) {
            throw new IllegalArgumentException("이미 삭제된 게시글 입니다.");
        }
        // 4. 소유자 확인 : 권한 체크
        if (!board.isOwner(sessionUser.getId())) {
            throw new RuntimeException("삭제 권한이 없습니다.");
        }
        // if (!(sessionUser.getId() == board.getUser().getId())) {
        //    throw new RuntimeException("삭제 권한이 없습니다.");
        // }
        // 5. 권한 확인 이후 삭제 처리
        boardRepository.deleteById(id);

        // 6. 삭제 성공 시 리다이렉트 처리
        return "redirect:/";
    }

    /**
     * 게시글 작성 화면 요청
     * 주소 설계 : http://localhost:8080/board/save-form
     *
     * @param session
     * @return
     */
    @GetMapping("/board/save-form")
    public String saveForm(HttpSession session) {
        // 권한 체크 -> 로그인된 사용자만 게시글 작성 화면 요청 가능
        User sessionUser = (User) session.getAttribute("sessionUser");
        if (sessionUser == null) {
            // 로그인 하지 않은 경우 다시 로그인 페이지로 리다이렉트 처리
            return "redirect:/login-form";
        }
        return "board/save-form";
    }

    // http://localhost:8080/board/save
    // 게시글 저장 액션 처리
    @PostMapping("/board/save")
    public String save(BoardRequest.SaveDTO reqDTO, HttpSession session) {

        try {
            // 1. 권한 체크
            User sessionUser = (User) session.getAttribute("sessionUser");
            if (sessionUser == null) {
                // 로그인 하지 않은 경우 다시 로그인 페이지로 리다이렉트 처리
                return "redirect:/login-form";
            }
            // 2. 유효성 검사
            reqDTO.validate();
            // 3. SaveDTO --> 저장시키기 위해 --> Board 변환을 해주어야 한다.
            // Board board = reqDTO.toEntity(sessionUser);
            boardRepository.save(reqDTO.toEntity(sessionUser));

            return "redirect:/";
        } catch (Exception e) {
            e.printStackTrace();
            // 필요하다면 에러 메세지를 내려줄 수 있다.
            return "board/save-form";
        }
    }

    @GetMapping("/")
    public String index(HttpServletRequest request) {

        // 1. 게시글 목록 조회
        List<Board> boardList = boardRepository.findByAll();
        // 2. 생각해볼 사항 - Board 엔티티에는 User 엔티티와 연관관계 중
        // 연관 관계 호출 확인
        // boardList.get(0).getUser().getUsername();
        // 3. 뷰에 데이터 전달
        request.setAttribute("boardList", boardList);
        return "index";
    }

    /**
     * 게시글 상세 보기 화면 요청
     *
     * @param id      - 게시글 pk
     * @param request (뷰에 데이터 전달)
     * @return detail.mustache
     */
    @GetMapping("/board/{id}")
    public String detail(@PathVariable(name = "id") Long id, HttpServletRequest request) {
        Board board = boardRepository.findById(id);
        request.setAttribute("board", board);
        return "board/detail";
    }
}
