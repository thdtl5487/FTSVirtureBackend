package com.springboot.react.cboard;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.TypedQuery;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import com.springboot.react.cboard.CBoardVO;

import com.springboot.react.cboard.CBoardRepository;

import lombok.RequiredArgsConstructor;

@Service("cboardService")
@RequiredArgsConstructor
public class CBoardService {
	
	
	private final CBoardRepository cboardDAO;


	

	@Transactional
	public void insert(CBoardVO vo) {
		cboardDAO.insert(vo);
	}
	
	
 
   public ResponseEntity<Map> getPagingBoard(Integer pageNum){
	   return cboardDAO.getPagingBoard(pageNum);
   }
   
   public ResponseEntity<Map> getBoard(Long bnum){
	   return cboardDAO.getBoard(bnum);
   }
	
	
}