package com.pitzdev.sandbox.model.base

import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import javax.persistence.*

@MappedSuperclass
open class BaseEntity (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id : Long? = null,

    @Version
    val version: Long? = null,

    @field:CreationTimestamp
    val dateCreated: Instant? = null,

    @field:UpdateTimestamp
    val lastUpdated: Instant? = null,

    var deleted: Boolean = false
)
