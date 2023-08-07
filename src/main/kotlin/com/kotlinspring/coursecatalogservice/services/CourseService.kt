package com.kotlinspring.coursecatalogservice.services

import com.kotlinspring.coursecatalogservice.dto.CourseDTO
import com.kotlinspring.coursecatalogservice.entities.Course
import com.kotlinspring.coursecatalogservice.exceptions.CourseNotFoundException
import com.kotlinspring.coursecatalogservice.exceptions.InstructorNotValidException
import com.kotlinspring.coursecatalogservice.repositories.CourseRepository
import mu.KLogging
import org.springframework.stereotype.Service

@Service
class CourseService(val courseRepository: CourseRepository, val instructorService: InstructorService) {

    companion object: KLogging()

    fun addCourse(courseDTO: CourseDTO) : CourseDTO {

        val instructorOptional = instructorService.findByInstructorId(courseDTO.instructorId!!)

        if (! instructorOptional.isPresent) {
            throw InstructorNotValidException("Instructor not valid: ${courseDTO.instructorId}")
        }

        val courseEntity = courseDTO.let {
            Course(null, it.name, it.category, instructorOptional.get())
        }

        courseRepository.save(courseEntity)

        logger.info("save course $courseEntity")

        return courseEntity.let{
            CourseDTO(it.id, it.name, it.category, it.instructor!!.id)
        }
    }

    fun retrieveAllCourses(courseName:String?):List<CourseDTO> {

        val courses = courseName?.let {
            courseRepository.findCoursesByName(courseName)
        }?: courseRepository.findAll()

        return courses.map {
            CourseDTO(it.id, it.name, it.category)
        }
    }


    fun updateCourse(courseDTO: CourseDTO, courseId: Int) : CourseDTO{

        val existingCourse = courseRepository.findById(courseId)

        return if (existingCourse.isPresent) {
            existingCourse.get().let{
                it.name = courseDTO.name
                it.category = courseDTO.category
                courseRepository.save(it)
                CourseDTO(it.id, it.name, it.category)
            }
        }else {

            throw CourseNotFoundException("No course found for the passed in Id: $courseId")
        }


    }

    fun deleteCourse(courseId: Int){
        val existingCourse = courseRepository.findById(courseId)

        return if (existingCourse.isPresent) {
            existingCourse.get().let{

                courseRepository.deleteById(courseId)

            }
        }else {

            throw CourseNotFoundException("No course found for the passed in Id: $courseId")
        }
    }
}