package com.kotlinspring.coursecatalogservice.controllers

import com.kotlinspring.coursecatalogservice.dto.CourseDTO
import com.kotlinspring.coursecatalogservice.dto.InstructorDTO
import com.kotlinspring.coursecatalogservice.services.InstructorService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.reactive.server.WebTestClient

@WebMvcTest(controllers = [InstructorController::class])
@AutoConfigureWebTestClient
class InstructorControllerUnitTest {

    @Autowired
    lateinit var webTestClient: WebTestClient

    @MockkBean
    lateinit var instructorServiceMock: InstructorService


    @Test
    fun createInstructor() {

        val instructorDTO  = InstructorDTO(null, "Samuel Paul")

        every { instructorServiceMock.createInstructor(any())} returns InstructorDTO(id=1, name = "Samuel Paul")

        val savedInstructorDTO  = webTestClient.post()
            .uri("/v1/instructors")
            .bodyValue(instructorDTO)
            .exchange()
            .expectStatus().isCreated
            .expectBody(InstructorDTO::class.java)
            .returnResult()
            .responseBody

        Assertions.assertNotNull(savedInstructorDTO?.id)
    }


    @Test
    fun createInstructorValidation() {

        val instructorDTO : InstructorDTO  = InstructorDTO(null, "")

        every { instructorServiceMock.createInstructor(any())} returns InstructorDTO(id=1, name = "Samuel Paul")

        val response  = webTestClient.post()
            .uri("/v1/instructors")
            .bodyValue(instructorDTO)
            .exchange()
            .expectStatus().isBadRequest
            .expectBody(String::class.java)
            .returnResult()
            .responseBody

        Assertions.assertEquals("instructorDTO.name must not be blank", response)
    }
}