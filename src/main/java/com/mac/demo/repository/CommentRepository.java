package com.mac.demo.repository;

import com.mac.demo.dto.CommentDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<CommentDTO, Long>{

	/*
	 * Generic type은 관리하는 Entity와 그 Entity의 primary type
	 * 
	 * @Transaction : 오류가 발생했을 때 모든 작업들을 원상태로 되돌림
	 * @Modifying : @Query를 통해 작성된 INSERT, UPDATE, DELETE (SELECT 제외) 쿼리에서 사용
	 *			 	주로 단건이 아닌 Bulk연산과 함께 사용
	 */
	
//	게시판 글번호를 받아 해당 번호에 달린 댓글리스트 얻어오기
	List<CommentDTO> findByPcode(Long pcode);
	
	int deleteByComment_num(Long comment_num);
	List<CommentDTO> findByComment(String comment);
	List<CommentDTO> findByNickname(String nickName);
	

}
