package com.spring.demo.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spring.demo.entity.Student;

public interface StudentRepository extends JpaRepository<Student, Long> {

}
