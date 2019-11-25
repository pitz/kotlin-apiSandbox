package com.pitzdev.sandbox.controller

import com.pitzdev.sandbox.model.holder.Holder
import com.pitzdev.sandbox.services.HolderService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/holders")
class HolderController(private val holderService: HolderService) {

    @GetMapping("{id}")
    fun get(@PathVariable id: Long): Holder? {
        return holderService.get(id)
    }

    @GetMapping("/list")
    fun list(): List<Holder> {
        return holderService.list()
    }

    @PostMapping
    fun save(@RequestBody holder: Holder): Holder {
        return holderService.save(holder)
    }

    @DeleteMapping("{id}")
    fun delete(@PathVariable id: Long) {
        holderService.delete(id)
    }

    @PutMapping("{id}")
    fun update(@PathVariable id: Long, @RequestBody holder: Holder): Holder? {
        return holderService.update(id, holder)
    }
}