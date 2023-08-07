package com.kotlinspring.coursecatalogservice.controllers

import com.kotlinspring.coursecatalogservice.dto.CourseDTO
import com.kotlinspring.coursecatalogservice.entities.Course
import com.kotlinspring.coursecatalogservice.services.CourseService
import com.kotlinspring.coursecatalogservice.utils.courseDTOFunc
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.just
import io.mockk.runs
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody


@WebMvcTest(controllers = [CourseController::class])
@AutoConfigureWebTestClient
class CourseControllerUnitTest {

    @Autowired
    lateinit var webTestClient: WebTestClient

    @MockkBean
    lateinit var courseServiceMock: CourseService

    @Test
    fun addCourse() {

        val courseDTO: CourseDTO = CourseDTO(null, "Introduction to Computer Science", "Design", 1)

        every { courseServiceMock.addCourse(any()) } returns courseDTOFunc(id=1)

        val savedCourseDTO  = webTestClient.post()
            .uri("/v1/courses")
            .bodyValue(courseDTO)
            .exchange()
            .expectStatus().isCreated
            .expectBody(CourseDTO::class.java)
            .returnResult()
            .responseBody

        Assertions.assertNotNull(savedCourseDTO?.id)
    }

    @Test
    fun addCourseValidation() {
        val courseDTO: CourseDTO = CourseDTO(null, "", "", 1)

        every { courseServiceMock.addCourse(any()) } returns courseDTOFunc(id=1)

        val response  = webTestClient.post()
            .uri("/v1/courses")
            .bodyValue(courseDTO)
            .exchange()
            .expectStatus().isBadRequest
            .expectBody(String::class.java)
            .returnResult()
            .responseBody


        Assertions.assertEquals("courseDTO.category must not be blank, courseDTO.name must not be blank", response)
    }


    @Test
    fun addCourseRuntimeException() {
        val courseDTO: CourseDTO = CourseDTO(null, "Introduction to Computer Science", "Design", 1)

        val errorMessage = "unexpected error occurred"
        every { courseServiceMock.addCourse(any()) } throws RuntimeException(errorMessage)

        val response  = webTestClient.post()
            .uri("/v1/courses")
            .bodyValue(courseDTO)
            .exchange()
            .expectStatus().is5xxServerError
            .expectBody(String::class.java)
            .returnResult()
            .responseBody


        Assertions.assertEquals(errorMessage, response)
    }



    @Test
    fun retrieveAllCourses() {

        every { courseServiceMock.retrieveAllCourses(null) }.returnsMany (
            listOf(courseDTOFunc(id=1), courseDTOFunc(id=2, name="Build Reactive Microservices using Spring WebFlux/SpringBoot"))
        )

        val courseDTOs = webTestClient.get()
            .uri("/v1/courses")
            .exchange()
            .expectStatus().isOk
            .expectBodyList(CourseDTO::class.java)
            .returnResult()
            .responseBody


        Assertions.assertEquals(2, courseDTOs?.size)

    }



    @Test
    fun updateCourse() {
        // persist a new course

        val course  =  CourseDTO(null,"Building RESTful APIs using Kotlin and Spring Boot", "Development", 1)

        every { courseServiceMock.updateCourse(any(), any()) } returns courseDTOFunc(id=100, category = "Design")

        val updatedCourseDTO = CourseDTO(null, "Building RESTful APIs using Kotlin and Spring Boot", "Design")
        val updatedCourse  = webTestClient.put()
            .uri("/v1/courses/{course_id}", 100)
            .bodyValue(course)
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
        // persist a new course
        val course  =  Course(null,"Building RESTful APIs using Kotlin and Spring Boot", "Development")

        every { courseServiceMock.deleteCourse(any()) } just runs


        webTestClient.delete()
            .uri("/v1/courses/{course_id}", 100)
            .exchange()
            .expectStatus().isNoContent
    }
}