package com.examen.tvmaze.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.examen.tvmaze.model.Comment;

public interface CommentRepository extends MongoRepository<Comment,String >{

     List<Comment> findByShowIdIn(Collection<Long> showIds);

      List<Comment> findByShowIdOrderByCreatedAtDesc(Long showId);

}
