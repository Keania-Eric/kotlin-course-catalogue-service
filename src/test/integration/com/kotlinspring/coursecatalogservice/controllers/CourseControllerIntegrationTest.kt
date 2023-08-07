package com.kotlinspring.coursecatalogservice.controllers

import com.kotlinspring.coursecatalogservice.dto.CourseDTO
import com.kotlinspring.coursecatalogservice.entities.Course
import com.kotlinspring.coursecatalogservice.repositories.CourseRepository
import com.kotlinspring.coursecatalogservice.repositories.InstructorRepository
import com.kotlinspring.coursecatalogservice.utils.PostgreSQLInitializer
import com.kotlinspring.coursecatalogservice.utils.courseEntityList
import com.kotlinspring.coursecatalogservice.utils.instructorEntity
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.util.UriComponentsBuilder
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
class CourseControllerIntegrationTest : PostgreSQLInitializer() {

    @Autowired
    lateinit var webTestClient: WebTestClient

    @Autowired
    lateinit var courseRepository: CourseRepository

    @Autowired
    lateinit var instructorRepository: InstructorRepository



    @BeforeEach
    fun setUp() {
        courseRepository.deleteAll()
        instructorRepository.deleteAll()
        val instructor = instructorEntity()
        val savedInstructor = instructorRepository.save(instructor)
        val courses = courseEntityList(savedInstructor)
        courseRepository.saveAll(courses)
    }


    @Test
    fun addCourse() {
        val instructor = instructorRepository.findAll().first()

        val courseDTO: CourseDTO = CourseDTO(null, "Introduction to Computer Science", "Design", instructor.id)
        val result  = webTestClient.post()
            .uri("/v1/courses")
            .bodyValue(courseDTO)
            .exchange()
            .expectStatus().isCreated
            .expectBody(CourseDTO::class.java)
            .returnResult()

        Assertions.assertEquals(courseDTO.name, result.responseBody?.name)
        Assertions.assertNotNull(result.responseBody?.id)
    }

    @Test
    fun retrieveAllCourses() {
        val courseDTOs = webTestClient.get()
            .uri("/v1/courses")
            .exchange()
            .expectStatus().isOk
            .expectBodyList(CourseDTO::class.java)
            .returnResult()
            .responseBody


        Assertions.assertEquals(3, courseDTOs?.size)

    }

    @Test
    fun retrieveAllCoursesByName() {
        val uri  = UriComponentsBuilder.fromUriString("/v1/courses").queryParam("course_name", "SpringBoot").toUriString()
        val courseDTOs = webTestClient.get()
            .uri(uri)
            .exchange()
            .expectStatus().isOk
            .expectBodyList(CourseDTO::class.java)
            .returnResult()
            .responseBody


        Assertions.assertEquals(2, courseDTOs?.size)

    }


    @Test
    fun updateCourse() {

        val instructor = instructorRepository.findAll().first()

        // persist a new course
        val course  =  Course(null,"Building RESTful APIs using Kotlin and Spring Boot", "Development", instructor)

        courseRepository.save(course)

        val updatedCourseDTO = CourseDTO(null, course.name, "Design", instructor!!.id)
        val updatedCourse  = webTestClient.put()
            .uri("/v1/courses/{course_id}", course.id)
            .bodyValue(updatedCourseDTO)
            .exchange()
            .expectStatus().isOk
            .expectBody(CourseDTO::class.java)
            .returnResult()
            .responseBody

        Assertions.assertEquals(updatedCourseDTO.category, updatedCourse?.category)
        Assertions.assertNotNull(updatedCourse?.id)
    }


    @Test
    fun deleteCourse() {

        val instructor = instructorRepository.findAll().first()

        // persist a new course
        val course  =  Course(null,"Building RESTful APIs using Kotlin and Spring Boot", "Development", instructor)

        courseRepository.save(course)

         webTestClient.delete()
            .uri("/v1/courses/{course_id}", course.id)
            .exchange()
            .expectStatus().isNoContent
    }
}