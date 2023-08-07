package com.kotlinspring.coursecatalogservice.utils

import com.kotlinspring.coursecatalogservice.dto.CourseDTO
import com.kotlinspring.coursecatalogservice.entities.Course
import com.kotlinspring.coursecatalogservice.entities.Instructor

fun courseEntityList(): List<Course> = listOf(

    Course(null,
        "Build RESTful APIs using SpringBoot and Kotlin",
        "Development"),
    Course(null,
        "Build Reactive Microservices using Spring WebFlux/SpringBoot",
        "Development"),
    Course(null,
        "Wiremock for Java Developers",
        "Development")

)

fun courseEntityList(instructor: Instructor? = null): List<Course> = listOf(

    Course(null,
        "Build RESTful APIs using SpringBoot and Kotlin",
        "Development",
        instructor),
    Course(null,
        "Build Reactive Microservices using Spring WebFlux/SpringBoot",
        "Development",
        instructor),
    Course(null,
        "Wiremock for Java Developers",
        "Development",
        instructor)

)

fun courseDTOFunc(
    id:Int?,
    name: String = "Build RESTful APIs using SpringBoot and Kotlin",
    category: String = "Testing",
   instructorId:Int? = 1
) = CourseDTO(
    id,
    name,
    category,
    instructorId
)

fun instructorEntity(name : String = "Dilip Sundarraj")
        = Instructor(null, name)