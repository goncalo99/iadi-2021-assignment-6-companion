package pt.unl.fct.di.iadidemo.bookshelf.presentation.api

import org.springframework.web.bind.annotation.*

interface GenAPI<S, T, U> { // S - InDTO, T - ListDTO, U - LongDTO
    /*@GetMapping
    fun getAll():List<T>;*/

    @PostMapping
    fun addOne(@RequestBody elem: S): Long;

    @GetMapping("{id}")
    fun getOne(@PathVariable id: Long): U

    @PutMapping("{id}")
    fun updateOne(@PathVariable id: Long, @RequestBody elem: S): Unit

    @DeleteMapping("{id}")
    fun deleteOne(@PathVariable id: Long): Unit;
}

