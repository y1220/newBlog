package it.course.myblog.service;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.course.myblog.entity.Comment;
import it.course.myblog.entity.Credit;
import it.course.myblog.entity.Post;
import it.course.myblog.entity.Users;
import it.course.myblog.repository.CreditRepository;

@Service
public class CountCreditsByUser {
	
	@Autowired
	CreditRepository creditRepository;
	
	public int countCredits(List<Post> ps, List<Comment> cs, Users currentUser) {
		
		int TotalCredits = 0;
		// CREDITS
		for(Post p : ps) {
			Credit cr = creditRepository.findByCreditCode(p.getCredit().getCreditCode()).get();
			TotalCredits = TotalCredits + (cr.getCreditImport());
			
		}
		
		for(Comment c : cs) {
			Credit cr = creditRepository.findByCreditCode(c.getCredit().getCreditCode()).get();
			TotalCredits = TotalCredits + (cr.getCreditImport());
		}
		
		// DEBTS
		Set<Post> postsBuyed = currentUser.getPosts();
		for(Post p : postsBuyed) {
			Credit cr = creditRepository.findByCreditCode(p.getCredit().getCreditCode()).get();
			TotalCredits = TotalCredits - (cr.getCreditImport());
		}
		
		
		return TotalCredits;
	}

}
