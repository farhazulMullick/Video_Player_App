package com.farhazulmullick.application.domain

import android.util.Log

abstract class Mapper<Domain, Dto, Entity> {
    private val TAG = "Mapper"

    // DTO and Domain Mapping
    abstract fun dtoToDomain(dto: Dto): Domain
    abstract fun domainToDto(domain: Domain): Dto
    fun dtoToDomain(dtoList: List<Dto>?): List<Domain> = try {
        (dtoList ?: emptyList()).map(::dtoToDomain)
    } catch (e: Exception) {
        Log.e(TAG,"dtoToDomain(dtoList: List<Dto>) ${this.javaClass}", e)
        emptyList()
    }

    fun domainToDto(domainList: List<Domain>?): List<Dto> = (domainList?: emptyList()).map(::domainToDto)

    fun dtoToDomainWithheaderDate(dtoList: List<Dto>): List<Domain> = dtoList.map(::dtoToDomain)

    // ****************** OPTIONAL USING ENTITY ***************************//
    open fun entityToDomain(entity: Entity): Domain =
        throw NotImplementedError("override and implement this method")

    open fun domainToEntity(domain: Domain): Entity =
        throw NotImplementedError("override and implement this method")

    fun entityToDomain(domainList: List<Entity>): List<Domain> = domainList.map(::entityToDomain)
    fun domainToEntity(domainList: List<Domain>): List<Entity> = domainList.map(::domainToEntity)

    // ****************** OPTIONAL IF NO RELATION B/W DTO AND ENTITY ***************//
    open fun entityToDto(entity: Entity): Dto =
        throw NotImplementedError("override and implement this method")

    open fun dtoToEntity(dto: Dto): Entity =
        throw NotImplementedError("override and implement this method")

    fun entityToDto(dtoList: List<Entity>): List<Dto> = dtoList.map(::entityToDto)
    fun dtoToEntity(dtoList: List<Dto>): List<Entity> = dtoList.map(::dtoToEntity)

}