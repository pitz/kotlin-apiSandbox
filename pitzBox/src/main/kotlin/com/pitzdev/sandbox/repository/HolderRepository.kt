package com.pitzdev.sandbox.repository

import com.pitzdev.sandbox.model.holder.Holder
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface HolderRepository : CrudRepository<Holder, Long>