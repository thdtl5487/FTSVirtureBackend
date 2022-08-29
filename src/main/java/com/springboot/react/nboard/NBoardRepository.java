package com.springboot.react.nboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class NBoardRepository{
	
	// @PersistenceContext : JPA의 ORM을 처리해주는 EntityManager을 불러올 때 쓰는 애노테이션 입니다.
	@PersistenceContext	
	private final EntityManager em;
	
	@Autowired
	NBoardRepositoryInterface nboardRepository;

	public void insert(NBoardVO vo) {
		// em.persist : JPA를 통해 값을 입력할 때 활용합니다.
		em.persist(vo);								
	}
	
	public NBoardVO selectById(NBoardVO vo) {
		
		NBoardVO result = null;
		try {
			result = em.createQuery("select a from NBoardVO a where a.BNum = (select max(a.BNum) from NBoardVO a)",NBoardVO.class).getSingleResult();
													// em.createQuery : JPA를 통해 쿼리문을 직접 입력할 때
													// getSingleResult() : 값이 단 하나일 경우를 처리하는 메소드 (0개나 2개 이상일 경우를 예외처리 해줘야함) 
		}
		catch (NoResultException e) {				// 1. 값이 0개일 경우 예외처리
			System.out.println("No Result");
		}
		catch (NonUniqueResultException e) {		// 2. 값이 2개 이상일 경우 예외처리
			System.out.println("No Unique Result");
		}
		
		return result;
	}
	
	public void delete(NBoardVO vo) {
		em.remove(vo);								// em.remove : JPA를 통해 값을 제거할 때
	}
	
	public List<NBoardVO> getList(NBoardVO vo){
	    
//		List getList = em.createQuery("select b from CBoardVO b").setFirstResult(0).setMaxResults(9).getResultList();
		Sort sortNum = Sort.by("BNum").descending(); 
		Pageable pageable = PageRequest.of(0, 10, sortNum);
		
		Page<NBoardVO> getList = nboardRepository.findAll(pageable);
		
		List<NBoardVO> result = new ArrayList<NBoardVO>();
		
		if(getList != null && getList.hasContent()) {
			result = getList.getContent();
		}
		
		return result;
	   }
	   
	// 페이징된 게시물 리스트와 페이징 정보(현재 페이지, 최대 페이지 번호)를 HashMap 타입으로 저장하는 메소드
	public ResponseEntity<Map> getPagingBoard(Integer pageNum){
		Map<String, Object> result = null;
		
		// 1. 리스트 페이징 처리
		Sort sortNum = Sort.by("BNum").descending(); // Sort 정렬 규칙 선언, BNum 역순
		Pageable pageable = PageRequest.of(pageNum, 10, sortNum); // 페이징, 매개변수 : (들어온 웹의 페이지 수, amount, 정렬규칙 적용)
		Page<NBoardVO> pageList = nboardRepository.findAll(pageable); // 페이징된 게시물 리스트를 담는 인스턴스
		
		List<NBoardVO> boardList = new ArrayList<NBoardVO>(); // pageList를 송신할 수 있는 타입으로 저장하는 인스턴스
		
		if(pageList != null && pageList.hasContent()) { // pageList에 값 있는지 체크. hasContent는 값 있으면 true 리턴
			boardList = pageList.getContent(); // pageList의 내용을 boardList에 저장
		}
		// 리스트 페이징 저장 끝 boardList 값 리턴하면 됨.
		
		// 2. 페이지 정보
		HashMap<String, Object> pageInfo = new HashMap<String, Object>();
		int currentPage = pageList.getNumber(); // 현재 페이지값 저장, getNumber는 Page 타입의 내장 메소드
		System.out.println("current page : "+ currentPage);
		int maxPage = pageList.getTotalPages(); // 전체 페이지값 저장
		
		pageInfo.put("currentPage", currentPage); // 현재 페이징된 리스트의 번호 저장
		pageInfo.put("maxPage", maxPage); // 마지막 페이지 번호 저장
		
		result = new HashMap<String, Object>();
		result.put("list", boardList); // 페이징된 게시물 리스트를 List라는 key에 저장
		result.put("pageInfo", pageInfo); // 페이징 정보를 pageInfo라는 key에 저장
		
		
		return ResponseEntity.ok(result);
	}
}