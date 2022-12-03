package com.mac.demo.serviceImpl;

import com.github.pagehelper.PageInfo;
import com.mac.demo.dto.Attach;
import com.mac.demo.dto.Board;
import com.mac.demo.repository.AttachRepository;
import com.mac.demo.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


@Service
@Transactional
@RequiredArgsConstructor
public class BoardServiceImpl implements com.mac.demo.service.BoardService {

	private final BoardRepository boardRepository;
	private final AttachRepository attachRepository;

	private final ResourceLoader resourceLoader;


	public Board getBoard(String user_id, String nickname, String category) {
		Board board = Board.builder()
				.user_id(user_id)
				.nickname(nickname)
				.category(category).build();
		return board;
	}

	public List<Board> findBoardByCategory(String categoryMac){
		return boardRepository.findByCategory(categoryMac);
	}

	/**
	 *	게시글 저장
	 */
	public Long save(Board board, MultipartFile[] mfiles, String savePath){
		Board _board = boardRepository.save(board);
		List<Attach> attlist = getFileSet(_board, mfiles, savePath);
		if(attlist!=null) attachRepository.saveAll(attlist);
		
		return _board.getBoard_num();
	}
	
	/**
	 *	게시글 수정
	 */
	public Boolean update(Board board, MultipartFile[] mfiles, String savePath) {
		try {
			boardRepository.update(board.getTitle(), board.getContents(), board.getBoard_num());
			List<Attach> attlist = getFileSet(board, mfiles, savePath);
			if(attlist!=null) attachRepository.saveAll(attlist);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 *	게시글 상세보기
	 */
	public Board getDetail(Long board_num, String category) {
		return boardRepository.findByNummacAndCategorymac(board_num, category);
	}

	/**
	 * 게시글 삭제
	 */
	public boolean delete(Long board_num) {
		return 0 > boardRepository.deleteByboard_num(board_num);
	}
	
	/**
	 *	글검색 - 제목&글내용
	 */
	public List<Board> getListByKeyword(String keyword, String categorymac) {
		return boardRepository.getListByKeyword(keyword, categorymac);
	}

	/**
	 *	글검색 - 닉네임
	 */
	public List<Board> getListByNickName(String nickname, String category) {
		return boardRepository.getListByNickname(nickname, category);
	}


//	------------------------File------------------------
	public List<Attach> getFileList(Long pcode){
		return attachRepository.findAllByPcodemac(pcode);
	}

//	File Id로 파일이름 가져오기
	public String getFname(Long file_id) {
		Attach attach = attachRepository.findById(file_id).get();
		String filename = attach.getFilename();
		return filename;
	}
	
//	파일 지우기 Ajax
	public boolean filedelete(Long file_Id) {
		try {
			attachRepository.deleteById(file_Id);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
//	파일 리스트 구성
	public List<Attach> getFileSet(Board board, MultipartFile[] mfiles, String savePath) {
		String fname_changed = null;
		List<Attach> attList = new ArrayList<>();
		
		try {
			for (int i = 0; i < mfiles.length; i++) {
				String[] token = mfiles[i].getOriginalFilename().split("\\.");
				fname_changed = token[0] + "_" + System.nanoTime() + "." + token[1];
				
					Attach _att = new Attach();
					_att.setPcode(board.getBoard_num());
					_att.setUser_id(board.getUser_id());
					_att.setFilename(fname_changed);
					_att.setFilepath(savePath);
				
				attList.add(_att);
				/**
				 * 메모리에 있는 파일을 저장경로에 옮김, 로컬 경로에 있는 파일만 선택 가능
				 * 추후 AWS S3로 전환
				 */
				mfiles[i].transferTo(
						new File(savePath + "/" + fname_changed));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return attList;
	}

//	파일 다운로드
	public ResponseEntity<Resource> download (String contentType, Resource resource) throws UnsupportedEncodingException {

	      if (contentType == null) contentType = "application/octet-stream";

	      ResponseEntity<Resource> file =  ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
	            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + new String(resource.getFilename().getBytes("UTF-8"), "ISO-8859-1") + "\"")
	                  
	            // .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
	            // HttpHeaders.CONTENT_DISPOSITION는 http header를 조작하는 것, 화면에 띄우지 않고 첨부화면으로
	            // 넘어가게끔한다
	            // filename=\"" + resource.getFilename() + "\"" 는 http프로토콜의 문자열을 고대로 쓴 것
	            .body(resource);

	      return file;
	   }
	
	
	public PageInfo<Board> getPageInfo(String categoryMac) {
		PageInfo<Board> pageInfo = new PageInfo<>(findBoardByCategory(categoryMac));
		return pageInfo;
	}
	
}
