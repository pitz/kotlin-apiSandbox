package com.pitzdev.sandbox.model.holder

import com.pitzdev.sandbox.model.base.BaseEntity
import java.util.*
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.validation.constraints.NotNull

@Entity
data class Holder (
        @get: NotNull
        var name: String = "",

        @get: NotNull
        var cpfCnpj: String = "",

        @get: NotNull
        @GeneratedValue(strategy = GenerationType.SEQUENCE)
        var publicId: String = "",

        var externalId: String?,

        var birthDate: Date?,

        var email: String = "",

        var phone: String?,

        var mobilePhone: String?,

        var address: String = "",

        var addressNumber: Int?,

        var complement: String?,

        var postalCode: String = "",

        var province: String = "",

        var city: String = "",

        var state: String = "",

        var motherName: String = ""

) : BaseEntity()