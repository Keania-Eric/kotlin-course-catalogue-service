package com.kotlinspring.coursecatalogservice.repositories

import com.kotlinspring.coursecatalogservice.entities.Instructor
import org.springframework.data.repository.CrudRepository

interface InstructorRepository: CrudRepository<Instructor, Int> {
}