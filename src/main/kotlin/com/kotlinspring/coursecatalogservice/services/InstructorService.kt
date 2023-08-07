package com.kotlinspring.coursecatalogservice.services

import com.kotlinspring.coursecatalogservice.dto.InstructorDTO
import com.kotlinspring.coursecatalogservice.entities.Instructor
import com.kotlinspring.coursecatalogservice.repositories.InstructorRepository
import mu.KLogging
import org.springframework.stereotype.Service
import java.util.Optional

@Service
class InstructorService(val instructorRepository: InstructorRepository) {

companion object: KLogging()

    fun createInstructor(instructorDTO: InstructorDTO):  InstructorDTO{
        val instructorEntity = Instructor(null, instructorDTO.name)

        instructorRepository.save(instructorEntity)

        logger.info("save instructor $instructorEntity")

        return instructorEntity.let{
            InstructorDTO(it.id, it.name)
        }
    }

    fun findByInstructorId(instructorId: Int): Optional<Instructor> {
        return instructorRepository.findById(instructorId)
    }
}