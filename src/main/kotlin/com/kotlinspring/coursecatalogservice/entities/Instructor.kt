package com.kotlinspring.coursecatalogservice.entities

import jakarta.persistence.*

@Entity
@Table(name  = "instructors")
data class Instructor(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Int?,
    val name: String,

    @OneToMany(
        mappedBy = "instructor",
        cascade = [CascadeType.ALL],
        orphanRemoval = true
    )
    var courses:List<Course> = mutableListOf()
)