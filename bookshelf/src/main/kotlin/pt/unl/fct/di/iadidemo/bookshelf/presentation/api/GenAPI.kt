package pt.unl.fct.di.iadidemo.bookshelf.presentation.api

import org.springframework.web.bind.annotation.*

interface GenAPI<S, T, U> { // S - InDTO, T - ListDTO, U - LongDTO

    @PostMapping
    fun addOne(@RequestBody elem: S): T;

    @GetMapping("{id}")
    fun getOne(@PathVariable id: Long): U

    @PutMapping("{id}")
    fun updateOne(@PathVariable id: Long, @RequestBody elem: S): T

    @DeleteMapping("{id}")
    fun deleteOne(@PathVariable id: Long): Unit;
}

