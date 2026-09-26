package com.examen.tvmaze.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.examen.tvmaze.model.Comment;

public interface CommentRepository extends MongoRepository<Comment,String >{

}
