package com.pitzdev.sandbox.services

import com.pitzdev.sandbox.repository.HolderRepository
import com.pitzdev.sandbox.model.holder.Holder
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class HolderService(private val holderRepository: HolderRepository) {

    fun get(id: Long): Holder? {
        return holderRepository.findByIdOrNull(id)
    }

    fun list(): List<Holder> {
        return holderRepository.findAll() as List<Holder>
    }

    fun save(holder: Holder): Holder {
        if (holder.cpfCnpj.isEmpty()) setError("O campo CPF/CNPJ é obrigatório.")
        if (holder.name.isEmpty()) setError("O campo nome é obrigatório.")

        return holderRepository.save(holder)
    }

    fun delete(id: Long) {
        var holder = get(id)

        if (holder != null) {
            holder.deleted = true
            holderRepository.save(holder)
        } else {
            setError("O portador informado não existe.")
        }
    }

    fun update(id: Long, holder: Holder): Holder? {
        var currentHolder = get(id)

        if (currentHolder != null) {
            currentHolder.phone = holder.phone
            currentHolder.email = holder.email
            currentHolder.address = holder.address
            currentHolder.addressNumber = holder.addressNumber
            currentHolder.city = holder.city
            currentHolder.complement = holder.complement
            holderRepository.save(currentHolder)
        } else {
            setError("O portador informado não existe.")
        }

        return currentHolder
    }

    private fun setError(message: String): Unit = throw IllegalArgumentException(message)
}