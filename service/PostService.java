package it.course.myblog.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import it.course.myblog.entity.Post;
import it.course.myblog.repository.PostPagedRepository;
import it.course.myblog.repository.PostRepository;
import it.course.myblog.repository.TagRepository;

@Service
public class PostService {
	
	@Autowired
	PostPagedRepository postPagedRepository;
	
	@Autowired
	PostRepository postRepository;
	
	@Autowired
	TagRepository tagRepository;
	
	public List<Post> findAllPaged(Integer pageNo, Integer pageSize, String sortBy){
		
		Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));
		
		Page<Post> pagedResult = postPagedRepository.findAll(paging);
		
		if(pagedResult.hasContent()) {
			return pagedResult.getContent();
		} else {
			return new ArrayList<Post>();
		}
		
	}
	
	
	public List<Post> findAllPagedAndPublished(Integer pageNo, Integer pageSize, String sortBy, String direction){
		
		Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(Direction.valueOf(direction.toUpperCase()), sortBy));
		
		Page<Post> pagedResult = postRepository.findAllByIsVisibleTrue(paging);
		
		if(pagedResult.hasContent()) {
			return pagedResult.getContent();
		} else {
			return new ArrayList<Post>();
		}
		
	}
	
	public List<Post> findAllPagedAndPublishedWhitTags(Set<Long> ids, Integer pageNo, Integer pageSize, String sortBy, String direction){
		
		Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(Direction.valueOf(direction.toUpperCase()), sortBy));
		
		Page<Post> pagedResult = postRepository.findByIdIn(ids, paging);
		
		if(pagedResult.hasContent()) {
			return pagedResult.getContent();
		} else {
			return new ArrayList<Post>();
		}
		
	}
	
	
	public static boolean isExactMatch(String source, String substring) {
		
		String pattern = "\\b"+substring+"\\b";
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(source);
		
		return m.find();
	}
	
	
	public static String findIp(HttpServletRequest request) {
		
		String ip = request.getHeader("X-FORWARDED-FOR");
		
		if(ip == null) {
			ip = request.getRemoteAddr();
		}
		
		return ip;
	}
	
	public static double calcRelevance(double s, double t, double f) {
		
		double k = (100/s) * ( f - 1 + (f/t));
		k = Math.floor(k*100) / 100; // round to two decimal
		
		return k;
		
		
		/**
		 * Numero Tags Cercati : S
			Numero Tag Trovati in un post: F
			Numero tag totali post: T
			Percentuale di base per ogni tag trovato:  B = 100 / S
			Correttiva della rilevanza: C = B * (1- F/T)
			% di rilevanza totale: R = B*F-C
			100/S * (F-1+F/T)
		*/
	}
	
	public Date addHoursToJavaUtilDate(Date date, int hours) {
	    Calendar calendar = Calendar.getInstance();
	    calendar.setTime(date);
	    calendar.add(Calendar.HOUR_OF_DAY, hours);
	    return calendar.getTime();
	}

}
