package org.acme.employeescheduling.domain

import ai.timefold.solver.core.api.domain.entity.PlanningEntity
import ai.timefold.solver.core.api.domain.lookup.PlanningId
import ai.timefold.solver.core.api.domain.variable.PlanningVariable
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit

@PlanningEntity
class Shift {
    @PlanningId
    private var id: String? = null

    private var start: LocalDateTime? = null
    private var end: LocalDateTime? = null

    private var location: String? = null
    private var requiredSkill: String? = null

    @PlanningVariable
    private var employee: Employee? = null

    constructor()

    @JvmOverloads
    constructor(
        start: LocalDateTime?,
        end: LocalDateTime?,
        location: String?,
        requiredSkill: String?,
        employee: Employee? = null
    ) : this(null, start, end, location, requiredSkill, employee)

    constructor(
        id: String?,
        start: LocalDateTime?,
        end: LocalDateTime?,
        location: String?,
        requiredSkill: String?,
        employee: Employee?
    ) {
        this.id = id
        this.start = start
        this.end = end
        this.location = location
        this.requiredSkill = requiredSkill
        this.employee = employee
    }

    fun getId(): String? {
        return id
    }

    fun setId(id: String?) {
        this.id = id
    }

    fun getStart(): LocalDateTime? {
        return start
    }

    fun setStart(start: LocalDateTime?) {
        this.start = start
    }

    fun getEnd(): LocalDateTime? {
        return end
    }

    fun setEnd(end: LocalDateTime?) {
        this.end = end
    }

    fun getLocation(): String? {
        return location
    }

    fun setLocation(location: String?) {
        this.location = location
    }

    fun getRequiredSkill(): String? {
        return requiredSkill
    }

    fun setRequiredSkill(requiredSkill: String?) {
        this.requiredSkill = requiredSkill
    }

    fun getEmployee(): Employee? {
        return employee
    }

    fun setEmployee(employee: Employee?) {
        this.employee = employee
    }

    fun isOverlappingWithDate(date: LocalDate?): Boolean {
        return getStart()!!.toLocalDate() == date || getEnd()!!.toLocalDate() == date
    }

    fun getOverlappingDurationInMinutes(date: LocalDate): Int {
        val startDateTime = LocalDateTime.of(date, LocalTime.MIN)
        val endDateTime = LocalDateTime.of(date, LocalTime.MAX)
        return getOverlappingDurationInMinutes(startDateTime, endDateTime, getStart(), getEnd())
    }

    private fun getOverlappingDurationInMinutes(
        firstStartDateTime: LocalDateTime, firstEndDateTime: LocalDateTime,
        secondStartDateTime: LocalDateTime?, secondEndDateTime: LocalDateTime?
    ): Int {
        val maxStartTime: LocalDateTime =
            (if (firstStartDateTime.isAfter(secondStartDateTime)) firstStartDateTime else secondStartDateTime)!!
        val minEndTime: LocalDateTime =
            (if (firstEndDateTime.isBefore(secondEndDateTime)) firstEndDateTime else secondEndDateTime)!!
        val minutes = maxStartTime.until(minEndTime, ChronoUnit.MINUTES)
        return if (minutes > 0) minutes.toInt() else 0
    }

    override fun toString(): String {
        return location + " " + start + "-" + end
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) {
            return true
        }
        if (o !is Shift) {
            return false
        }
        return getId() == o.getId()
    }

    override fun hashCode(): Int {
        return getId().hashCode()
    }
}
